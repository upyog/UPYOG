export const newConfig =[
    {
      
        "head": "Milestone-Details",
        "body": [
              {
                type: "component",
                component: "WmsPhmId",
                key: "WmsPhmId",
                withoutLabel: true,
              },//TODO: Done
              {
                type: "component",
                component: "WmsPhmPrjName",
                key: "WmsPhmPrjName",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPhmWorkName",
                key: "WmsPhmWorkName",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPhmMLName",
                key: "WmsPhmMLName",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPhmPercent",//TODO: Need to ask
                key: "WmsPhmPercent",
                isMandatory: true,
                withoutLabel: true,
              }
        ]
    },
];