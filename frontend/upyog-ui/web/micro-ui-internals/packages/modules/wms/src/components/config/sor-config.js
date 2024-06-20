export const newConfig =[
    {
        "head": "SOR-Details",
        "body": [
              {
                type: "component",
                component: "WmsSorId",
                key: "WmsSorId",
                withoutLabel: true,
              },//TODO: Done
              {
                type: "component",
                component: "WmsSorName",
                key: "WmsSorName",
                isMandatory: true,
                withoutLabel: true,
              },{
                type: "component",
                component: "WmsSorChapter",
                key: "WmsSorChapter",
                withoutLabel: true,
                isMandatory: true,//TODO: Need to ask
              },
              {
                type: "component",
                component: "WmsSorItemNo",
                key: "WmsSorItemNo",
                isMandatory: true,//TODO: Need to ask
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsSorDescriptionOfItem",
                key: "WmsSorDescriptionOfItem",
                isMandatory: true,//TODO: Need to ask
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsSorUnit",
                key: "WmsSorUnit",//TODO: Need to ask
                isMandatory: true,
                withoutLabel: true,
              },{
                type: "component",
                component: "WmsSorRate",//TODO: Need to ask
                key: "WmsSorRate",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsSorStartDate",//TODO: Need to ask
                key: "WmsSorStartDate",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsSorEndDate",//TODO: Need to ask
                key: "WmsSorEndDate",
                isMandatory: true,
                withoutLabel: true,
              },
        ]
    },
];