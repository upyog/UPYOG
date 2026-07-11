import { mergeRouteConfig } from "@nudmcdgnpm/digit-ui-react-components";
import LOCAL_SEARCH_CONFIG from "../config/searchApplicationConfig";

const pickFirstMdmsEntry = (data) => {
  if (!data) return null;
  if (Array.isArray(data)) return data[0] || null;
  if (Array.isArray(data.body)) return data.body[0] || null;
  return data;
};

/**
 * Build search page config from MDMS Estate.searchApplicationConfig,
 * falling back to local searchApplicationConfig.js when MDMS is empty.
 */
export const resolveSearchApplicationConfig = (mdmsData, override = {}) => {
  const entry = pickFirstMdmsEntry(mdmsData);
  const local = { ...LOCAL_SEARCH_CONFIG, ...override };

  if (!entry) return local;

  const mdmsRoute =
    entry.routeConfig ||
    (Array.isArray(entry.form) ? entry : null);

  const routeConfig = mdmsRoute
    ? mergeRouteConfig(mdmsRoute, local.routeConfig || {})
    : local.routeConfig;

  return {
    header: entry.header || local.header,
    sessionKey: entry.sessionKey || local.sessionKey,
    assignRoute: entry.assignRoute || local.assignRoute,
    emptyState: { ...local.emptyState, ...(entry.emptyState || {}) },
    table: { ...local.table, ...(entry.table || {}) },
    paginationDefaults: {
      ...local.paginationDefaults,
      ...(entry.paginationDefaults || {}),
    },
    routeConfig,
  };
};

/** Resolve locality label from asset row (MDMS codes, objects, or plain strings). */
export const resolveLocalityDisplay = (asset, t = (k) => k) => {
  if (!asset) return "";
  const locObj = asset.locality || asset.address?.locality;
  if (typeof locObj === "string") {
    if (locObj.startsWith("TENANT_") || locObj.startsWith("EST_")) return t(locObj);
    return locObj;
  }
  if (locObj && typeof locObj === "object") {
    if (locObj.i18nKey) return t(locObj.i18nKey);
    return locObj.label || locObj.name || locObj.code || "";
  }
  const candidates = [asset.localityName, asset.localityCode, asset.serviceType];
  const raw = candidates.find((v) => v !== undefined && v !== null && v !== "");
  if (!raw) return "";
  if (typeof raw === "string" && (raw.startsWith("TENANT_") || raw.startsWith("EST_"))) {
    return t(raw);
  }
  return raw;
};
