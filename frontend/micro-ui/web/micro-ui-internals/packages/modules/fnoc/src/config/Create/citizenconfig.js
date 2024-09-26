export const citizenConfig =
[
    {
    "head": "ES_TITILE_OWNER_DETAILS",
    "body": [
        {
            "route":"info",
            "component":"FNOCServiceDoc",
            "nextStep": "building-details",
            "key": "Documents"
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
            "nextStep": "property-details",
            "hideInEmployee": true,
            "isMandatory": true,
            "texts": {
                "submitBarLabel": "COMMON_NEXT",
                "header": "PROPERTY_DETAILS_HEADER",
            }
            },

    
    // {
    // "head": "PTR_LOCATION_DETAILS",
    // "body": 
    // [
    //     {
    //     "route": "pincode",
    //     "component": "PTRSelectPincode",
    //     "texts": {
            
    //         "submitBarLabel": "PTR_COMMON_NEXT",
    //         "skipText": "CORE_COMMON_SKIP_CONTINUE",
    //     },
    //     "withoutLabel": true,
    //     "key": "address",
    //     "nextStep": "address",
    //     "type": "component",
    //     },

    //     {
    //     "route": "address",
    //     "component": "PTRSelectAddress",
    //     "withoutLabel": true,
    //     "texts": {
            
    //         "submitBarLabel": "PTR_COMMON_NEXT",
    //     },
    //     "key": "address",
    //     "nextStep": "street",
    //     "isMandatory": true,
    //     "type": "component",
    //     },

    //     {
    //     "type": "component",
    //     "route": "street",
    //     "component": "PTRCitizenAddress",
    //     "key": "address",
    //     "withoutLabel": true,
    //     "texts": {
    //         "submitBarLabel": "PTR_COMMON_NEXT",
    //     },
    //     "nextStep": "documents",
    //     },

        
    // ],
    // },
        

    // {
    // "head": "ES_TITILE_DOCUMENT_DETAILS",
    // "body": [
    //     {
    //         "route": "documents",
    //         "component": "PTRSelectProofIdentity",
    //         "withoutLabel": true,
    //         "key": "documents",
    //         "type": "component",
    //         "nextStep":null,
    //         "texts": {
    //             "submitBarLabel": "PTR_COMMON_NEXT",
    //         },
            
    //     }
    ],
    },
];