export const citizenConfig =
    [
        {
            "head": "EWASTE_TITLE_PRODUCT_DETAILS",
            "body": [
                {
                    "route": "productdetails",
                    "component": "EWASTEProductDetails",
                    "withoutLabel": true,
                    "key": "ewdet",
                    "type": "component",
                    "nextStep": "owner-details",
                    "hideInEmployee": true,
                    "isMandatory": true,
                    "texts": {
                        "submitBarLabel": "EWASTE_COMMON_NEXT",
                    }
                },
            ],
        },

        {
            "head": "EWASTE_TITLE_OWNER_DETAILS",
            "body": [
                {
                    "route": "owner-details",
                    "component": "EWASTEOwnerInfo",
                    "withoutLabel": true,
                    "key": "ownerKey",
                    "type": "component",
                    "isMandatory": true,
                    "hideInEmployee": true,
                    // "nextStep": "summary-ewaste",
                    "nextStep": "vendor-details",
                    "texts": {
                        "submitBarLabel": "EWASTE_COMMON_NEXT",
                    }
                },
            ],
        },

        {
            "head": "EWASTE_TITLE_VENDOR_DETAILS",
            "body": [
                {
                    "route": "vendor-details",
                    "component": "EWASTEVendorDetails",
                    "withoutLabel": true,
                    "key": "vendorKey",
                    "type": "component",
                    "isMandatory": true,
                    "hideInEmployee": true,
                    // "nextStep": "summary-ewaste",
                    "nextStep": "pincode",
                    "texts": {
                        "submitBarLabel": "EWASTE_COMMON_NEXT",
                    }
                },
            ],
        },

        {
            "head": "EWASTE_LOCATION_DETAILS",
            "body": 
            [
                {
                "route": "pincode",
                "component": "EWASTESelectPincode",
                "texts": {
                    
                    "submitBarLabel": "EWASTE_COMMON_NEXT",
                    "skipText": "CORE_COMMON_SKIP_CONTINUE",
                },
                "withoutLabel": true,
                "key": "address",
                "nextStep": "address",
                "type": "component",
                },

                {
                "route": "address",
                "component": "EWASTESelectAddress",
                "withoutLabel": true,
                "texts": {
                    
                    "submitBarLabel": "EWASTE_COMMON_NEXT",
                },
                "key": "address",
                "nextStep": "street",
                "isMandatory": true,
                "type": "component",
                },

                {
                "type": "component",
                "route": "street",
                "component": "EWASTECitizenAddress",
                "key": "address",
                "withoutLabel": true,
                "texts": {
                    "submitBarLabel": "EWASTE_COMMON_NEXT",
                },
                "nextStep": null,
                },

                
            ],
            }

    ];