export const procurementRequestConfig =
[
    {
    "head": "ES_TITILE_OWNER_DETAILS",
    "body": [
        {
            "route": "create",
            "component": "ProcurementReq",
            "withoutLabel": true,
            "key": "procurementReq",
            "type": "component",
           "nextStep":null,
            "hideInEmployee": true,
            "isMandatory": true,
            "texts": {
                "header": "ADD_INVENTORY_PROCEREMENT_REQUEST",
                "submitBarLabel": "COMMON_NEXT",
            }
        },
    ],
    }
];