export const newConfig =[
    {
      
        "head": "Work Status Report",
        "body": [
              {
                type: "component",
                component: "WmsWsrId",
                key: "WmsWsrId",
                withoutLabel: true,
              },//TODO: Done
              {
                type: "component",
                component: "WmsWsrPrjName",
                key: "WmsWsrPrjName",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsWsrWorkName",
                key: "WmsWsrWorkName",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsWsrActivity",
                key: "WmsWsrActivity",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsWsrEmployee",
                key: "WmsWsrEmployee",
                isMandatory: true,
                withoutLabel: true,
              },{
                type: "component",
                component: "WmsWsrRole",
                key: "WmsWsrRole",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsWsrStartDate",
                key: "WmsWsrStartDate",
                isMandatory: true,
                withoutLabel: true,
              },{
                type: "component",
                component: "WmsWsrEndDate",
                key: "WmsWsrEndDate",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsWsrRemarks",//TODO: Need to ask
                key: "WmsWsrRemarks",
                isMandatory: true,
                withoutLabel: true,
              }
        ]
    },
];