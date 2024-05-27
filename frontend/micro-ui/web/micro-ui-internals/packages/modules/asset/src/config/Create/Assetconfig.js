export const newConfig =[
        
    {
        "head": "ASSET_GENERAL_DETAILS",
        "body": [
            {
                "component": "AssetClassification",
                "withoutLabel": true,
                "key": "assets",
                "type": "component"
            },
            {
                "route": "commonpage",
                "component": "AssetCommonDetails",
                "withoutLabel": true,
                "key": "assetcommonforall",
                "isMandatory": true,
                "type": "component"
            },
            {
                "component": "AssetCommonSelection",
                "withoutLabel": true,
                "key": "assetscommon",
                "type": "component",
                
            },
        ]
    },
    

    {
        "head": "AST_LOCATION_DETAILS",
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
        "head": "AST_DOCUMENT_DETAILS",
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