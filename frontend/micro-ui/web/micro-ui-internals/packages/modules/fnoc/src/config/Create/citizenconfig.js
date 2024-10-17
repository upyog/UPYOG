export const citizenConfig =
[
    {
        "head": "ES_TITILE_OWNER_DETAILS",
        "body": [
            {
                "route":"info",
                "component":"FNOCServiceDoc",
                "nextStep": "common-details",
                "key": "Documents"
            },
            {
                "route": "common-details",
                "component": "FNOCCommonDetails",
                "withoutLabel": true,
                "key": "commonDetails",
                "type": "component",
                "nextStep": "building-details",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_NEXT",
                    "header": "COMMON_DETAILS_HEADER",
                }
            },
            {
            "route": "building-details",
            "component": "FNOCBuildingDetails",
            "withoutLabel": true,
            "key": "buildings",
            "type": "component",
            "nextStep": "property-details",
            "hideInEmployee": true,
            "isMandatory": true,
            "texts": {
                "submitBarLabel": "COMMON_NEXT",
                "header": "BUILDING_DETAILS_HEADER",
            }
            },
            {
                "route": "property-details",
                "component": "PropertySearch",
                "withoutLabel": true,
                "key": "propertyDetails",
                "type": "component",
                "nextStep": "application-details",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_NEXT",
                    "header": "PROPERTY_DETAILS_HEADER",
                }
            },
            {
                "route": "application-details",
                "component": "FNOCApplicationDetails",
                "withoutLabel": true,
                "key": "application",
                "type": "component",
                "nextStep": "documents",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_NEXT",
                    "header": "NOC_COMMON_APPLICANT_DETAILS",
                }
            },
            {
                "route": "documents",
                "component": "FNOCDocuments",
                "withoutLabel": true,
                "key": "documents",
                "type": "component",
                "nextStep":null,
                "texts": {
                    "header":"FNOC_DOCUMENT_DETAILS",
                    "submitBarLabel": "COMMON_NEXT",
                },
                
            }
        ],
    },
];