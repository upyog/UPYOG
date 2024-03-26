export const newConfig =[
    {
      
        "head": "Milestone-Details",
        "body": [
              {
                type: "component",
                component: "WmsPmaId",
                key: "WmsPmaId",
                withoutLabel: true,
              },//TODO: Done
              {
                type: "component",
                component: "WmsPmaDescriptionOfItem",
                key: "WmsPmaDescriptionOfItem",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPmaPercent",
                key: "WmsPmaPercent",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPmaStartDate",
                key: "WmsPmaStartDate",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPmaEndDate",//TODO: Need to ask
                key: "WmsPmaEndDate",
                isMandatory: true,
                withoutLabel: true,
              }
        ]
    },
];