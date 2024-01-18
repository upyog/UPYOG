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
                component: "WmsWsrPercent",//TODO: Need to ask
                key: "WmsWsrPercent",
                isMandatory: true,
                withoutLabel: true,
              }
        ]
    },
];