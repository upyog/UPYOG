/**
 * useDynamicMDMS
 *
 * Thin adapter for config-driven DynamicForm dropdowns.
 * Does NOT reimplement MDMS / locality / city fetching — it wires form
 * `dataSource` config to existing Digit hooks/services:
 *
 *   1. dataSource.type === "MDMS"
 *      → Digit.Hooks.useSelectedMDMS(...).getMultipleTypesWithFilter
 *        (same path as Digit.Hooks.useCustomMDMS / useEnabledMDMS)
 *   2. egov-location / TenantBoundary
 *      → Digit.Hooks.useBoundaryLocalities (LocationService + LocalityService)
 *   3. dataSource.defaultValueSource === "tenantId"
 *      → Digit.Hooks.useTenants (initData tenants) filtered to current tenant
 *
 * Returns { dropdownData, isLoading } keyed by field.name.
 */

import { useMemo } from "react";
import { useQueries } from "@tanstack/react-query";
import { flattenFormConfig } from "./formUtils";

/* ── helpers ──────────────────────────────────────────────────────────── */

const OPTION_TYPES = ["dropdown", "radio"];

const isLocalityField = (ds) =>
  ds?.moduleName === "egov-location" || ds?.masterName === "TenantBoundary";

const isCityField = (ds) => ds?.defaultValueSource === "tenantId";

const tenantI18nKey = (tenantId) =>
  `TENANT_TENANTS_${String(tenantId).replace(".", "_").toUpperCase()}`;

const applyMdmsFilter = (list, filter) => {
  if (!filter || typeof filter !== "object" || !Object.keys(filter).length) {
    return Array.isArray(list) ? list : [];
  }
  return (Array.isArray(list) ? list : []).filter((item) =>
    Object.entries(filter).every(([key, expected]) => {
      if (expected == null || expected === "") return true;
      return String(item?.[key] ?? "").toUpperCase() === String(expected).toUpperCase();
    })
  );
};

const toOptions = (list, fieldCode, filter) =>
  applyMdmsFilter(list, filter)
    .filter((item) => item?.active !== false)
    .map((item) => ({
      ...item,
      code: item.code,
      name: item.name || item.code,
      i18nKey: item.i18nKey || item.labelKey || item.name || `${fieldCode}_${item.code}`,
    }));

/**
 * Tenant ids are case-sensitive for location APIs (pg.citya ≠ PG.CITYA).
 * Do NOT uppercase — MDMS / boundary lookup depends on exact casing.
 */
const resolveCityTenantId = (cityValue, fallbackTenantId) => {
  let fromCity = "";
  if (cityValue && typeof cityValue === "object" && cityValue.code != null) {
    fromCity = String(cityValue.code).trim();
  } else if (typeof cityValue === "string") {
    fromCity = cityValue.trim();
  }
  return String(fromCity || fallbackTenantId || "").trim();
};

const normalizeLocalityOptions = (list = []) =>
  (Array.isArray(list) ? list : []).map((loc) => ({
    ...loc,
    code: loc.code,
    name: loc.name || loc.localname || loc.code,
    // useBoundaryLocalities may set lowercase i18nkey (already translated via t)
    i18nKey: loc.i18nKey || loc.i18nkey || loc.name || loc.localname || loc.code,
  }));

/* ── hook ─────────────────────────────────────────────────────────────── */

