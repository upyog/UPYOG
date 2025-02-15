//Main Config File designed for rendering of Components in multistep forms
export const commonConfig =
    [
        {
            "head": "ES_TITILE_OWNER_DETAILS",
            "body": [
                {
                    "route": "Existing-booking",
                    "component": "BookingPopup",
                    "nextStep": "info",
                    "key": "data"
                },
                {
                    "route": "info",
                    "component": "InfoPage",
                    "nextStep": "applicant-details",
                    "key": "infodetails"
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
                    "additionaFields":{
                    "gender":true,
                    "dateofBirth":false,
                    "guardianName":false,
                    "relationShipType":false,               }
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
        }
    ];