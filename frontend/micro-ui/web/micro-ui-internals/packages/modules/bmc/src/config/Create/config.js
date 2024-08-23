export const newConfig = [
    {
        "head": "ES_TITILE_AADHAR_VERIFICATION",
        "body": [
            {
                "route": "aadhaar",
                "component": "AadhaarVerification",
                "texts": {
                    "submitBarLabel": "PTR_COMMON_VERIFY",
                },
                "withoutLabel": true,
                "key": "aadhaar",
                "type": "component",
                "nextStep": "otpverification",
            },
            {
                "route": "aadhaarForm",
                "component": "AadhaarFullForm",
                "withoutLabel": true,
                "key": "AadhaarFullForm",
                "type": "component",
                "nextStep": "selectScheme",
                "texts": {
                    "submitBarLabel": "PTR_COMMON_CONFIRM",
                }
            }]
    }, {
        "head": "ES_TITILE_SELECT_SCHEME",
        "body": [
            {
                "route": "selectScheme",
                "component": "SelectSchemePage",
                "withoutLabel": true,
                "key": "SelectScheme",
                "type": "component",
                "nextStep": "egibilityCheck",
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
                "component": "EligibilityCheck",
                "withoutLabel": true,
                "key": "EligibilityCheck",
                "type": "component",
                "nextStep": "ApplicationDetails",
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
                "route": "ApplicationDetail",
                "component": "ApplicationDetail",
                "withoutLabel": true,
                "key": "ApplicationDetail",
                "type": "component",
                "nextStep": "review",
                "texts": {
                    "submitBarLabel": "PTR_COMMON_NEXT",
                }
            }
        ]
    },
    {
        "head": "ES_TITILE_REVIEW",
        "body": [
            {
                "component": "Review",
                "withoutLabel": true,
                "key": "Review",
                "type": "component",
                "nextStep": "response",
                "texts": {
                    "submitBarLabel": "PTR_COMMON_SUBMIT",
                }
            }
        ]
    }
]