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
                "component": "Pickup",
                "withoutLabel": true,
                "key": "requestType",
                "type": "component",
                "nextStep": "construction-details",
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "CND_WASTE_PICKUP_DEPOSIT",
                }
            },
            {
                "route": "construction-details",
                "component": "ConstructionType",
                "withoutLabel": true,
                "key": "constructionType",
                "type": "component",
                "nextStep": "applicant-details",
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "CND_TYPE_CONSTRUCTION",
                }
            },
            {
                "route": "applicant-details",
                "component": "ApplicantDetails",
                "withoutLabel": true,
                "key": "owner",
                "type": "component",
                "nextStep": "business-details",
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "COMMON_PERSONAL_DETAILS",
                }
            }
        ],
    },
];