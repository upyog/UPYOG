export const newConfig =[
        
    {
        "head": "ASSET_GENERAL_DETAILS",
        "body": [
            {
                "route": "commonpage",
                "component": "AssetCommonDetails",
                "withoutLabel": true,
                "key": "assetcommonforall",
                "isMandatory": true,
                "type": "component"
            },
            {
                "component": "AssetClassification",
                "withoutLabel": true,
                "key": "assets",
                "type": "component"
            }
        ]
    },
    {
        "head": "ES_TITILE_ASSET_COMMON_SELECTION",
        "body": [
            {
                "component": "AssetCommonSelection",
                "withoutLabel": true,
                "key": "assetscommon",
                "type": "component",
                
            },
        ]
    },

    {
        "head": "ASSET_LOCATION_DETAILS",
        "body": [
           
            {
                "route": "pincode",
                "component": "AssetPincode",
                "withoutLabel": true,
                "key": "address",
                "nextStep": "address",
                "type": "component"
            },
            {
                "route": "address",
                "component": "AssetAddress",
                "withoutLabel": true,
                "key": "address",
                "nextStep": "commonpage",
                "isMandatory": true,
                "type": "component"
            },
            {
                "route": "address",
                "component": "AssetStreets",
                "withoutLabel": true,
                "key": "address",
                "nextStep": "commonpage",
                "isMandatory": true,
                "type": "component"
            },           
        ]
    },
    {
        "head": "ES_TITILE_DOCUMENT_DETAILS",
        "body": [
            {
                "component": "AssetDocuments",
                "withoutLabel": true,
                "key": "documents",
                "type": "component"
            }
        ]
    },

       
       
        
];