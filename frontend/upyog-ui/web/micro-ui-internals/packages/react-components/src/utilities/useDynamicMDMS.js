/**
 * useDynamicMDMS
 *
 * Generic dropdown/radio data hook for config-driven forms (DynamicForm).
 * Reads the form config, finds every dropdown AND radio field, and resolves
 * options by the field's `dataSource`:
 *
 *   1. dataSource.type === "MDMS" → batched into ONE egov-mdms-service
 *      _search call at STATE level, grouped by moduleName.
 *      Optional dataSource.filter (e.g. { assetClassification: "IMMOVABLE" })
 *      narrows options after fetch.
 *   2. moduleName "egov-location" / masterName "TenantBoundary" → LOCALITY,
 *      fetched from CITY-tenant MDMS boundary-data
 *      (e.g. pg.citya → data/pg/citya/egov-location/boundary-data.json)
 *      and flattened to leaf Locality nodes.
 *   3. dataSource.defaultValueSource === "tenantId" → CITY: single option,
 *      the current tenant.
 *
 * Returns { dropdownData, isLoading } where dropdownData is keyed by
 * field.name ("assetType", "serviceType", "city").
 */

import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
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
 * Walk City → Zone → Ward/Block → Locality tree from MDMS TenantBoundary
 * and collect leaf locality nodes (label === "Locality").
 */
const flattenBoundaryNodes = (nodes, out = []) => {
  const list = Array.isArray(nodes) ? nodes : nodes ? [nodes] : [];
  list.forEach((node) => {
    if (!node || typeof node !== "object") return;
    const kids = node.children;
    const label = String(node.label || "").toLowerCase();
    if (label === "locality" && node.code) {
      out.push(node);
    }
    if (Array.isArray(kids) && kids.length) {
      flattenBoundaryNodes(kids, out);
    }
  });
  return out;
};

const localityI18nKey = (cityTenantId, hierarchyCode, localityCode) =>
  `${String(cityTenantId || "")
    .replace(/\./g, "_")
    .toUpperCase()}_${String(hierarchyCode || "REVENUE").toUpperCase()}_${localityCode}`;

const extractLocalitiesFromMdms = (mdmsRes, cityTenantId) => {
  const boundaries = mdmsRes?.["egov-location"]?.TenantBoundary;
  if (!Array.isArray(boundaries) || !boundaries.length) return [];

  const revenue =
    boundaries.find(
      (tb) => String(tb?.hierarchyType?.code || "").toUpperCase() === "REVENUE"
    ) || boundaries[0];

  const hierarchyCode = revenue?.hierarchyType?.code || "REVENUE";
  const root = revenue?.boundary;
  const leaves = flattenBoundaryNodes(root);

  return leaves.map((loc) => ({
    ...loc,
    code: loc.code,
    name: loc.name || loc.localname || loc.code,
    i18nKey:
      loc.i18nkey ||
      loc.i18nKey ||
      localityI18nKey(cityTenantId, hierarchyCode, loc.code),
  }));
};

/**
 * Tenant ids are case-sensitive for MDMS / location APIs (pg.citya ≠ PG.CITYA).
 * Do NOT use optionCode() here — that uppercases values for form matching.
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

  /* 1 ─ State-level MDMS masters (AssetType, etc.) */
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

  const mdmsService = Digit?.MDMSService || Digit?.MdmsService;

  const {
    data: mdmsRes,
    isLoading: isMdmsLoading,
    isError: isMdmsError,
    error: mdmsError,
  } = useQuery({
    queryKey: ["DYNAMIC_FORM_MDMS", stateId, JSON.stringify(moduleDetails)],
    queryFn: () => mdmsService.call(stateId, { moduleDetails }),
    enabled: !!stateId && moduleDetails.length > 0 && !!mdmsService,
    staleTime: Infinity,
    select: (data) => data?.MdmsRes || data,
  });

  if (isMdmsError) {
    // eslint-disable-next-line no-console
    console.error("useDynamicMDMS: MDMS fetch failed", mdmsError);
  }

  /* 2 ─ City-tenant localities (pg.citya boundary-data → leaf Locality nodes)
   * Prefer location API (flat Locality list), fall back to MDMS TenantBoundary tree. */
  const {
    data: cityLocalities = [],
    isLoading: isBoundaryLoading,
    isError: isBoundaryError,
    error: boundaryError,
  } = useQuery({
    queryKey: ["DYNAMIC_FORM_TENANT_BOUNDARY", cityTenantId],
    queryFn: async () => {
      const LocationService = Digit?.LocationService;
      const LocalityService = Digit?.LocalityService;

      if (LocationService?.getRevenueLocalities && LocalityService?.get) {
        try {
          const response = await LocationService.getRevenueLocalities(cityTenantId);
          const tenantBoundary = response?.TenantBoundary?.[0];
          if (Array.isArray(tenantBoundary?.boundary) && tenantBoundary.boundary.length) {
            return LocalityService.get(tenantBoundary).map((loc) => ({
              ...loc,
              code: loc.code,
              name: loc.name || loc.localname || loc.code,
              i18nKey: loc.name || loc.localname || loc.i18nkey || loc.code,
            }));
          }
        } catch (err) {
          // eslint-disable-next-line no-console
          console.warn("useDynamicMDMS: location localities failed, trying MDMS", err);
        }
      }

      if (!mdmsService?.call) return [];
      const data = await mdmsService.call(cityTenantId, {
        moduleDetails: [
          {
            moduleName: "egov-location",
            masterDetails: [{ name: "TenantBoundary" }],
          },
        ],
      });
      return extractLocalitiesFromMdms(data?.MdmsRes || data, cityTenantId).map((loc) => ({
        ...loc,
        i18nKey: loc.name || loc.localname || loc.i18nKey || loc.code,
      }));
    },
    enabled: localityFields.length > 0 && !!cityTenantId,
    staleTime: Infinity,
  });

  if (isBoundaryError) {
    // eslint-disable-next-line no-console
    console.error("useDynamicMDMS: locality fetch failed", boundaryError);
  }

  /* 3 ─ Assemble dropdownData keyed by field name. */
  const dropdownData = useMemo(() => {
    const result = {};

    mdmsFields.forEach((f) => {
      const { moduleName, masterName, filter } = f.dataSource;
      result[f.name] = toOptions(mdmsRes?.[moduleName]?.[masterName], f.code, filter);
    });

    localityFields.forEach((f) => {
      result[f.name] = cityTenantId && Array.isArray(cityLocalities) ? cityLocalities : [];
    });

    cityFields.forEach((f) => {
      result[f.name] = tenantId
        ? [
            {
              code: tenantId,
              name: t ? t(tenantI18nKey(tenantId)) : tenantId,
              i18nKey: tenantI18nKey(tenantId),
            },
          ]
        : [];
    });

    return result;
  }, [
    mdmsFields,
    localityFields,
    cityFields,
    mdmsRes,
    cityLocalities,
    tenantId,
    cityTenantId,
    t,
  ]);

  return {
    dropdownData,
    isLoading:
      (moduleDetails.length > 0 && isMdmsLoading) ||
      (localityFields.length > 0 && !!cityTenantId && isBoundaryLoading),
    cityTenantId,
  };
};

export default useDynamicMDMS;
