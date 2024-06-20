export const newConfig =[
    {
      
        "head": "Deduction-Register",
        "body": [
              {
                type: "component",
                component: "WmsDrId",
                key: "WmsDrId",
                withoutLabel: true,
              },//TODO: Done
              {
                type: "component",
                component: "WmsDrPrjName",
                key: "WmsDrPrjName",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsDrWorkName",
                key: "WmsDrWorkName",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsDrMLName",
                key: "WmsDrMLName",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsDrPercent",//TODO: Need to ask
                key: "WmsDrPercent",
                isMandatory: true,
                withoutLabel: true,
              }
        ]
    },
];