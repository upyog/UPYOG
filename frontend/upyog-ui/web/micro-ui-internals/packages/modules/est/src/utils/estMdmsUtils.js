import { mergeRouteConfig } from "@nudmcdgnpm/digit-ui-react-components";
import LOCAL_PAYMENT_HISTORY_CONFIG from "../config/paymentHistoryConfig";

/** Offline fallback when Estate.SearchApplicationConfig MDMS is empty. */
const LOCAL_SEARCH_CONFIG = {
  header: "EST_SEARCH_APPLICATIONS",
  sessionKey: "EST_ASSIGN_ASSETS",
  assignRoute: "assignassets",
  emptyState: {
    actionLabel: "EST_CREATE_ASSET",
  },
  table: {
    showAssetRef: true,
    actions: "allot",
  },
  paginationDefaults: {
    offset: 0,
    limit: 10,
    sortBy: "createdDate",
    sortOrder: "DESC",
  },
  routeConfig: {
    key: "SearchFilters",
    payloadKey: "Filters",
    searchLayout: "inline",
    actionButton: {
      text: {
        create: "ES_COMMON_SEARCH",
        clear: "ES_COMMON_CLEAR_ALL",
      },
    },
    form: [
      {
        order: 1,
        key: "EST_SEARCH_ASSET_NUMBER",
        field: {
          code: "EST_SEARCH_ASSET_NUMBER",
          name: "estateNo",
          type: "text",
          placeholder: "EST_SEARCH_ASSET_NUMBER",
        },
        validation: { required: false },
      },
      {
        order: 2,
        key: "EST_LOCALITY",
        apiFieldName: "localityCode",
        field: {
          code: "EST_LOCALITY",
          name: "locality",
          type: "dropdown",
          placeholder: "EST_SELECT_LOCALITY",
          dataSource: {
            type: "MDMS",
            moduleName: "egov-location",
            masterName: "TenantBoundary",
            customiztionRequired: true,
          },
          optionCardStyles: { overflowY: "auto", maxHeight: "300px" },
        },
        validation: { required: false },
      },
      {
        order: 3,
        key: "EST_ASSET_TYPE",
        apiFieldName: "assetParentCategory",
        field: {
          code: "EST_ASSET_TYPE",
          name: "assetType",
          type: "dropdown",
          placeholder: "EST_SELECT_ASSET_TYPE",
          dataSource: {
            type: "MDMS",
            moduleName: "ASSET",
            masterName: "assetParentCategory",
            filter: {
              assetClassification: "IMMOVABLE",
            },
          },
        },
        validation: { required: false },
      },
    ],
  },
};

/** Offline fallback when Estate.CitizenMyApplicationsConfig MDMS is empty. */
const LOCAL_CITIZEN_MY_APPLICATIONS_CONFIG = {
  header: "EST_MY_APPLICATIONS",
  resultMode: "cards",
  autoSearch: true,
  emptyState: {
    message: "EST_NO_APPLICATION_FOUND_MSG",
    showCreateAction: false,
  },
  paginationDefaults: {
    offset: 0,
    limit: 50,
    sortBy: "createdDate",
    sortOrder: "DESC",
  },
  filters: [
    {
      order: 1,
      key: "EST_ASSET_NUMBER",
      name: "estateNo",
      type: "text",
      placeholder: "EST_ENTER_ASSET_NUMBER",
    },
    {
      order: 2,
      key: "PT_COMMON_TABLE_COL_STATUS_LABEL",
      name: "assetStatus",
      type: "dropdown",
      placeholder: "EST_SELECT_STATUS",
      dataSource: {
        type: "MDMS",
        moduleName: "Estate",
        masterName: "AssetStatus",
      },
    },
  ],
  actionButton: {
    search: "ES_COMMON_SEARCH",
    clear: "ES_COMMON_CLEAR_ALL",
  },
};

export const pickFirstMdmsEntry = (data) => {
  if (!data) return null;
  if (Array.isArray(data)) return data[0] || null;
  if (Array.isArray(data.body)) return data.body[0] || null;
  return data;
};

/**
 * Build search page config from MDMS Estate.SearchApplicationConfig,
 * falling back to local defaults when MDMS is empty.
 * Accepts legacy `searchApplicationConfig` key for older MDMS payloads.
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

/**
 * Build citizen my-applications config from MDMS Estate.CitizenMyApplicationsConfig.
 */
export const resolveCitizenMyApplicationsConfig = (mdmsData) => {
  const entry = pickFirstMdmsEntry(mdmsData);
  if (!entry || typeof entry !== "object") return LOCAL_CITIZEN_MY_APPLICATIONS_CONFIG;
  return {
    ...LOCAL_CITIZEN_MY_APPLICATIONS_CONFIG,
    ...entry,
    emptyState: {
      ...LOCAL_CITIZEN_MY_APPLICATIONS_CONFIG.emptyState,
      ...(entry.emptyState || {}),
    },
    paginationDefaults: {
      ...LOCAL_CITIZEN_MY_APPLICATIONS_CONFIG.paginationDefaults,
      ...(entry.paginationDefaults || {}),
    },
    filters:
      Array.isArray(entry.filters) && entry.filters.length > 0
        ? entry.filters
        : LOCAL_CITIZEN_MY_APPLICATIONS_CONFIG.filters,
    actionButton: {
      ...LOCAL_CITIZEN_MY_APPLICATIONS_CONFIG.actionButton,
      ...(entry.actionButton || {}),
    },
  };
};

/**
 * Build payment-history config from MDMS Estate.PaymentHistoryConfig,
 * falling back to local paymentHistoryConfig.js when MDMS is empty.
 */
export const resolvePaymentHistoryConfig = (mdmsData) => {
  const entry = pickFirstMdmsEntry(mdmsData);
  if (!entry || typeof entry !== "object") return LOCAL_PAYMENT_HISTORY_CONFIG;
  return {
    ...LOCAL_PAYMENT_HISTORY_CONFIG,
    ...entry,
    emptyState: {
      ...LOCAL_PAYMENT_HISTORY_CONFIG.emptyState,
      ...(entry.emptyState || {}),
    },
    filters:
      Array.isArray(entry.filters) && entry.filters.length > 0
        ? entry.filters
        : LOCAL_PAYMENT_HISTORY_CONFIG.filters,
    resultFields:
      Array.isArray(entry.resultFields) && entry.resultFields.length > 0
        ? entry.resultFields
        : LOCAL_PAYMENT_HISTORY_CONFIG.resultFields,
    actionButton: {
      ...LOCAL_PAYMENT_HISTORY_CONFIG.actionButton,
      ...(entry.actionButton || {}),
    },
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

const pickValue = (...values) =>
  values.find((v) => v !== null && v !== undefined && v !== "");

/** Shared asset summary fields for assign-assets form, check page, and ack PDF. */
export const buildAllotmentAssetDisplay = (asset = {}, allotment = {}, t = (k) => k) => ({
  assetNo: pickValue(allotment.assetNo, asset.estateNo, asset.assetNo),
  assetRefNumber: pickValue(
    allotment.assetReferenceNo,
    allotment.assetRefNumber,
    asset.refAssetNo,
    asset.assetRef
  ),
  buildingName: pickValue(allotment.buildingName, asset.buildingName),
  localityDisplay: resolveLocalityDisplay(asset, t),
  totalFloorArea: pickValue(allotment.totalFloorArea, asset.totalFloorArea),
  buildingFloor: pickValue(allotment.buildingFloor, asset.buildingFloor, asset.floor),
  assetRate: pickValue(allotment.rentRate, allotment.rate, asset.rate),
});
