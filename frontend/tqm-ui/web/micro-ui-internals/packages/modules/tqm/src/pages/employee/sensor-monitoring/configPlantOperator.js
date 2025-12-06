export const SensorScreenConfigPlantOperator = {
    tenantId: 'od',
    moduleName: 'commonSanitationUiConfig',
    SensorConfig: [
      {
        label: 'TQM_VIEW_PAST_RESULTS',
        type: 'search',
        apiDetails: {
          serviceName: '/pqm-service/v1/_search',
          requestParam: {},
          requestBody: {},
          minParametersForSearchForm: 1,
          masterName: 'commonUiConfig',
          moduleName: 'SearchTestResults',
          tableFormJsonPath: 'requestBody.custom',
          filterFormJsonPath: 'requestBody.custom',
          searchFormJsonPath: 'requestBody.custom',
        },
        sections: {
            search: {
                uiConfig: {
                  headerLabel: 'TQM_INBOX_FILTERS',
                  headerStyle: null,
                  primaryLabel: 'ES_COMMON_SEARCH',
                  secondaryLabel: 'ES_COMMON_CLEAR_SEARCH',
                  minReqFields: 1,
                  showFormInstruction: 'TQM_SEARCH_HINT',
                  defaultValues: {
                    id:'',
                    plantCodes: [],
                    processCodes: [],
                    processStage: [],
                    status: [],
                  },
                  fields: [
                    {
                      "label": "Device ID",
                      "type": "text",
                      "isMandatory": false,
                      "disable": false,
                      // "preProcess": {
                      //   "convertStringToRegEx": [
                      //     "populators.validation.pattern"
                      //   ]
                      // },
                      "populators": {
                        "name": "id",
                        // "error": "Enter valid bill number",
                        // "validation": {
                        //   "pattern": "^[A-Za-z0-9\\/-]*$",
                        //   "minlength": 2
                        // }
                      }
                    },
                    {
                      label: 'TQM_PLANT_NAME',
                      type: 'dropdown',
                      isMandatory: false,
                      disable: false,
                      populators: {
                        optionsCustomStyle: {
                          top: '2.3rem',
                        },
                        name: 'plantCodes',
                        optionsKey: 'i18nKey',
                        allowMultiSelect: true,
                        mdmsv2:{
                          schemaCode:"PQM.Plant",
                        }
                      },
                    },
                    {
                      label: 'TQM_TREATMENT_PROCESS',
                      type: 'dropdown',
                      isMandatory: false,
                      disable: false,
                      populators: {
                        optionsCustomStyle: {
                          top: '2.3rem',
                        },
                        name: 'processCodes',
                        optionsKey: 'i18nKey',
                        allowMultiSelect: true,
                        mdmsv2:{
                          schemaCode:"PQM.Process",
                        }
                      },
                    },
                    {
                      label: 'TQM_PROCESS_STAGE',
                      type: 'dropdown',
                      isMandatory: false,
                      disable: false,
                      populators: {
                        optionsCustomStyle: {
                          top: '2.3rem',
                        },
                        name: 'processStage',
                        optionsKey: 'i18nKey',
                        allowMultiSelect: false,
                        mdmsv2:{
                          schemaCode:"PQM.Stage",
                        }
                      },
                    },
                    {
                        "label": "TQM_WF_STATUS",
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
                          "moduleName": "SensorConfig",
                          "customfn": "populateStatusReqCriteria"
                        }
                      },
                  ],
                },
                label: '',
                children: {},
                show: true,
                labelMobile: 'TQM_INBOX_FILTER',
              },
          searchResult: {
            uiConfig: {
              columns: [
                {
                  label: 'TQM_DEVICE_ID',
                  jsonPath: 'id',
                  additionalCustomization: false,
                },
                {
                  label: 'TQM_TREATMENT_PROCESS',
                  jsonPath: 'processCode',
                  additionalCustomization: false,
                  prefix:"PQM.ProcessType_",
                  translate:true
                },
                {
                  label: 'TQM_PROCESS_STAGE',
                  jsonPath: 'stageCode',
                  additionalCustomization: false,
                  prefix:"PQM.STAGE_",
                  translate:true
                },
                {
                  label: 'TQM_OUTPUT_TYPE',
                  jsonPath: 'materialCode',
                  additionalCustomization: false,
                  prefix:"PQM.MATERIAL_",
                  translate:true
                },
                {
                  label: 'TQM_LAST_CALIBRATED_DATE',
                  jsonPath: 'testType',
                  additionalCustomization: false,
                  prefix:"PQM.TESTTYPE_",
                  translate:true
                },
                {
                    label: 'TQM_DEVICE_STATUS',
                    jsonPath: 'testType',
                    additionalCustomization: false,
                    prefix:"PQM.TESTTYPE_",
                    translate:true
                },
                {
                    label: 'TQM_VERIFICATION_STATUS',
                    jsonPath: 'testType',
                    additionalCustomization: false,
                    prefix:"PQM.TESTTYPE_",
                    translate:true
                },
                {
                  label: 'TQM_LAST_VERIFICATION_DATE',
                  jsonPath: 'scheduledDate',
                  additionalCustomization: true,
                },
                {
                  label: 'TQM_PARAMETERS',
                  jsonPath: 'status',
                  additionalCustomization: true,
                  
                },
              ],
              showActionBarMobileCard: true,
              actionButtonLabelMobileCard: 'TQM_VIEW_RESULTS',
              enableGlobalSearch: false,
              enableColumnSort: true,
              resultsJsonPath: 'tests',
            },
            children: {},
            show: true,
          },
          filter: {
            uiConfig: {
              type: 'sort',
              headerStyle: null,
              headerLabel: 'TQM_INBOX_SORTBY',
              primaryLabel: 'TQM_INBOX_SORT',
              secondaryLabel: 'TQM_CLEAR_SEARCH',
              minReqFields: 0,
              defaultValues: {
                sortOrder: '',
              },
              fields: [
                {
                  label: '',
                  type: 'radio',
                  isMandatory: false,
                  disable: false,
                  populators: {
                    name: 'sortOrder',
                    options: [
                      {
                        code: 'LATEST_FIRST',
                        name: 'TQM_INBOX_LATEST_FIRST',
                        value:"ASC"
                      },
                      {
                        code: 'LATEST_LAST',
                        name: 'TQM_INBOX_LATEST_LAST',
                        value:"DESC"
                      },
                    ],
                    optionsKey: 'name',
                    styles: {
                      gap: '1rem',
                      flexDirection: 'column',
                    },
                    innerStyles: {
                      display: 'flex',
                    },
                  },
                },
              ],
            },
            label: 'Filter',
            labelMobile: 'TQM_INBOX_SORT',
            show: true,
          },
        },
        additionalSections: {},
      },
    ],
  };
  