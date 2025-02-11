export const VehicleAlertsConfig = {
  label: "ES_FSM_ALERTS",
  type: "inbox",
  apiDetails: {
    serviceName: "/inbox/v2/_search",
    requestParam: {},
    requestBody: {
      inbox: {
        processSearchCriteria: {
          businessService: ["FSM", "FSM_POST_PAY_SERVICE", "PAY_LATER_SERVICE", "FSM_ADVANCE_PAY_SERVICE", "FSM_ZERO_PAY_SERVICE"],
          moduleName: "fsm",
        },
        moduleSearchCriteria: {
          sortBy: "createdTime",
          sortOrder: "DESC",
        },
        // tenantId: "pb.amritsar"
      },
    },
    minParametersForSearchForm: 0,
    minParametersForFilterForm: 0,
    masterName: "commonUiConfig",
    moduleName: "FSMInboxConfig",
    tableFormJsonPath: "requestBody.inbox",
    filterFormJsonPath: "requestBody.inbox.moduleSearchCriteria",
    searchFormJsonPath: "requestBody.inbox.moduleSearchCriteria",
  },
  sections: {
    search: {
      uiConfig: {
        headerStyle: null,
        primaryLabel: "Search",
        secondaryLabel: "Clear Search",
        minReqFields: 1,
        defaultValues: {
          complaintNumber: "",
          mobileNumber: "",
        },
        fields: [
          {
            label: "ES_SEARCH_APPLICATION_NO",
            type: "text",
            isMandatory: false,
            disable: false,
            preProcess: {
              convertStringToRegEx: ["populators.validation.pattern"],
            },
            populators: {
              name: "applicationId",
              error: "ERR_INVALID_APPLICATION_NO",
              validation: {
                minlength: 3,
              },
            },
          },
          {
            label: "ES_FSM_DSO_NAME",
            type: "text",
            isMandatory: false,
            disable: false,
            preProcess: {
              convertStringToRegEx: ["populators.validation.pattern"],
            },
            populators: {
              name: "dso",
              error: "ERR_INVALID_DSO_NAME",
              validation: {
                pattern: "^[0-9]{0,10}+$",
                minlength: 3,
                maxlength: 10,
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
      label: "",
      estimateNumber: "",
      projectId: "",
      department: "",
      estimateStatus: "",
      fromProposalDate: "",
      toProposalDate: "",
      uiConfig: {
        columns: [
          {
            key: "applicationNo",
            label: "ES_ALERTS_APPLICATION_ID",
            jsonPath: "businessObject.applicationNo",
            additionalCustomization: true,
          },
          {
            key: "dso",
            label: "ES_ALERTS_DSO",
            jsonPath: "businessObject.name",
            additionalCustomization: true,
          },
          {
            key: "vehicleNumber",
            label: "ES_ALERTS_VEHICLE_NUMBER",
            jsonPath: "businessObject.locality",
            additionalCustomization: true,
          },
          {
            key: "type",
            label: "ES_ALERTS_TYPE",
            jsonPath: "ProcessInstance.state.applicationStatus",
            additionalCustomization: true,
          },
          {
            key: "date",
            label: "ES_ALERTS_DATE",
            jsonPath: "businessObject.auditDetails.createdTime",
            additionalCustomization: true,
          },
        ],
        enableGlobalSearch: false,
        enableColumnSort: true,
        resultsJsonPath: "items",
      },
      children: {},
      show: true,
    },
    links: {
      uiConfig: {
        links: [
          {
            text: "ES_TITLE_NEW_DESULDGING_APPLICATION",
            url: `/employee/fsm/new-application`,
            roles: ["FSM_CREATOR_EMP"],
          },
          {
            text: "ES_TITILE_SEARCH_APPLICATION",
            url: `/employee/fsm/search`,
            roles: [],
          },
          {
            text: "ES_TITLE_REPORTS",
            url: `/employee/report/fsm/FSMDailyDesludingReport`,
            roles: ["FSM_REPORT_VIEWER"],
            hyperlink: true,
          },
        ],
        label: "ES_TITLE_FSM",
        logoIcon: {
          component: "ReceiptIcon",
          customClass: "inbox-links-icon",
        },
      },
      children: {},
      show: true,
    },
    filter: {
      uiConfig: {
        type: "filter",
        headerStyle: null,
        primaryLabel: "Filter",
        secondaryLabel: "",
        minReqFields: 0,
        defaultValues: {
          status: "",
          locality: [],
          assignee: {
            code: "ASSIGNED_TO_ME",
            name: "ASSIGNED_TO_ME",
          },
          dateRange: {
            range: {
              startDate: new Date(),
              endDate: new Date(),
            },
          },
        },
        fields: [
          // {
          //   label: "",
          //   type: "radio",
          //   isMandatory: false,
          //   disable: false,
          //   populators: {
          //     name: "assignee",
          //     options: [
          //       {
          //         code: "ASSIGNED_TO_ME",
          //         name: "ASSIGNED_TO_ME",
          //       },
          //       {
          //         code: "ASSIGNED_TO_ALL",
          //         name: "ASSIGNED_TO_ALL",
          //       },
          //     ],
          //     optionsKey: "name",
          //     styles: {
          //       gap: "1rem",
          //       flexDirection: "column",
          //     },
          //     innerStyles: {
          //       display: "flex",
          //     },
          //   },
          // },
          {
            label: "ES_INBOX_LOCALITY",
            type: "apidropdown",
            isMandatory: false,
            disable: false,
            preProcess: {
              convertStringToRegEx: ["populators.validation.pattern"],
            },
            populators: {
              name: "locality",
              type: "locality",
              optionsKey: "i18nKey",
              defaultText: "COMMON_SELECT_LOCALITY",
              selectedText: "COMMON_SELECTED",
              allowMultiSelect: true,
              masterName: "commonUiConfig",
              moduleName: "FSMInboxConfig",
              customfn: "localitydropdown",
              isDropdownWithChip: true,
            },
          },
          {
            label: "Vehicle Number",
            type: "apidropdown",
            isMandatory: false,
            disable: false,
            preProcess: {
              convertStringToRegEx: ["populators.validation.pattern"],
            },
            populators: {
              name: "vehicleNumber",
              type: "vehicleNumber",
              optionsKey: "registrationNumber",
              defaultText: "Select vehicle number",
              selectedText: "COMMON_SELECTED",
              allowMultiSelect: true,
              masterName: "commonUiConfig",
              moduleName: "VehicleAlertsConfig",
              customfn: "vehicleListDropdown",
              isDropdownWithChip: true,
            },
          },
          {
            label: "Date Range",
            type: "dateRange",
            isMandatory: false,
            disable: false,
            preProcess: {
              convertStringToRegEx: ["populators.validation.pattern"],
            },
            populators: {
              name: "dateRange",
              type: "dateRange",
              range: {
                startDate: new Date(),
                endDate: new Date(),
              },
              validation: {},
              //   masterName: "commonUiConfig",
              //   moduleName: "VehicleAlertsConfig",
              //   customfn: "vehicleListDropdown",
              //   isDropdownWithChip: true,
            },
          },
        ],
      },
      label: "Filter",
      show: true,
    },
  },
  additionalSections: {},
};
