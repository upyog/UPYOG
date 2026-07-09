/**
 * useDynamicMDMS
 *
 * Generic dropdown/radio data hook for config-driven forms (DynamicForm).
 * Reads the form config, finds every dropdown AND radio field, and resolves
 * options by the field's `dataSource`:
 *
 *   1. dataSource.type === "MDMS" → batched into ONE egov-mdms-service
 *      _search call, grouped by moduleName (assetType and anything added later).
 *   2. moduleName "egov-location" / masterName "TenantBoundary" → LOCALITY,
 *      fetched at the CITY tenant level via Digit.Hooks.useBoundaryLocalities.
 *   3. dataSource.defaultValueSource === "tenantId" → CITY: single option,
 *      the current tenant.
 *
 * Returns { dropdownData, isLoading } where dropdownData is keyed by
 * field.name ("assetType", "serviceType", "city") — the same key
 * DynamicFormField.resolveOptions looks up.
 */

import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { flattenFormConfig } from "./formUtils";

/* ── helpers ──────────────────────────────────────────────────────────── */

const OPTION_TYPES = ["dropdown", "radio"]; // radio can be MDMS-backed too

const isLocalityField = (ds) =>
  ds?.moduleName === "egov-location" || ds?.masterName === "TenantBoundary";

const isCityField = (ds) => ds?.defaultValueSource === "tenantId";

const tenantI18nKey = (tenantId) =>
  `TENANT_TENANTS_${tenantId.replace(".", "_").toUpperCase()}`;

// Generic MDMS master → dropdown options
const toOptions = (list, fieldCode) =>
  (Array.isArray(list) ? list : [])
    .filter((item) => item?.active !== false)
    .map((item) => ({
      ...item,
      code: item.code,
      name: item.name || item.code,
      // e.g. EST_ASSET_TYPE_LAND — add these keys in localization.
      // DynamicFormField falls back to `name` while untranslated.
      i18nKey: `${fieldCode}_${item.code}`,
    }));

/* ── hook ─────────────────────────────────────────────────────────────── */

const useDynamicMDMS = (form = [], stateId, tenantId, t) => {
  // Split option-bearing fields into the three categories.
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

  /* 1 ─ Batched MDMS request: group masters by module, one network call. */
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

  // MdmsService.call expects { moduleDetails: [...] } — it spreads this into
  // MdmsCriteria; passing the raw array yields a malformed request.
  const mdmsService = Digit?.MDMSService || Digit?.MdmsService;

  const {
    data: mdmsRes,
    isLoading: isMdmsLoading,
    isError: isMdmsError,
    error: mdmsError,
  } = useQuery({
    queryKey: ["DYNAMIC_FORM_MDMS", stateId, JSON.stringify(moduleDetails)],
    queryFn: () => mdmsService.call(stateId, { moduleDetails }),
    enabled: !!stateId && moduleDetails.length > 0,
    staleTime: Infinity,
    select: (data) => data?.MdmsRes || data,
  });

  // v5 removed onError from useQuery — log failures so an empty dropdown is
  // never silent.
  if (isMdmsError) {
    // eslint-disable-next-line no-console
    console.error("useDynamicMDMS: MDMS fetch failed", mdmsError);
  }

  /* 2 ─ Localities (TenantBoundary) — city-tenant level, revenue hierarchy. */
  const { data: localities, isLoading: isLocalityLoading } =
    Digit.Hooks.useBoundaryLocalities(
      tenantId,
      "revenue",
      { enabled: localityFields.length > 0 && !!tenantId },
      t
    );

  /* 3 ─ Assemble dropdownData keyed by field name. */
  const dropdownData = useMemo(() => {
    const result = {};

    mdmsFields.forEach((f) => {
      const { moduleName, masterName } = f.dataSource;
      result[f.name] = toOptions(mdmsRes?.[moduleName]?.[masterName], f.code);
    });

    localityFields.forEach((f) => {
      result[f.name] = (localities || []).map((loc) => ({
        ...loc,
        i18nKey: loc.i18nkey || loc.i18nKey || loc.name,
      }));
    });

    cityFields.forEach((f) => {
      result[f.name] = tenantId
        ? [{
            code: tenantId,
            name: t ? t(tenantI18nKey(tenantId)) : tenantId,
            i18nKey: tenantI18nKey(tenantId),
          }]
        : [];
    });

    return result;
  }, [mdmsFields, localityFields, cityFields, mdmsRes, localities, tenantId, t]);

  return {
    dropdownData,
    isLoading:
      (moduleDetails.length > 0 && isMdmsLoading) ||
      (localityFields.length > 0 && isLocalityLoading),
  };
};

export default useDynamicMDMS;
