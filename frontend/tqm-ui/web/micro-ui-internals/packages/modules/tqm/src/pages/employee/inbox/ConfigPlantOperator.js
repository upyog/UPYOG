export const tqmInboxConfigPlantOperator = {
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
        "moduleName": "TqmInboxConfig",
        "tableFormJsonPath": "requestBody.inbox",
        "filterFormJsonPath": "requestBody.custom",
        "searchFormJsonPath": "requestBody.custom"
      },
      "sections": {
        "search": {
          "uiConfig": {
            "type": "search",
            "typeMobile":"filter",
            "headerLabel": "TQM_INBOX_FILTERS",
            "searchWrapperStyles": {
              "flexDirection": "column-reverse",
              "marginTop": "1.4rem",
              "alignItems": "center",
              "justifyContent": "end",
              "gridColumn": "3"
            },
            "headerStyle": null,
            "primaryLabel": "TQM_INBOX_FILTER",
            "secondaryLabel": "ES_COMMON_CLEAR_SEARCH",
            "minReqFields": 0,
            "defaultValues": {
              "processCodes": [],
              "materialCodes": [],
              "status": [],
              "dateRange": ""
            },
            "fields": [
              {
                "label": "TQM_TREATMENT_PROCESS",
                "type": "dropdown",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "optionsCustomStyle": {
                    "top": "2.3rem"
                  },
                  "name": "processCodes",
                  "optionsKey": "i18nKey",
                  "allowMultiSelect": true,
                  "mdmsv2": {
                    "schemaCode": "PQM.Process"
                  }
                },
                "removableTagConf":{
                  "name":"processCodes",
                  "label":"TQM_RT_PROCESS",
                  "valueJsonPath":"i18nKey",
                  "type":"multi", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"searchForm.processCodes",
                  "deleteRef":"code"
                }
              },
              {
                "label": "TQM_OUTPUT_TYPE",
                "type": "dropdown",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "optionsCustomStyle": {
                    "top": "2.3rem"
                  },
                  "name": "materialCodes",
                  "optionsKey": "i18nKey",
                  "allowMultiSelect": true,
                  "mdmsv2": {
                    "schemaCode": "PQM.Material"
                  }
                },
                "removableTagConf":{
                  "name":"materialCodes",
                  "label":"TQM_RT_OUTPUT",
                  "valueJsonPath":"i18nKey",
                  "type":"multi", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"searchForm.materialCodes",
                  "deleteRef":"code"
                }
              },
              {
                "label": "TQM_INBOX_STATUS",
                "type": "apidropdown",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "optionsCustomStyle": {
                    "top": "2.3rem"
                  },
                  "name": "status",
                  "optionsKey": "i18nKey",
                  "allowMultiSelect": true,
                  "masterName": "commonUiConfig",
                  "moduleName": "TqmInboxConfig",
                  "customfn": "populateStatusReqCriteria"
                },
                "removableTagConf":{
                  "name":"status",
                  "label":"TQM_RT_STATUS",
                  "valueJsonPath":"i18nKey",
                  "type":"multi", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"searchForm.status",
                  "deleteRef":"code"
                }
              },
              {
                "label": "TQM_INBOX_DATE_RANGE",
                "type": "dateRange",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "name": "dateRange"
                },
                "removableTagConf":{
                  "name":"dateRange",
                  "label":"TQM_RT_DATE_RANGE",
                  "valueJsonPath":"range.title",
                  "type":"dateRange", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"searchForm.dateRange",
                  // "deleteRef":"code" // deleteRef should only be given for multi type
                }
              }
            ]
          },
          "label": "",
          "labelMobile": "TQM_INBOX_FILTER",
          "children": {},
          "show": true,
        },
        "filter": {
          "uiConfig": {
            "formClassName": "filter",
            "type": "filter",
            "typeMobile":"sort",
            "headerStyle": null,
            "headerLabel": "TQM_INBOX_SORTBY",
            "primaryLabel": "TQM_INBOX_SORT",
            "secondaryLabel": "TQM_CLEAR_SEARCH",
            "minReqFields": 0,
            "defaultValues": {
              "sortOrder": {
                "code": "LATEST_FIRST",
                "name": "TQM_INBOX_LATEST_FIRST",
                "value": "DESC"
              }
            },
            "fields": [
              {
                "label": "",
                "type": "radio",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "name": "sortOrder",
                  "options": [
                    {
                      "code": "LATEST_FIRST",
                      "name": "TQM_INBOX_LATEST_FIRST",
                      "value": "DESC"
                    },
                    {
                      "code": "LATEST_LAST",
                      "name": "TQM_INBOX_LATEST_LAST",
                      "value": "ASC"
                    }
                  ],
                  "optionsKey": "name",
                  "styles": {
                    "gap": "1rem",
                    "flexDirection": "column"
                  },
                  "innerStyles": {
                    "display": "flex"
                  }
                },
                // "removableTagConf":{
                //   "name":"sortOrder",
                //   "label":"TQM_RT_SORT_ORDER",
                //   "valueJsonPath":"name",
                //   "type":"single", // single, multi, date(single), dateRange(single),...etc,
                //   "sessionJsonPath":"filterForm.sortOrder",
                //   // "deleteRef":"code"
                // }
              }
            ]
          },
          "label": "TQM_INBOX_SORT",
          "labelMobile": "TQM_INBOX_SORT",
          "show": true,
        },
        "searchResult": {
          "uiConfig": {
            "columns": [
              {
                "label": "TQM_TEST_ID",
                "jsonPath": "ProcessInstance.businessId",
                "additionalCustomization": true
              },
              {
                "label": "TQM_TREATMENT_PROCESS",
                "jsonPath": "businessObject.processCode",
                "prefix": "PQM.Process_",
                "translate": true
              },
              {
                "label": "TQM_PLANT_NAME",
                "jsonPath": "businessObject.plantCode",
                "prefix": "PQM.Plant_",
                "translate": true
              },
              {
                "label": "TQM_PROCESS_STAGE",
                "jsonPath": "businessObject.stageCode",
                "prefix": "PQM.Stage_",
                "translate": true
              },
              {
                "label": "TQM_OUTPUT_TYPE",
                "jsonPath": "businessObject.materialCode",
                "prefix": "PQM.Material_",
                "translate": true
              },
              {
                "label": "TQM_PENDING_DATE",
                "jsonPath": "businessObject.scheduledDate",
                "additionalCustomization": true
              },
              {
                "label": "TQM_INBOX_STATUS",
                "jsonPath": "ProcessInstance.state.applicationStatus",
                "prefix": "WF_STATUS_TQM_",
                "translate": true
              },
              {
                "label": "TQM_INBOX_SLA",
                "jsonPath": "businessObject.serviceSla",
                "additionalCustomization": true
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
                "url": "/employee/tqm/search-test-results",
                "roles": [
                  "PQM_TP_OPERATOR","FSM_EMP_FSTPO"
                ]
              },
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
        
      },
      "additionalSections": {},
      "persistFormData":true,//for now it's redundant
      "showAsRemovableTagsInMobile":true
    }
  ]
}
