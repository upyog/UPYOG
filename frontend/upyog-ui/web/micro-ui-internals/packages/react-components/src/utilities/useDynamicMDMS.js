/**
 * useDynamicMDMS.js
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
 * Typical usage (inside DynamicForm / DynamicFormStep)
 * ----------------------------------------------------
 *   const { dropdownData, isLoading, cityTenantId } = useDynamicMDMS(
 *     form,
 *     stateId,
 *     tenantId,
 *     t,
 *     { city: formValues.city }
 *   );
 *
 * Params
 * ------
 * @param {Array<object>}  form       Flattened or nested form config array from routeConfig.
 * @param {string}         stateId    State-level tenant id for MDMS queries (e.g. "pb").
 * @param {string}         tenantId   Current ULB tenant id for city / locality resolution.
 * @param {Function}       t          i18n translator; used for tenant and locality labels.
 * @param {object}         [options]  Hook options:
 *                                    - city: selected city field value (object or string code).
 *
 * Returns
 * -------
 * @returns {{ dropdownData: object, isLoading: boolean, cityTenantId: string }}
 *   dropdownData — map of field.name → option arrays for dropdown/radio fields.
 *   isLoading    — true while any underlying MDMS / boundary / tenant query is in flight.
 *   cityTenantId — resolved tenant id used for locality boundary lookups.
 *
 * @see DynamicForm
 * @see DynamicFormStep
 */

import { useMemo } from "react";
import { useQueries } from "@tanstack/react-query";
import { flattenFormConfig } from "./formUtils";

/* ── helpers ──────────────────────────────────────────────────────────── */

/** Field types that consume dropdown option lists from this hook. */
const OPTION_TYPES = ["dropdown", "radio"];

/**
 * Detects locality dropdown fields backed by egov-location / TenantBoundary.
 * These fields are resolved via Digit.Hooks.useBoundaryLocalities rather than MDMS.
 *
 * @param {object} [ds] - Field `dataSource` config from form metadata.
 * @returns {boolean} True when the field should load revenue-boundary localities.
 */
const isLocalityField = (ds) =>
  ds?.moduleName === "egov-location" || ds?.masterName === "TenantBoundary";

/**
 * Detects city dropdown fields whose default value comes from the current tenant.
 * These fields are resolved via Digit.Hooks.useTenants (initData) rather than MDMS.
 *
 * @param {object} [ds] - Field `dataSource` config from form metadata.
 * @returns {boolean} True when `dataSource.defaultValueSource === "tenantId"`.
 */
const isCityField = (ds) => ds?.defaultValueSource === "tenantId";

/**
 * Builds the standard i18n key for a tenant display name.
 * Dots in tenant ids (e.g. "pg.citya") are replaced with underscores and uppercased.
 *
 * @param {string} tenantId - ULB tenant id (case-sensitive for APIs; key is normalized).
 * @returns {string} i18n key such as `TENANT_TENANTS_PG_CITYA`.
 */
const tenantI18nKey = (tenantId) =>
  `TENANT_TENANTS_${String(tenantId).replace(".", "_").toUpperCase()}`;

/**
 * Applies a config-driven equality filter to an MDMS master list.
 * Each filter entry is matched case-insensitively against the corresponding item property.
 * Null or empty expected values are treated as "match any" (no constraint on that key).
 *
 * @param {Array<object>} [list]   - Raw MDMS master rows.
 * @param {object}        [filter] - Map of property name → expected value from dataSource.filter.
 * @returns {Array<object>} Filtered list; empty array when input is not an array or filter is absent.
 */
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

/**
 * Converts raw MDMS master rows into DynamicForm dropdown options.
 * Inactive rows (`active === false`) are excluded. Each option receives normalized
 * `code`, `name`, and `i18nKey` fields expected by dropdown/radio renderers.
 *
 * @param {Array<object>} [list]      - Raw MDMS master rows (pre- or post-filter).
 * @param {string}        fieldCode   - Form field code; used as i18nKey prefix fallback.
 * @param {object}        [filter]    - Optional dataSource.filter passed to applyMdmsFilter.
 * @returns {Array<object>} Option objects with code, name, i18nKey, and spread source fields.
 */
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
 * Resolves the tenant id used for locality boundary lookups from a city field value.
 * Tenant ids are case-sensitive for location APIs (pg.citya ≠ PG.CITYA).
 * Do NOT uppercase — MDMS / boundary lookup depends on exact casing.
 *
 * @param {object|string} [cityValue]       - Selected city (object with `.code` or raw string).
 * @param {string}        [fallbackTenantId] - Current ULB tenant when city is unset.
 * @returns {string} Trimmed tenant id string; empty when neither source is available.
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

