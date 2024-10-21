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
            {
                "route": "address",
                "component": "PTRSelectAddress",
                "withoutLabel": true,
                "key": "address",
                "nextStep": "street",
                "isMandatory": true,
                "type": "component"
            },
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