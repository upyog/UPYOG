export const citizenConfig = [
    {
        "head": "ES_TITILE_AADHAR_VERIFICATION",
        "body": [
            {
                "route": "aadhaar",
                "component": "AadhaarVerification",
                "withoutLabel": true,
                "key": "aadhaar",
                "type": "component",
                "nextStep": "otpverification",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "PTR_COMMON_VERIFY",
                }
            }, 
            {
                "route": "aadhaarForm",
                "component": "AadhaarFullForm",
                "withoutLabel": true,
                "key": "AadhaarFullForm",
                "type": "component",
                "nextStep": "selectScheme",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "PTR_COMMON_CONFIRM",
                }
            }]
    }, 
    {
        "head": "ES_TITILE_SELECT_SCHEME",
        "body": [
            {
                "route": "selectScheme",
                "component": "SelectSchemePage",
                "withoutLabel": true,
                "key": "SelectScheme",
                "type": "component",
                "nextStep": "egibilityCheck",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "PTR_COMMON_NEXT",
                }
            }
        ]
    }, 
    {
        "head": "ES_TITILE_EGIBILITY_CHECK",
        "body": [
            {
                "route": "egibilitycheck",
                "component": "EgibilityCheckPage",
                "withoutLabel": true,
                "key": "EgibilityCheck",
                "type": "component",
                "nextStep": "ApplicationDetails",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "PTR_COMMON_NEXT",
                }
            }
        ]
    },
    {
        "head": "ES_TITILE_OWNER_DETAILS",
        "body": [
            {
                "route": "ApplicationDetails",
                "component": "ApplicationDetail",
                "withoutLabel": true,
                "key": "ApplicationDetails",
                "type": "component",
                "nextStep": "review",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "PTR_COMMON_NEXT",
                }
            }
        ]
    },
    {
        "head": "ES_TITILE_BMC_REVIEW",
        "body": [
            {
                "route": "review",
                "component": "BMCReviewPage",
                "withoutLabel": true,
                "key": "BMCReview",
                "type": "component",
                "nextStep": null,
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "PTR_COMMON_SUBMIT",
                }
            }
        ]
    }
]