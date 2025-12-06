export const tqmInboxConfig = {
  "tenantId": "od",
  "moduleName": "commonSanitationUiConfig",
  "tqmInboxConfig": [
    {
      "label": "ACTION_TEST_TQM_INBOX",
      "type": "inbox",
      "apiDetails": {
        "serviceName": "/inbox/v2/_search",
        "requestParam": {},
        "requestBody": {
          "inbox": {
            "processSearchCriteria": {
              "businessService": [
                "PQM"
              ],
              "moduleName": "pqm"
            },
            "moduleSearchCriteria": {}
          }
        },
        "minParametersForSearchForm": 0,
        "minParametersForFilterForm": 0,
        "masterName": "commonUiConfig",
        "moduleName": "TqmInboxConfigUlbAdmin",
        "tableFormJsonPath": "requestBody.inbox",
        "filterFormJsonPath": "requestBody.custom",
        "searchFormJsonPath": "requestBody.custom"
      },
      "sections": {
        "search": {
          "uiConfig": {
            "headerLabel": "ES_COMMON_SEARCH",
            "type":"search",
            "typeMobile":"filter",
            "searchWrapperStyles": {
              "flexDirection": "column-reverse",
              "marginTop": "1.4rem",
              "alignItems": "center",
              "justifyContent": "end",
              "gridColumn": "3"
            },
            "headerStyle": null,
            "primaryLabel": "Search",
            "secondaryLabel": "ES_COMMON_CLEAR_SEARCH",
            "minReqFields": 0,
            "defaultValues": {
              "id": "",
              "plantCodes": ""
            },
            "fields": [
              {
                "label": "TQM_TEST_ID",
                "type": "text",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "name": "id",
                  "error": "TQM_ERR_VALID_TEST_ID",
                  "style":{
                    "marginBottom":"0px"
                  }
                },
                "removableTagConf":{
                  "name":"id",
                  "label":"TQM_RT_ID",
                  "valueJsonPath":"id",
                  "type":"single", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"searchForm.id",
                  // "deleteRef":"code"
                }
              },
              {
                "label": "TQM_PLANT_NAME",
                "type": "apidropdown",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "optionsCustomStyle": {
                    "top": "2.3rem"
                  },
                  "name": "plantCodes",
                  "optionsKey": "i18nKey",
                  "allowMultiSelect": false,
                  "masterName": "commonUiConfig",
                  "moduleName": "TqmInboxConfigUlbAdmin",
                  "customfn": "populatePlantUsersReqCriteria"
                },
                "removableTagConf":{
                  "name":"plantCodes",
                  "label":"TQM_RT_PLANT",
                  "valueJsonPath":"i18nKey",
                  "type":"multi", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"searchForm.plantCodes",
                  "deleteRef":"id"
                }
              }
            ]
          },
          "label": "",
          "labelMobile": "ES_COMMON_SEARCH",
          "children": {},
          "show": true
        },
        "searchResult": {
          "uiConfig": {
            "columns": [
              {
                "label": "TQM_TEST_ID",
                "jsonPath": "businessObject.testId",
                "additionalCustomization": true
              },
              {
                "label": "TQM_PLANT_NAME",
                "jsonPath": "businessObject.plantCode",
                "prefix": "PQM.Plant_",
                "translate": true
              },
              {
                "label": "TQM_PENDING_DATE",
                "jsonPath": "businessObject.scheduledDate",
                "additionalCustomization": true
              },
              {
                "label": "TQM_TREATMENT_PROCESS",
                "jsonPath": "businessObject.processCode",
                "prefix": "PQM.Process_",
                "translate": true
              },
              {
                "label": "TQM_INBOX_STATUS",
                "jsonPath": "ProcessInstance.state.applicationStatus",
                "prefix": "WF_STATUS_",
                "translate": true
              },
              {
                "label": "TQM_INBOX_SLA",
                "jsonPath": "businessObject.serviceSla",
                "additionalCustomization": true,
                "disableSortBy":true
              }
            ],
            "enableGlobalSearch": false,
            "enableColumnSort": true,
            "resultsJsonPath": "items",
            "tableClassName":"table pqm-table"
          },
          "children": {},
          "show": true
        },
        "links": {
          "uiConfig": {
            "links": [
              {
                "text": "TQM_VIEW_PAST_RESULTS",
                "url": "/employee/tqm/search-test-results?from=TQM_BREAD_INBOX",
                "roles": [
                  "PQM_ADMIN"
                ]
              },
              // {
              //   "text": "TQM_VIEW_IOT_RESULTS",
              //   "url": "/employee/tqm/search-test-results?from=TQM_BREAD_INBOX",
              //   "roles": [
              //     "PQM_ADMIN"
              //   ]
              // },
              // {
              //   "text": "TQM_SENSOR_MON",
              //   "url": "/employee/tqm/search-devices?from=TQM_BREAD_INBOX",
              //   "roles": [
              //     "PQM_ADMIN"
              //   ]
              // },
              // {
              //   "text": "TQM_VIEW_DASHBOARD",
              //   "url": "/employee/dss/dashboard/pqm",
              //   "roles": [
              //     "PQM_ADMIN"
              //   ]
              // }
            ],
            "label": "TQM_QUALITY_TESTING",
            "logoIcon": {
              "component": "TqmInboxIcon",
              "customClass": "inbox-links-icon"
            }
          },
          "children": {},
          "show": true
        },
        "filter": {
          "uiConfig": {
            "headerLabel": "TQM_INBOX_FILTER",
            "secondaryLabel": "ES_COMMON_CLEAR_SEARCH",
            "formClassName": "filter",
            "type": "filter",
            "typeMobile":"sort",
            "headerStyle": null,
            "primaryLabel": "Filter",
            "minReqFields": 0,
            "defaultValues": {
              "processCodes": [],
              "stage": [],
              "materialCodes": [],
              "status": []
            },
            "fields": [
              {
                "label": "TQM_TREATMENT_PROCESS",
                "type": "dropdown",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "allowMultiSelect": true,
                  "name": "processCodes",
                  "optionsKey": "i18nKey",
                  "labelKey": "i18nKey",
                  "masterName": "commonUiConfig",
                  "moduleName": "TqmInboxConfigUlbAdmin",
                  "customfn": "populateMdmsv2SearchReqCriteria",
                  "mdmsv2": {
                    "schemaCode": "PQM.Process"
                  },
                },
                "removableTagConf":{
                  "name":"processCodes",
                  "label":"TQM_RT_PROCESS",
                  "valueJsonPath":"i18nKey",
                  "type":"multi", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"filterForm.processCodes",
                  "deleteRef":"code"
                }
              },
              {
                "label": "TQM_PROCESS_STAGE",
                "type": "dropdown",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "name": "stage",
                  "optionsKey": "i18nKey",
                  "allowMultiSelect": true,
                  "mdmsv2": {
                    "schemaCode": "PQM.Stage"
                  }
                },
                "removableTagConf":{
                  "name":"stage",
                  "label":"TQM_RT_STAGE",
                  "valueJsonPath":"i18nKey",
                  "type":"multi", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"filterForm.stage",
                  "deleteRef":"code"
                }
              },
              {
                "label": "TQM_OUTPUT_TYPE",
                "type": "dropdown",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "allowMultiSelect": true,
                  "name": "materialCodes",
                  "optionsKey": "i18nKey",
                  "masterName": "commonUiConfig",
                  "moduleName": "TqmInboxConfigUlbAdmin",
                  "customfn": "populateMdmsv2SearchReqCriteria",
                  "labelKey": "i18nKey",
                  "mdmsv2": {
                    "schemaCode": "PQM.Material"
                  }
                },
                "removableTagConf":{
                  "name":"materialCodes",
                  "label":"TQM_RT_OUTPUT",
                  "valueJsonPath":"i18nKey",
                  "type":"multi", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"filterForm.materialCodes",
                  "deleteRef":"code"
                }
              },
              {
                "type": "workflowstatesfilter",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "componentLabel": "TQM_WF_STATUS",
                  "name": "status",
                  "labelPrefix": "WF_STATUS_",
                  "businessService": "PQM"
                },
                "removableTagConf":{
                  "name":"status",
                  "label":"Status",
                  // "valueJsonPath":"i18nKey",
                  "type":"workflowStatusFilter", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"filterForm.status",
                  // "deleteRef":"id",
                  "valuePrefix": "WF_STATUS_PQM_",
                }
              }
            ]
          },
          "label": "Filter",
          "labelMobile": "TQM_INBOX_FILTER",
          "show": true
        }
      },
      "additionalSections": {},
      "persistFormData":true,
      "showAsRemovableTagsInMobile":true
    }
  ]
}
