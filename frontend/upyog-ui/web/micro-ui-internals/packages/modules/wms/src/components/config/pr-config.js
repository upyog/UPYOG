export const newConfig =[
    {
      
        "head": "Project-Register",
        "body": [
              {
                type: "component",
                component: "WmsPrId",
                key: "WmsPrId",
                withoutLabel: true,
              },//TODO: Done
              {
                type: "component",
                component: "WmsPrSchName",
                key: "WmsPrSchName",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPrPrjName",
                key: "WmsPrPrjName",
                isMandatory: true,
                withoutLabel: true,
              },{
                type: "component",
                component: "WmsPrTypeOfWork",
                key: "WmsPrTypeOfWork",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPrWorkName",
                key: "WmsPrWorkName",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPrEstNumber",
                key: "WmsPrEstNumber",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPrEstWorkCost",
                key: "WmsPrEstWorkCost",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPrSTA",//TODO: Need to ask
                key: "WmsPrSTA",
                isMandatory: true,
                withoutLabel: true,
              }
              ,
              {
                type: "component",
                component: "WmsPrStatus",//TODO: Need to ask
                key: "WmsPrStatus",
                isMandatory: true,
                withoutLabel: true,
              },
              {
                type: "component",
                component: "WmsPrBillDate",//TODO: Need to ask
                key: "WmsPrBillDate",
                isMandatory: true,
                withoutLabel: true,
              }
              ,
              // {
              //   type: "component",
              //   component: "WmsPrPaymentDate",//TODO: Need to ask
              //   key: "WmsPrPaymentDate",
              //   isMandatory: true,
              //   withoutLabel: true,
              // }
        ]
    },
];