const useDynamicMDMS = (form = [], stateId, tenantId, t, options = {}) => {
  const cityTenantId = resolveCityTenantId(options.city, tenantId);

  const { mdmsFields, localityFields, cityFields } = useMemo(() => {
    const mdms = [];
    const locality = [];
    const city = [];
    flattenFormConfig(form).forEach((fc) => {
      const f = fc?.field;
      if (!f || !OPTION_TYPES.includes(f.type)) return;
      const ds = f.dataSource;
      if (!ds) return;
      if (isCityField(ds)) city.push(f);
      else if (isLocalityField(ds)) locality.push(f);
      else if (ds.type === "MDMS") mdms.push(f);
    });
    return { mdmsFields: mdms, localityFields: locality, cityFields: city };
  }, [form]);

  /* 1 ─ State-level MDMS masters via existing Digit MDMS hooks/services */
  const moduleDetails = useMemo(() => {
    const byModule = {};
    mdmsFields.forEach(({ dataSource: { moduleName, masterName } }) => {
      (byModule[moduleName] = byModule[moduleName] || new Set()).add(masterName);
    });
    return Object.entries(byModule).map(([moduleName, masters]) => ({
      moduleName,
      masterDetails: [...masters].map((name) => ({ name })),
    }));
  }, [mdmsFields]);

  // Same queryKey + service path as Digit.Hooks.useCustomMDMS / useEnabledMDMS.
  const mdmsQueries = useQueries({
    queries: moduleDetails.map(({ moduleName, masterDetails }) => ({
      queryKey: [stateId, moduleName, masterDetails],
      queryFn: () => {
        // Prefer useSelectedMDMS (V1/V2) — same as useEnabledMDMS.
        const selected = Digit?.Hooks?.useSelectedMDMS?.(moduleName);
        if (selected?.getMultipleTypesWithFilter) {
          return selected.getMultipleTypesWithFilter(stateId, moduleName, masterDetails);
        }
        const mdmsService = Digit?.MDMSService || Digit?.MdmsService;
        return mdmsService.getMultipleTypesWithFilter(stateId, moduleName, masterDetails);
      },
      enabled: !!stateId && masterDetails.length > 0,
      staleTime: Infinity,
    })),
  });

  const isMdmsLoading = mdmsQueries.some((q) => q.isLoading);
  // Stable scalar so useMemo deps don't change length across module counts.
  const mdmsDataVersion = mdmsQueries.map((q) => q.dataUpdatedAt ?? 0).join("|");

  const mdmsRes = useMemo(() => {
    const merged = {};
    moduleDetails.forEach(({ moduleName }, index) => {
      const data = mdmsQueries[index]?.data;
      if (!data) return;
      // useCustomMDMS returns MdmsRes (module-keyed) or the module slice.
      if (data[moduleName]) {
        merged[moduleName] = { ...(merged[moduleName] || {}), ...data[moduleName] };
      } else {
        merged[moduleName] = { ...(merged[moduleName] || {}), ...data };
      }
    });
    return merged;
    // mdmsQueries is read inside; mdmsDataVersion tracks when query data changes.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [moduleDetails, mdmsDataVersion]);

  /* 2 ─ Localities via existing Digit.Hooks.useBoundaryLocalities */
  const {
    data: boundaryLocalities,
    isLoading: isBoundaryLoading,
  } = Digit.Hooks.useBoundaryLocalities(
    cityTenantId,
    "revenue",
    {
      enabled: localityFields.length > 0 && !!cityTenantId,
    },
    t
  );

  /* 3 ─ Cities via existing Digit.Hooks.useTenants */
  const { data: tenants = [], isLoading: isTenantsLoading } = Digit.Hooks.useTenants();

  /* 4 ─ Assemble dropdownData keyed by field name */
  const dropdownData = useMemo(() => {
    const result = {};

    mdmsFields.forEach((f) => {
      const { moduleName, masterName, filter } = f.dataSource;
      result[f.name] = toOptions(mdmsRes?.[moduleName]?.[masterName], f.code, filter);
    });

    localityFields.forEach((f) => {
      result[f.name] =
        cityTenantId && Array.isArray(boundaryLocalities)
          ? normalizeLocalityOptions(boundaryLocalities)
          : [];
    });

    cityFields.forEach((f) => {
      if (!tenantId) {
        result[f.name] = [];
        return;
      }
      const fromTenants = (Array.isArray(tenants) ? tenants : []).find(
        (item) => String(item?.code || "").toLowerCase() === String(tenantId).toLowerCase()
      );
      result[f.name] = [
        {
          ...(fromTenants || {}),
          code: tenantId,
          name: fromTenants?.name || (t ? t(tenantI18nKey(tenantId)) : tenantId),
          i18nKey: fromTenants?.i18nKey || tenantI18nKey(tenantId),
        },
      ];
    });

    return result;
  }, [
    mdmsFields,
    localityFields,
    cityFields,
    mdmsRes,
    boundaryLocalities,
    tenants,
    tenantId,
    cityTenantId,
    t,
  ]);

  return {
    dropdownData,
    isLoading:
      (moduleDetails.length > 0 && isMdmsLoading) ||
      (localityFields.length > 0 && !!cityTenantId && isBoundaryLoading) ||
      (cityFields.length > 0 && isTenantsLoading),
    cityTenantId,
  };
};

export default useDynamicMDMS;
