export const FSMInboxConfig = {
  label: "ES_COMMON_INBOX",
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
            label: "ES_SEARCH_APPLICATION_APPLICATION_NO",
            type: "text",
            isMandatory: false,
            disable: false,
            preProcess: {
              convertStringToRegEx: ["populators.validation.pattern"],
            },
            populators: {
              name: "applicationNos",
              error: "ERR_INVALID_APPLICATION_NO",
              validation: {
                minlength: 3,
              },
            },
          },
          {
            label: "ES_SEARCH_APPLICATION_MOBILE_NO",
            type: "text",
            isMandatory: false,
            disable: false,
            preProcess: {
              convertStringToRegEx: ["populators.validation.pattern"],
            },
            populators: {
              name: "mobileNumber",
              error: "ERR_INVALID_MOBILE_NUMBER",
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
            label: "CS_FILE_DESLUDGING_APPLICATION_NO",
            jsonPath: "businessObject.applicationNo",
            additionalCustomization: true,
          },
          {
            key: "createdTime",
            label: "ES_INBOX_APPLICATION_DATE",
            jsonPath: "businessObject.auditDetails.createdTime",
            additionalCustomization: true,
          },
          {
            key: "locality",
            label: "ES_INBOX_LOCALITY",
            jsonPath: "businessObject.locality",
            additionalCustomization: true,
          },
          {
            key: "status",
            label: "ES_INBOX_STATUS",
            jsonPath: "ProcessInstance.state.applicationStatus",
            additionalCustomization: true,
          },
          {
            key: "sla",
            label: "ES_INBOX_SLA_DAYS_REMAINING",
            jsonPath: "businessObject.serviceSla",
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
        label: "ES_TITLE_FAECAL_SLUDGE_MGMT",
        logoIcon: {
          component: "ShippingTruck",
          customClass: "inbox-links-icon",
        },
      },
      children: {},
      show: true,
    },
    filter: {
      uiConfig: {
        formClassName: "filter",
        type: "filter",
        headerStyle: null,
        primaryLabel: "Filter",
        secondaryLabel: "",
        minReqFields: 0,
        defaultValues: {
          status: "",
          locality: [],
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
            key: "state",
            type: "component",
            component: "WorkflowFilter",
            // isMandatory: false,
            // disable: false,
            populators: {
              componentLabel: "ES_INBOX_STATUS",
              name: "state",
              labelPrefix: "CS_COMMON_",
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
