export const newConfig =[
    {
      
        "head": "Scheme-Details",
        "body": [
              {
                type: "component",
                component: "WmsSchId",
                key: "WmsSchId",
                withoutLabel: true,
              },//TODO: Done
              {
                type: "component",
                component: "WmsSchNameEn",
                key: "WmsSchNameEn",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsSchNameReg",
                key: "WmsSchNameReg",
                isMandatory: true,
                withoutLabel: true,
              },{
                type: "component",
                component: "WmsSchFund",//TODO: Need to ask
                key: "WmsSchFund",
                isMandatory: true,
                withoutLabel: true,
              },{
                type: "component",
                component: "WmsSchSourceOfFund",//TODO: Need to ask
                key: "WmsSchSourceOfFund",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsSchDescriptionOfScheme",//TODO: Need to ask
                key: "WmsSchDescriptionOfScheme",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsSchStartDate",//TODO: Need to ask
                key: "WmsSchStartDate",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsSchEndDate",//TODO: Need to ask
                key: "WmsSchEndDate",
                isMandatory: true,
                withoutLabel: true,
              }
        ]
    },
];