/**
 * Normalizes locality rows from useBoundaryLocalities into DynamicForm option shape.
 * Handles alternate property names (`localname`, lowercase `i18nkey`) returned by boundary APIs.
 *
 * @param {Array<object>} [list] - Locality rows from Digit.Hooks.useBoundaryLocalities.
 * @returns {Array<object>} Option objects with code, name, i18nKey, and spread source fields.
 */
const normalizeLocalityOptions = (list = []) =>
  (Array.isArray(list) ? list : []).map((loc) => ({
    ...loc,
    code: loc.code,
    name: loc.name || loc.localname || loc.code,
    // useBoundaryLocalities may set lowercase i18nkey (already translated via t)
    i18nKey: loc.i18nKey || loc.i18nkey || loc.name || loc.localname || loc.code,
  }));

/* ── hook ─────────────────────────────────────────────────────────────── */

/**
 * Fetches and assembles dropdown option data for config-driven DynamicForm fields.
 * Scans the form config for dropdown/radio fields, groups them by data source type,
 * and delegates fetching to existing Digit MDMS, boundary, and tenant hooks.
 *
 * @param {Array<object>}  form       - Form config array (flattened internally via flattenFormConfig).
 * @param {string}         stateId    - State tenant id for MDMS module queries.
 * @param {string}         tenantId   - Current ULB tenant id for city fields and locality fallback.
 * @param {Function}       t          - i18n translator for tenant display names.
 * @param {object}         [options]  - Hook options; `options.city` drives locality tenant resolution.
 * @returns {{ dropdownData: object, isLoading: boolean, cityTenantId: string }}
 *   dropdownData — `{ [fieldName]: option[] }` keyed by each dropdown/radio field name.
 *   isLoading    — aggregate loading flag across MDMS, boundary, and tenant queries.
 *   cityTenantId — tenant id passed to useBoundaryLocalities (from city selection or tenantId).
 */
const useDynamicMDMS = (form = [], stateId, tenantId, t, options = {}) => {
  /** Tenant id for revenue-boundary locality lookups (selected city or current ULB). */
  const cityTenantId = resolveCityTenantId(options.city, tenantId);

  /**
   * Scans flattened form config and partitions dropdown/radio fields by data source:
   * MDMS masters, egov-location localities, or tenant-derived city fields.
   *
   * @returns {{ mdmsFields: Array, localityFields: Array, cityFields: Array }}
   */
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

  /* ── MDMS queries ───────────────────────────────────────────────────── */

  /**
   * Groups MDMS fields by moduleName and builds masterDetails for batched queries.
   * One react-query entry per module; each fetches all required masters in a single call.
   */
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

  /**
   * Parallel MDMS fetches via react-query.
   * Same queryKey + service path as Digit.Hooks.useCustomMDMS / useEnabledMDMS.
   * Prefers Digit.Hooks.useSelectedMDMS when available; falls back to MDMSService directly.
   */
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
  /** Stable scalar so useMemo deps don't change length across module counts. */
  const mdmsDataVersion = mdmsQueries.map((q) => q.dataUpdatedAt ?? 0).join("|");

  /**
   * Merges per-module MDMS query results into a single `{ [moduleName]: { [masterName]: rows } }` map.
   * Handles both module-keyed responses (MdmsRes) and flat module slices from the service.
   */
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

  /* ── Localities ─────────────────────────────────────────────────────── */

  /**
   * Revenue-boundary localities for the resolved cityTenantId.
   * Enabled only when the form has locality fields and a tenant id is available.
   * Delegates to Digit.Hooks.useBoundaryLocalities (LocationService + LocalityService).
   */
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

  /* ── Cities ─────────────────────────────────────────────────────────── */

  /**
   * Init-data tenant list for city dropdown fields (`defaultValueSource === "tenantId"`).
   * The current tenantId is matched (case-insensitive) to produce a single-option dropdown.
   */
  const { data: tenants = [], isLoading: isTenantsLoading } = Digit.Hooks.useTenants();

  /* ── Assemble dropdownData ──────────────────────────────────────────── */

  /**
   * Builds the final `{ [fieldName]: option[] }` map consumed by DynamicForm dropdowns.
   * MDMS fields → toOptions from merged mdmsRes.
   * Locality fields → normalizeLocalityOptions from boundaryLocalities (empty when no city).
   * City fields → single tenant option with i18n fallback when tenants list lacks a match.
   */
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
