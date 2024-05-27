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
                        "submitBarLabel": "PTR_COMMON_NEXT",
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
                        "submitBarLabel": "PTR_COMMON_NEXT",
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
                    "nextStep": null,
                    "texts": {
                        "submitBarLabel": "PTR_COMMON_NEXT",
                    }
                },
            ],
        },

    ];