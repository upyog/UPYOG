export const newConfig =[
    {
      
        "head": "Deduction-Register",
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
                component: "WmsWsrMLName",
                key: "WmsWsrMLName",
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