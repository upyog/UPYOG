//Main Config File designed for rendering of Components in multistep forms
export const commonConfig =
    [
        {
            "head": "ES_TITILE_OWNER_DETAILS",
            "body": [
                {
                    "route": "info",
                    "component": "InfoPage",
                    "nextStep": "service-type",
                    "key": "infodetails"
                },
                {
                    "route": "service-type",
                    "component": "ServiceTypes",
                    "withoutLabel": true,
                    "key": "serviceType",
                    "type": "component",
                    "nextStep": "applicant-details",
                    "hideInEmployee": true,
                    "isMandatory": true,
                    "texts": {
                        "submitBarLabel": "COMMON_SAVE_NEXT",
                        "header":"ES_SERVICE_TYPE"
                    }
                },
                {
                    "route": "applicant-details",
                    "component": "ApplicantDetails",
                    "withoutLabel": true,
                    "key": "owner",
                    "type": "component",
                    "nextStep": "address-details",
                    "hideInEmployee": true,
                    "isMandatory": true,
                    "texts": {
                        "submitBarLabel": "COMMON_SAVE_NEXT",
                        "header":"ES_APPLICANT_DETAILS"
                    },
                   
                },
            ],
        },
        {
            "head": "ES_TITLE_ADDRESS_DETAILS",
            "body": [
                {
                    "route": "address-details",
                    "component": "AddressDetails",
                    "withoutLabel": true,
                    "key": "address",
                    "type": "component",
                    "isMandatory": true,
                    "hideInEmployee": true,
                    "nextStep": "request-details",
                    "texts": {
                        "submitBarLabel": "COMMON_SAVE_NEXT",
                        "header":"ES_ADDRESS_DETAILS"
                    }
                },
            ],
        },
        {
            "head": "ES_REQUEST_DETAILS",
            "body": [
                {
                    "route": "request-details",
                    "component": "RequestDetails",
                    "withoutLabel": true,
                    "key": "requestDetails",
                    "type": "component",
                    "isMandatory": true,
                    "hideInEmployee": true,
                    "nextStep": null,
                    "texts": {
                        "submitBarLabel": "COMMON_SAVE_NEXT",
                        "header":"ES_REQUEST_DETAILS"
                    }
                },
            ],
        },
        {
            "head": "ES_REQUEST_DETAILS",
            "body": [
                {
                    "route": "toiletRequest-details",
                    "component": "ToiletRequestDetails",
                    "withoutLabel": true,
                    "key": "toiletRequestDetails",
                    "type": "component",
                    "isMandatory": true,
                    "hideInEmployee": true,
                    "nextStep":null,
                    "texts": {
                        "submitBarLabel": "COMMON_SAVE_NEXT",
                        "header":"ES_REQUEST_DETAILS"
                    }
                },
            ],
        },
    ];