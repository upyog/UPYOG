export const Config =
[
    {
        "head": "ES_TITILE_OWNER_DETAILS",
        "body": [
            {
                "route":"info",
                "component":"SVRequiredDoc",
                "nextStep": "applicant-details",
                "key": "Documents"
            },
            {
                "route": "applicant-details",
                "component": "SVApplicantDetails",
                "withoutLabel": true,
                "key": "owner",
                "type": "component",
                "nextStep": "business-details",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "SV_VENDOR_PERSONAL_DETAILS",
                }
            },
            {
                "route": "business-details",
                "component": "SVBusinessDetails",
                "withoutLabel": true,
                "key": "businessDetails",
                "type": "component",
                "nextStep": "address-details",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "SV_VENDOR_BUSINESS_DETAILS",
                }
            },
            {
                "route": "address-details",
                "component": "SVAdrressDetails",
                "withoutLabel": true,
                "key": "address",
                "type": "component",
                "nextStep": "bank-details",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "SV_ADDRESS_DETAILS",
                }
            },
            {
                "route": "bank-details",
                "component": "SVBankDetails",
                "withoutLabel": true,
                "key": "bankdetails",
                "type": "component",
                "nextStep": "document-details",
                "hideInEmployee": true,
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "SV_BANK_DETAILS",
                }
            },
            {
                "route": "document-details",
                "component": "SVDocumentsDetail",
                "type": "component",
                "nextStep": null,
                "isMandatory": true,
                "key": "documents",
                "texts": {
                    "header": "SV_DOCUMENT_DETAILS_LABEL",
                    "submitBarLabel": "COMMON_SAVE_NEXT"
                }
            },
            
        ],
    },
];