import { InboxSearchComposer,Header } from "@egovernments/digit-ui-react-components";

import React,{Fragment} from 'react'

const config = {
  label: "ATM_SEARCH_ATTENDANCE",
  type: "search",
  apiDetails: {
    serviceName: "/wms/muster/_search",
    requestParam: {},
    requestBody: {
      inbox: {
        moduleSearchCriteria: {},
      },
    },
    minParametersForSearchForm: 1,
    masterName: "commonUiConfig",
    moduleName: "SearchAttendanceConfig",
    tableFormJsonPath: "requestBody.inbox",
    filterFormJsonPath: "requestBody.inbox.moduleSearchCriteria",
    searchFormJsonPath: "requestBody.inbox.moduleSearchCriteria",
  },
  sections: {
    search: {
      uiConfig: {
        headerStyle: null,
        primaryLabel: "ES_COMMON_SEARCH",
        secondaryLabel: "ES_COMMON_CLEAR_SEARCH",
        minReqFields: 1,
        showFormInstruction: "MUSTER_SEARCH_HINT",
        formClassName: "custom-both-clear-search",
        defaultValues: {
          ward: "",
          projectType: "",
          attendanceRegisterName: "",
          musterRollNumber: "",
          musterRollStatus: "",
          startDate: "",
          endDate: "",
        },
        fields: [
          {
            label: "COMMON_WARD",
            type: "locationdropdown",
            isMandatory: false,
            disable: false,
            populators: {
              optionsCustomStyle: {
                top: "2.3rem",
              },
              name: "ward",
              type: "ward",
              optionsKey: "i18nKey",
              defaultText: "COMMON_SELECT_WARD",
              selectedText: "COMMON_SELECTED",
              allowMultiSelect: false,
            },
          },
          {
            label: "WORKS_PROJECT_TYPE",
            type: "dropdown",
            isMandatory: false,
            disable: false,
            populators: {
              name: "projectType",
              optionsKey: "name",
              optionsCustomStyle: {
                top: "2.3rem",
              },
              mdmsConfig: {
                masterName: "ProjectType",
                moduleName: "works",
                localePrefix: "COMMON_MASTERS",
              },
            },
          },
          {
            label: "ES_COMMON_PROJECT_NAME",
            type: "text",
            isMandatory: false,
            disable: false,
            preProcess: {
              convertStringToRegEx: ["populators.validation.pattern"],
            },
            populators: {
              name: "attendanceRegisterName",
              error: "PROJECT_PATTERN_ERR_MSG",
              validation: {
                pattern: '^[^\\$"<>?\\\\~`!@$%^()+={}\\[\\]*:;“”‘’]{1,50}$',
                minlength: 2,
              },
            },
          },
          {
            label: "ATM_MUSTER_ROLL_ID",
            type: "text",
            isMandatory: false,
            disable: false,
            preProcess: {
              convertStringToRegEx: ["populators.validation.pattern"],
            },
            populators: {
              name: "musterRollNumber",
              error: "COMMON_PATTERN_ERR_MSG_MUSTER_ID",
              validation: {
                pattern: "MR\\/[0-9]+-[0-9]+\\/[0-9]+\\/[0-9]+\\/[0-9]+",
                minlength: 2,
              },
            },
          },
          {
            label: "CORE_COMMON_STATUS",
            type: "apidropdown",
            isMandatory: false,
            disable: false,
            populators: {
              optionsCustomStyle: {
                top: "2.3rem",
              },
              name: "musterRollStatus",
              optionsKey: "i18nKey",
              allowMultiSelect: false,
              masterName: "commonUiConfig",
              moduleName: "SearchAttendanceConfig",
              customfn: "populateReqCriteria",
            },
          },
          {
            label: "CREATED_FROM_DATE",
            type: "date",
            isMandatory: false,
            disable: false,
            key: "startDate",
            preProcess : {
              updateDependent : ["populators.max"]
            },
            populators: {
              name: "startDate",
              max : "currentDate"
            },
          },
          {
            label: "CREATED_TO_DATE",
            type: "date",
            isMandatory: false,
            disable: false,
            key: "endDate",
            preProcess : {
              updateDependent : ["populators.max"]
            },
            populators: {
              name: "endDate",
              max : "currentDate"
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
            label: "ES_COMMON_MUSTER_ROLL_ID",
            jsonPath: "businessObject.musterRollNumber",
            additionalCustomization: true,
          },
          {
            label: "ES_COMMON_PROJECT_NAME",
            jsonPath: "businessObject.additionalDetails.attendanceRegisterName",
            additionalCustomization: true,
          },
          {
            label: "ES_COMMON_LOCATION",
            jsonPath: "businessObject.additionalDetails",
            additionalCustomization: true,
          },
          {
            label: "COMMON_NAME_OF_CBO",
            jsonPath: "businessObject.additionalDetails.orgName",
          },
          {
            label: "CORE_COMMON_STATUS",
            jsonPath: "ProcessInstance.state.state",
            additionalCustomization: true,
          }
        ],
        enableGlobalSearch: false,
        enableColumnSort: true,
        resultsJsonPath: "items",
      },
      children: {},
      show: true,
    },
  },
  additionalSections: {},
}

const SampleComp = () => {
  return (
    <React.Fragment>
      <Header className="works-header-search">{config?.label}</Header>
      <div className="inbox-search-wrapper">
        <InboxSearchComposer configs={config}></InboxSearchComposer>
      </div>
    </React.Fragment>
  )
}

export default SampleComp