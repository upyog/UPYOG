export const addTestConfig = [
  // {
  //   body: [
  //     {
  //       isMandatory: true,
  //       key: "plantCode",
  //       type: "radioordropdown",
  //       label: "TQM_PLANT_NAME",
  //       disable: false,
  //       populators: {
  //         name: "plantCode",
  //         optionsKey: "i18nKey",
  //         error: "ES_TQM_REQUIRED",
  //         required: true,
  //         mdmsv2: {
  //           schemaCode: "PQM.Plant",
  //         }
  //       },
  //     },
  //     {
  //       isMandatory: true,
  //       key: "processCode",
  //       type: "radioordropdown",
  //       label: "TQM_TREATMENT_PROCESS",
  //       disable: false,
  //       populators: {
  //         name: "processCode",
  //         optionsKey: "i18nKey",
  //         error: "ES_TQM_REQUIRED",
  //         required: true,
  //         mdmsv2: {
  //           schemaCode: "PQM.Process",
  //         }
  //       },
  //     },
  //     {
  //       isMandatory: true,
  //       key: "stageCode",
  //       type: "radioordropdown",
  //       label: "TQM_PROCESS_STAGE",
  //       disable: false,
  //       populators: {
  //         name: "stageCode",
  //         optionsKey: "i18nKey",
  //         error: "ES_TQM_REQUIRED",
  //         required: true,
  //         mdmsv2: {
  //           schemaCode: "PQM.Stage",
  //         }
  //       },
  //     },
  //     {
  //       isMandatory: true,
  //       key: "materialCode",
  //       type: "radioordropdown",
  //       label: "TQM_OUTPUT_TYPE",
  //       disable: false,
  //       populators: {
  //         name: "materialCode",
  //         optionsKey: "i18nKey",
  //         error: "ES_TQM_REQUIRED",
  //         required: true,
  //         mdmsv2: {
  //           schemaCode: "PQM.Material",
  //         }
  //       },
  //     }
  //   ],
  // },
  {
    body: [
      {
        type: "component",
        component: "TestStandard",
        "withoutLabel": true,
        "key": "TestStandard",
        "customProps": {
          "module": "TQMModule"
        }
      }
    ]
  },
  {
    body: [
      {
        type: "component",
        component: "QualityParameter",
        "withoutLabel": true,
        "key": "QualityParameter",
        "customProps": {
          "module": "TQMModule"
        }
      }
    ]
  }

];