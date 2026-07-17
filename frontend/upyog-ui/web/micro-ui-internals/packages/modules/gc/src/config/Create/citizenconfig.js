export const commonConfig =
    [
        {
            "head": "ES_TITILE_OWNER_DETAILS",
            "body": [
                {
                    "route": "applicant-details",
                    "component": "ApplicantDetails",
                    "withoutLabel": true,
                    "key": "owner",
                    "type": "component",
                    "nextStep": "garbage-propertyLocation",
                    "hideInEmployee": true,
                    "isMandatory": true,
                    "texts": {
                        "submitBarLabel": "COMMON_SAVE_NEXT",
                        "header": "ES_APPLICANT_DETAILS"
                    },
                    "timeLine": [{
                        "currentStep": 1,
                        "actions": "ES_APPLICANT_DETAILS"
                    }]

                },
                {
                    "route": "garbage-propertyLocation",
                    "component": "GCPropertyLocDetails",
                    "withoutLabel": true,
                    "key": "gcpropertylocdetails",
                    "type": "component",
                    "nextStep": "garbage-specifications",
                    "hideInEmployee": true,
                    "isMandatory": true,
                    "texts": {
                        "submitBarLabel": "COMMON_SAVE_NEXT",
                        "header": "GC_GARBAGE_PROPERTY_LOCATION"
                    },
                    "timeLine": [{
                        "currentStep": 2,
                        "actions": "GC_GARBAGE_PROPERTY_LOCATION"
                    }]
                },
                {
                    "route": "garbage-specifications",
                    "component": "GCSpecifications",
                    "withoutLabel": true,
                    "key": "gcspecifications",
                    "type": "component",
                    "nextStep": "garbage-special-category",
                    "hideInEmployee": true,
                    "isMandatory": true,
                    "texts": {
                        "submitBarLabel": "COMMON_SAVE_NEXT",
                        "header": "GC_GARBAGE_SPECIFICATIONS"
                    },
                    "timeLine": [{
                        "currentStep": 3,
                        "actions": "GC_GARBAGE_SPECIFICATIONS"
                    }]
                },
                {
                    "route": "garbage-special-category",
                    "component": "GCSpecialCategory",
                    "withoutLabel": true,
                    "key": "gcspecialcategory",
                    "type": "component",
                    "nextStep": "garbage-documents",
                    "hideInEmployee": true,
                    "isMandatory": true,
                    "texts": {
                        "submitBarLabel": "COMMON_SAVE_NEXT",
                        "header": "GC_GARBAGE_SPECIAL_CATEGORY"
                    },
                    "timeLine": [{
                        "currentStep": 4,
                        "actions": "GC_GARBAGE_SPECIAL_CATEGORY"
                    }]
                },
                {
                    "route": "garbage-documents",
                    "component": "GCDocumentDetails",
                    "withoutLabel": true,
                    "key": "gcdocuments",
                    "type": "component",
                    "nextStep": null,
                    "hideInEmployee": true,
                    "isMandatory": true,
                    "texts": {
                        "submitBarLabel": "COMMON_SAVE_NEXT",
                        "header": "GC_GARBAGE_DOCUMENTS"
                    },
                    "timeLine": [{
                        "currentStep": 5,
                        "actions": "GC_GARBAGE_DOCUMENTS"
                    }]
                },
            ],
        }
    ];