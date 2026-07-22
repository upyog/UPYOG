/**
 * Local fallback for Estate.CitizenMyApplicationsConfig (MDMS).
 * Citizen My Applications loads MDMS first via resolveSearchApplicationConfig().
 */
const EST_CITIZEN_MY_APPLICATIONS_CONFIG = {
  header: "EST_MY_APPLICATIONS",
  sessionKey: "EST_CITIZEN_MY_APPLICATIONS",
  resultMode: "cards",
  autoSearch: true,
  emptyState: {
    message: "EST_NO_APPLICATION_FOUND_MSG",
    showCreateAction: false,
  },
  table: {
    showAssetRef: false,
    actions: "none",
  },
  paginationDefaults: {
    offset: 0,
    limit: 10,
    sortBy: "createdDate",
    sortOrder: "DESC",
  },
  routeConfig: {
    key: "CitizenSearchFilters",
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
        key: "EST_ASSET_NUMBER",
        field: {
          code: "EST_ASSET_NUMBER",
          name: "estateNo",
          type: "text",
          placeholder: "EST_ENTER_ASSET_NUMBER",
        },
        validation: { required: false },
      },
      {
        order: 2,
        key: "PT_COMMON_TABLE_COL_STATUS_LABEL",
        apiFieldName: "assetStatus",
        field: {
          code: "PT_COMMON_TABLE_COL_STATUS_LABEL",
          name: "assetStatus",
          type: "dropdown",
          placeholder: "EST_SELECT_STATUS",
          dataSource: {
            type: "MDMS",
            moduleName: "Estate",
            masterName: "AssetStatus",
          },
        },
        validation: { required: false },
      },
    ],
  },
};

export default EST_CITIZEN_MY_APPLICATIONS_CONFIG;
