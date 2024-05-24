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
                    "nextStep": null,
                    "texts": {
                        "submitBarLabel": "PTR_COMMON_NEXT",
                    }
                },
            ],
        },

        // {
        //     "head": "EWASTE_TITLE_SUMMARY",
        //     "body": [
        //         {
        //             "route": "summary-ewaste",
        //             "component": "EWASTESummary",
        //             "withoutLabel": true,
        //             "key": "summaryKey",
        //             "type": "component",
        //             "isMandatory": true,
        //             "hideInEmployee": true,
        //             "nextStep": null,
        //             "texts": {
        //                 "submitBarLabel": "PTR_COMMON_NEXT",
        //             }
        //         },
        //     ],
        // },

    ];