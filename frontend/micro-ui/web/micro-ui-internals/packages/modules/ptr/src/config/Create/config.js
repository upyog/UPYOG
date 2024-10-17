export const newConfig = [

    {
        "head": "ES_TITILE_OWNER_DETAILS",
        "body": [
            {
                "component": "PTROwnerDetails",
                "withoutLabel": true,
                "key": "owners",
                "type": "component"
            }
        ]
    },

    {
        "head": "ES_TITILE_PET_DETAILS",
        "body": [
            {
                "component": "PTRPetdetails",
                "withoutLabel": true,
                "key": "pets",
                "type": "component"
            }
        ]
    },

    {
        "head": "ES_TITILE_PROPERTY_SEARCH",
        "body": [
            {
                "component": "PropertySearch",
                "withoutLabel": true,
                "key": "propertyDetails",
                "type": "component"
            }
        ]
    },

    {
        "head": "PTR_LOCATION_DETAILS",
        "body": [

            // {
            //     "route": "pincode",
            //     "component": "PTRSelectPincode",
            //     "texts": {
            //         "headerCaption": "PTR_CAPTION",
            //         "header": "PTR_PINCODE_LABEL",
            //         "cardText": "PTR_PINCODE_TEXT",
            //         "submitBarLabel": "PTR_COMMON_NEXT",
            //         "skipText": "CORE_COMMON_SKIP_CONTINUE"
            //     },
            //     "withoutLabel": true,
            //     "key": "address",
            //     "nextStep": "address",
            //     "type": "component"
            // },
            {
                "route": "address",
                "component": "PTRSelectAddress",
                "withoutLabel": true,
                "key": "address",
                "nextStep": "street",
                "isMandatory": true,
                "type": "component"
            },
            // {
            //     "type": "component",
            //     "route": "street",
            //     "component": "PTRSelectStreet",
            //     "key": "address",
            //     "withoutLabel": true,
            //     "nextStep": "landmark"
            // },
            // {
            //     "type": "component",
            //     "route": "landmark",
            //     "component": "PTSelectLandmark",
            //     "withoutLabel": true,
            //     "key": "address",
            //     // "nextStep": "proof",
            //     "hideInEmployee": true
            // },

        ]
    },
    {
        "head": "ES_TITILE_DOCUMENT_DETAILS",
        "body": [
            {
                "component": "PTRDocumentUpload",
                "withoutLabel": true,
                "key": "documents",
                "type": "component"
            }
        ]
    },




];