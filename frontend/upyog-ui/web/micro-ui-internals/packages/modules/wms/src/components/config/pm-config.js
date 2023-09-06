export const newConfig =[
    
    {
      "head": "PM-Details",
      "body": [
            {
              type: "component",
              component: "WmsPmId",
              key: "WmsPmId",
              withoutLabel: true,
            },//TODO: Done
            {
              type: "component",
              component: "WmsPmPrjName",
              key: "WmsPmPrjName",
              isMandatory: true,
              withoutLabel: true,
            },{
              type: "component",
              component: "WmsPmMlName",
              key: "WmsPmMlName",
              withoutLabel: true,
              isMandatory: true,//TODO: Need to ask
            },
            {
              type: "component",
              component: "WmsPmWrkName",
              key: "WmsPmWrkName",
              isMandatory: false,//TODO: Need to ask
              withoutLabel: true,
            },{
              type: "component",
              component: "WmsPmPer",
              key: "WmsPmPer",
              isMandatory: false,//TODO: Need to ask
              withoutLabel: true,
            },
      ]
  },
];