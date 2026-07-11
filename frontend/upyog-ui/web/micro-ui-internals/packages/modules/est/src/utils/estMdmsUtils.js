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
