export const tqmSearchConfigPlantOperator = {
  tenantId: 'od',
  moduleName: 'commonSanitationUiConfig',
  tqmSearchConfig: [
    {
      "label": "TQM_VIEW_PAST_RESULTS",
      "type": "search",
      "apiDetails": {
        "serviceName": "/pqm-service/v1/_search",
        "requestParam": {},
        "requestBody": {},
        "minParametersForSearchForm": 0,
        "masterName": "commonUiConfig",
        "moduleName": "SearchTestResults",
        "tableFormJsonPath": "requestBody.custom",
        "filterFormJsonPath": "requestBody.custom",
        "searchFormJsonPath": "requestBody.custom"
      },
      "sections": {
        "search": {
          "uiConfig": {
            "type": "search",
            "typeMobile": "filter",
            "headerLabel": "TQM_INBOX_FILTERS",
            "headerStyle": null,
            "primaryLabel": "TQM_INBOX_FILTER",
            "secondaryLabel": "ES_COMMON_CLEAR_SEARCH",
            "minReqFields": 0,
            "showFormInstruction": "TQM_SEARCH_HINT",
            "defaultValues": {
              // "plantCodes": [],
              "processCodes": [],
              "materialCodes": [],
              "testType": [],
              "dateRange": {}
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
                  "deleteRef":"code"// only required for multi type
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
                  "deleteRef":"code" // only required for multi type
                }
              },
              {
                "label": "TQM_TEST_TYPE",
                "type": "dropdown",
                "isMandatory": false,
                "disable": false,
                "populators": {
                  "optionsCustomStyle": {
                    "top": "2.3rem"
                  },
                  "name": "testType",
                  "optionsKey": "i18nKey",
                  "allowMultiSelect": true,
                  "mdmsv2": {
                    "schemaCode": "PQM.SourceType"
                  }
                },
                "removableTagConf":{
                  "name":"testType",
                  "label":"TQM_RT_TEST_TYPE",
                  "valueJsonPath":"i18nKey",
                  "type":"multi", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"searchForm.testType",
                  "deleteRef":"code" // only required for multi type
                }
              },
              {
                "label": "TQM_VIEW_PAST_DATE_RANGE",
                "type": "dateRange",
                "isMandatory": false,
                "disable": false,
                "key": "dateRange",
                "preProcess": {
                  "updateDependent": [
                    "populators.maxDate"
                  ]
                },
                "populators": {
                  "name": "dateRange",
                  "maxDate":"currentDate"
                },
                "removableTagConf":{
                  "name":"dateRange",
                  "label":"TQM_RT_DATE_RANGE",
                  "valueJsonPath":"range.title",
                  "type":"dateRange", // single, multi, date(single), dateRange(single),...etc,
                  "sessionJsonPath":"searchForm.dateRange",
                  // "deleteRef":"code" // only required for multi type
                }
              }
            ]
          },
          "label": "",
          "children": {},
          "show": true,
          "labelMobile": "TQM_INBOX_FILTER"
        },
        "searchResult": {
          "uiConfig": {
            "columns": [
              {
                "label": "TQM_TEST_ID",
                "jsonPath": "testId",
                "additionalCustomization": true
              },
              {
                "label": "TQM_TREATMENT_PROCESS",
                "jsonPath": "processCode",
                "additionalCustomization": false,
                "prefix": "PQM.Process_",
                "translate": true
              },
              {
                "label": "TQM_PLANT_NAME",
                "jsonPath": "plantCode",
                "prefix": "PQM.Plant_",
                "translate": true
              },
              {
                "label": "TQM_PROCESS_STAGE",
                "jsonPath": "stageCode",
                "additionalCustomization": false,
                "prefix": "PQM.STAGE_",
                "translate": true
              },
              {
                "label": "TQM_OUTPUT_TYPE",
                "jsonPath": "materialCode",
                "additionalCustomization": false,
                "prefix": "PQM.MATERIAL_",
                "translate": true
              },
              {
                "label": "TQM_TEST_TYPE",
                "jsonPath": "testType",
                "additionalCustomization": false,
                "prefix": "PQM.TESTTYPE_",
                "translate": true
              },
              {
                "label": "TQM_TEST_SUBMITTED_DATE",
                "jsonPath": "auditDetails.lastModifiedTime",
                "additionalCustomization": true
              },
              {
                "label": "TQM_TEST_RESULTS",
                "jsonPath": "status",
                "additionalCustomization": true
              }
            ],
            "showActionBarMobileCard": true,
            "actionButtonLabelMobileCard": "TQM_VIEW_RESULTS",
            "enableGlobalSearch": false,
            "enableColumnSort": true,
            "resultsJsonPath": "tests",
            "tableClassName":"table pqm-table"
          },
          "children": {},
          "show": true
        },
        "filter": {
          "uiConfig": {
            "type": "filter",
            "typeMobile": "sort",
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
                //   // "deleteRef":"code" // only required for multi type
                // }
              }
            ]
          },
          "label": "Filter",
          "labelMobile": "TQM_INBOX_SORT",
          "show": true
        }
      },
      "additionalSections": {},
      "persistFormData":true,
      "showAsRemovableTagsInMobile":true
    },
  ],
};
