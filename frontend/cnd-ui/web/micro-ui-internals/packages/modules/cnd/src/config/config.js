export const Config =
[
    {
        "head": "ES_TITILE_OWNER_DETAILS",
        "body": [
            {
                "route":"info",
                "component":"CndRequirementDetails",
                "nextStep": "request-details",
                "key": "Documents"
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
                }
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
                }
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
                }
            },
            {
                "route": "waste-material-details",
                "component": "WasteType",
                "withoutLabel": true,
                "key": "wasteType",
                "type": "component",
                "nextStep": "address-details",
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "CND_WASTE_TYPE",
                }
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
                }
            }
        ],
    },
];