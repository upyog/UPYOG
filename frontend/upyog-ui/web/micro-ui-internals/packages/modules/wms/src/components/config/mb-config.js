export const newConfig =[
    {
        "head": "MB-Details",
        "body": [
              {
                type: "component",
                component: "WmsMbId",
                key: "WmsMbId",
                withoutLabel: true,
              },//TODO: Done
              {
                type: "component",
                component: "WmsMbChapter",
                key: "WmsMbChapter",
                withoutLabel: true,
                isMandatory: true,//TODO: Need to ask
              },
              {
                type: "component",
                component: "WmsMbDescriptionOfItem",
                key: "WmsMbDescriptionOfItem",
                isMandatory: true,//TODO: Need to ask
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsMbDate",//TODO: Need to ask
                key: "WmsMbDate",
                isMandatory: true,
                withoutLabel: true,
              }
        ]
    },
];