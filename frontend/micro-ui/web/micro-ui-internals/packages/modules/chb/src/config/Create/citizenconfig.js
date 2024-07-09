                export const citizenConfig =
                [
                    {
                    "head": "ES_TITILE_APPLICANT_DETAILS",
                    "body": [
                        {
                            "route":"searchhall",
                            "component":"CHBSearchHall",
                            "nextStep": "applicant-details",
                            "key": "slotlist",
                            "type": "component"
                        },

                        
                        {
                        "route": "applicant-details",
                        "component": "CHBCitizenDetails",
                        "withoutLabel": true,
                        "key": "ownerss",
                        "type": "component",
                        "nextStep": "slot-details",
                        "hideInEmployee": true,
                        "isMandatory": true,
                        "texts": {
                            "submitBarLabel": "CHB_COMMON_NEXT",
                        }
                        },
                    ],
                    },

                    {
                    "head": "ES_TITILE_SLOT_DETAILS",
                    "body": [
                        {
                        "route": "slot-details",
                        "component": "CHBSlotDetails",
                        "withoutLabel": true,
                        "key": "slots",
                        "type": "component",
                        "isMandatory": true,
                        "hideInEmployee": true,
                        "nextStep": "address-details",
                        "texts": {
                            "submitBarLabel": "CHB_COMMON_NEXT",
                        }
                        },
                    ],
                    },
                    {
                    "head": "CHB_ADDRESS_DETAILS",
                    "body": [
                        {
                        "route": "address-details",
                        "component": "CHBAddressDetails",
                        "withoutLabel": true,
                        "key": "address",
                        "type": "component",
                        "isMandatory": true,
                        "hideInEmployee": true,
                        "nextStep": "bank-details",
                        "texts": {
                            "submitBarLabel": "CHB_COMMON_NEXT",
                        }
                        },
                    ],
                    },
                    
                    {
                    "head": "CHB_BANK_DETAILS",
                    "body": 
                    [
                        {
                        "type": "component",
                        "route": "bank-details",
                        "component": "CHBBankDetails",
                        "key": "bankdetails",
                        "withoutLabel": true,
                        "texts": {
                            "submitBarLabel": "CHB_COMMON_NEXT",
                        },
                        "nextStep": "documents",
                        },

                        
                    ],
                    },
                        
                
                    {
                    "head": "ES_TITILE_DOCUMENT_DETAILS",
                    "body": [
                        {
                            "route": "documents",
                            "component": "CHBDocumentDetails",
                            "withoutLabel": true,
                            "key": "documents",
                            "type": "component",
                            "nextStep":null,
                            "texts": {
                                "submitBarLabel": "CHB_COMMON_NEXT",
                            },
                            
                        }
                    ],
                    },
                ];