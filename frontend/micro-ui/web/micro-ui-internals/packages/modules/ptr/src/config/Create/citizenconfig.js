export const citizenConfig =
    [
        {
            "head": "ES_TITILE_OWNER_DETAILS",
            "body": [
                {
                    "route": "info",
                    "component": "PTRServiceDoc",
                    "nextStep": "owners",
                    "key": "Documents"
                },


                {
                    "route": "owners",
                    "component": "PTRCitizenDetails",
                    "withoutLabel": true,
                    "key": "ownerss",
                    "type": "component",
                    "nextStep": "pet-details",
                    "hideInEmployee": true,
                    "isMandatory": true,
                    "texts": {
                        "submitBarLabel": "PTR_COMMON_NEXT",
                    }
                },
            ],
        },

        {
            "head": "ES_TITILE_PET_DETAILS",
            "body": [
                {
                    "route": "pet-details",
                    "component": "PTRCitizenPet",
                    "withoutLabel": true,
                    "key": "pets",
                    "type": "component",
                    "isMandatory": true,
                    "hideInEmployee": true,
                    "nextStep": "property-details",
                    "texts": {
                        "submitBarLabel": "PTR_COMMON_NEXT",
                    }
                },
            ],
        },

        {
            "head": "PTR_LOCATION_DETAILS",
            "body":
                [
                    {
                        "route": "property-details",
                        "component": "PropertySearch",
                        "withoutLabel": true,
                        "key": "propertyDetails",
                        "type": "component",
                        "nextStep": "address",
                        "hideInEmployee": true,
                        "isMandatory": true,
                        "texts": {
                            "submitBarLabel": "COMMON_NEXT",
                            "header": "PROPERTY_DETAILS_HEADER",
                        }
                    },
                    // {
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
                    // },

                    {
                        "route": "address",
                        "component": "PTRSelectAddress",
                        "withoutLabel": true,
                        "texts": {

                            "submitBarLabel": "PTR_COMMON_NEXT",
                        },
                        "key": "address",
                        "nextStep": "documents",
                        "isMandatory": true,
                        "type": "component",
                    },

                    // {
                    //     "type": "component",
                    //     "route": "street",
                    //     "component": "PTRCitizenAddress",
                    //     "key": "address",
                    //     "withoutLabel": true,
                    //     "texts": {
                    //         "submitBarLabel": "PTR_COMMON_NEXT",
                    //     },
                    //     "nextStep": "documents",
                    // },


                ],
        },


        {
            "head": "ES_TITILE_DOCUMENT_DETAILS",
            "body": [
                {
                    "route": "documents",
                    "component": "PTRSelectProofIdentity",
                    "withoutLabel": true,
                    "key": "documents",
                    "type": "component",
                    "nextStep": null,
                    "texts": {
                        "submitBarLabel": "PTR_COMMON_NEXT",
                    },

                }
            ],
        },
    ];