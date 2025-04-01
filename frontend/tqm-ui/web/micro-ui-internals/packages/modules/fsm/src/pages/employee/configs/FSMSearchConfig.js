export const FSMSearchConfig = () => {
  return {
    label: "ACTION_TEST_SEARCH_FSM_APPLICATION",
    type: "search",
    apiDetails: {
      serviceName: "/fsm/v1/_search",
      requestParam: {
        tenantId: "pg.citya",
        limit: 10,
        offset: 0,
      },
      requestBody: {
        inbox: {
          moduleSearchCriteria: {},
        },
      },
      minParametersForSearchForm: 1,
      masterName: "commonUiConfig",
      moduleName: "FSMSearchConfig",
      tableFormJsonPath: "requestParam",
      filterFormJsonPath: "requestParam",
      searchFormJsonPath: "requestParam",
    },
    sections: {
      search: {
        uiConfig: {
          headerStyle: null,
          formClassName: "",
          primaryLabel: "ES_COMMON_SEARCH",
          secondaryLabel: "ES_COMMON_CLEAR_SEARCH",
          minReqFields: 1,
          showFormInstruction: "ES_SEARCH_APPLICATION_ERROR",
          defaultValues: {
            applicationNos: "",
            mobileNumber: "",
            applicationStatus: "",
            fromDate: "",
            toDate: "",
          },
          fields: [
            {
              label: "Application No",
              type: "text",
              isMandatory: false,
              disable: false,
              populators: {
                name: "applicationNos",
                error: "PROJECT_PATTERN_ERR_MSG",
              },
            },
            {
              label: "Mobile No.",
              type: "text",
              isMandatory: false,
              disable: false,
              populators: {
                name: "mobileNumber",
                error: "ESTIMATE_PATTERN_ERR_MSG",
                validation: {
                  maxlength: 10,
                },
              },
            },
            {
              label: "Status",
              type: "apidropdown",
              isMandatory: false,
              disable: false,
              populators: {
                name: "applicationStatus",
                optionsKey: "name",
                allowMultiSelect: false,
                masterName: "commonUiConfig",
                moduleName: "FSMSearchConfig",
                customfn: "getApplicationStatus",
              },
            },
            {
              label: "Search From Date",
              type: "date",
              isMandatory: false,
              disable: false,
              key: "fromDate",
              populators: {
                name: "fromDate",
                max: "currentDate",
              },
            },
            {
              label: "Search To Date",
              type: "date",
              isMandatory: false,
              disable: false,
              key: "toDate",
              populators: {
                name: "toDate",
                error: "DATE_VALIDATION_MSG",
                max: "currentDate",
              },
              additionalValidation: {
                type: "date",
                keys: {
                  start: "fromDate",
                  end: "toDate",
                },
              },
            },
          ],
        },
        label: "",
        children: {},
        show: true,
      },
      searchResult: {
        applicationNo: "",
        applicantName: "",
        mobileNumber: "",
        propertyType: "",
        subPropertyType: "",
        locality: "",
        status: "",
        uiConfig: {
          columns: [
            {
              key: "applicationNo",
              label: "ES_INBOX_APPLICATION_NO",
              jsonPath: "applicationNo",
              additionalCustomization: true,
            },
            {
              key: "applicantName",
              label: "ES_APPLICATION_DETAILS_APPLICANT_NAME",
              jsonPath: "citizen.name",
            },
            {
              key: "mobileNumber",
              label: "ES_APPLICATION_DETAILS_APPLICANT_MOBILE_NO",
              jsonPath: "citizen.mobileNumber",
            },
            {
              key: "propertyType",
              label: "ES_APPLICATION_DETAILS_PROPERTY_TYPE",
              jsonPath: "propertyUsage",
              additionalCustomization: true,
            },
            {
              key: "subPropertyType",
              label: "ES_APPLICATION_DETAILS_PROPERTY_SUB-TYPE",
              additionalCustomization: true,
            },
            {
              key: "locality",
              label: "ES_INBOX_LOCALITY",
              jsonPath: "address.locality.name",
            },
            {
              key: "status",
              label: "ES_INBOX_STATUS",
              jsonPath: "applicationStatus",
              additionalCustomization: true,
            },
          ],
          enableGlobalSearch: false,
          enableColumnSort: true,
          resultsJsonPath: "fsm",
        },
        children: {},
        show: true,
      },
    },
    additionalSections: {},
  };
};
