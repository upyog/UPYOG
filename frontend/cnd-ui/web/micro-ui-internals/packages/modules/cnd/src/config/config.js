export const Config =
[
    {
        "head": "ES_TITILE_OWNER_DETAILS",
        "body": [
            {
                "route":"info",
                "component":"CndRequirementDetails",
                "nextStep": "request-details",
                "key": "Documents",
            },
            {
                "route": "request-details",
                "component": "RequestPickup",
                "withoutLabel": true,
                "key": "requestType",
                "type": "component",
                "nextStep": "applicant-details",
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "CND_WASTE_PICKUP_DEPOSIT",
                },
                "timeLine":[{
                    "currentStep":1,
                    "actions":"CND_WASTE_PICKUP_DEPOSIT"
                }]
            },
            {
                "route": "applicant-details",
                "component": "ApplicantDetails",
                "withoutLabel": true,
                "key": "owner",
                "type": "component",
                "nextStep": "property",
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "COMMON_PERSONAL_DETAILS",
                },
                "timeLine":[{
                    "currentStep":2,
                    "actions":"COMMON_PERSONAL_DETAILS"
                }]
            },
            {
                "route": "property",
                "component": "PropertyNature",
                "withoutLabel": true,
                "key": "propertyNature",
                "type": "component",
                "nextStep": "waste-material-details",
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "CND_NATURE_PROPERTY",
                },
                "timeLine":[{
                    "currentStep":3,
                    "actions":"CND_NATURE_PROPERTY"
                }]
            },
            {
                "route": "waste-material-details",
                "component": "WasteType",
                "withoutLabel": true,
                "key": "wasteType",
                "type": "component",
                "nextStep": "select-address-details",
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "CND_WASTE_TYPE",
                },
                "timeLine":[{
                    "currentStep":4,
                    "actions":"CND_WASTE_TYPE"
                }]
            },
            {
                "route": "select-address-details",
                "component": "Address",
                "withoutLabel": true,
                "key": "addressDetails",
                "type": "component",
                "nextStep": null,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "SELECT_ADDRESS_DETAILS",
                },
                "timeLine":[{
                    "currentStep":5,
                    "actions":"ADDRESS_DEATILS"
                }]
            },
            {
                "route": "address-details",
                "component": "AddressDetails",
                "withoutLabel": true,
                "key": "address",
                "type": "component",
                "nextStep": null,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "ADDRESS_DEATILS",
                },
                "timeLine":[{
                    "currentStep":5,
                    "actions":"ADDRESS_DEATILS"
                }]
            }
        ],
    },
];