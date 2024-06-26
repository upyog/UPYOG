export const newAssetConfig =
[
    {
    "head": "ES_TITILE_OWNER_DETAILS",
    "body": [
        {
            "route":"info",
            "component":"ServiceDoc",
            "nextStep": "asset-deatils",
            "key": "Documents"
        },

        
        {
        "route": "asset-deatils",
        "component": "NewAssetClassification",
        "withoutLabel": true,
        "key": "asset",
        "type": "component",
        "nextStep": "assets",
        "hideInEmployee": true,
        "isMandatory": true,
        "texts": {
            "header": "ASSET_GENERAL_DETAILS",
            "submitBarLabel": "COMMON_NEXT",
        }
        },
    ],
    },

    {
    "head": "TITLE_ASSET_DEATIL",
    "body": [
        {
        "route": "assets",
        "component": "NewAsset",
        "withoutLabel": true,
        "key": "assetDetails",
        "type": "component",
        "isMandatory": true,
        "hideInEmployee": true,
        "nextStep": "pincode",
        "texts": {
            "header": "AST_DETAILS",
            "submitBarLabel": "COMMON_NEXT",
        }
        },
    ],
    },
    
    {
    "head": "AST_ADDRESS_DETAILS",
    "body": 
    [
        {
        "route": "pincode",
        "component": "AssetPincode",
        "texts": {
            "header": "AST_ADDRESS_DETAILS",
            "submitBarLabel": "COMMON_NEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE",
        },
        "withoutLabel": true,
        "key": "address",
        "nextStep": "address",
        "type": "component",
        },

        {
        "route": "address",
        "component": "AssetAddress",
        "withoutLabel": true,
        "texts": {
            "header": "AST_ADDRESS_DETAILS",
            "submitBarLabel": "COMMON_NEXT",
        },
        "key": "address",
        "nextStep": "documents",
        "isMandatory": true,
        "type": "component",
        },

        {
        "type": "component",
        "route": "street",
        "component": "AssetStreets",
        "key": "address",
        "withoutLabel": true,
        "texts": {
            "header": "AST_ADDRESS_DETAILS",
            "submitBarLabel": "COMMON_NEXT",
        },
        "nextStep": "documents",
        },

        
    ],
    },
        

    {
    "head": "ES_TITILE_DOCUMENT_DETAILS",
    "body": [
        {
            "route": "documents",
            "component": "NewDocument",
            "withoutLabel": true,
            "key": "documents",
            "type": "component",
            "nextStep":null,
            "texts": {
                "header":"AST_DOCUMENT_DETAILS",
                "submitBarLabel": "COMMON_NEXT",
            },
            
        }
    ],
    },
];