/**
 * EST Search Applications — DynamicForm routeConfig (mode="search").
 * Same field shape as allotment/registration forms for one reusable DynamicForm.
 */
export const EST_SEARCH_APPLICATION_CONFIG = {
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
  /** Passed to DynamicForm as routeConfig */
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
            moduleName: "egov-location",
            masterName: "TenantBoundary",
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
          },
        },
        validation: { required: false },
      },
    ],
  },
};

export default EST_SEARCH_APPLICATION_CONFIG;
