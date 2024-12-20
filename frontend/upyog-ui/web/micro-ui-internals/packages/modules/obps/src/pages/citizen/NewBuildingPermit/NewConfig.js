export const newConfig1=[
    {
        "route": "docs-required",
        "component": "DocsRequired",
        "key": "data",
        "nextStep": "basic-details"
    },
    {
        "route": "basic-details",
        "component": "BasicDetails",
        "key": "data",
        "nextStep": "plot-details"
    },
    {
        "route": "plot-details",
        "component": "PlotDetails",
        "key": "data",
        "nextStep": "scrutiny-details",
        "texts": {
            "headerCaption": "BPA_SCRUTINY_DETAILS",
            "header": "BPA_PLOT_DETAILS_TITLE",
            "cardText": "",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE"
        },
        "inputs": [
            {
                "label": "BPA_HOLDING_NUMBER_LABEL",
                "type": "text",
                "validation": {},
                "name": "holdingNumber"
            },
            {
                "label": "BPA_BOUNDARY_LAND_REG_DETAIL_LABEL",
                "type": "textarea",
                "validation": {},
                "name": "registrationDetails"
            }
        ]
    },
    {
        "route": "scrutiny-details",
        "component": "ScrutinyDetails",
        "nextStep": "search-property",
        "hideInEmployee": true,
        "key": "subOccupancy",
        "texts": {
            "headerCaption": "BPA_STEPPER_SCRUTINY_DETAILS_HEADER",
            "header": "BPA_STEPPER_SCRUTINY_DETAILS_HEADER",
            "cardText": "",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE"
        }
    },
    {
    "type": "component",
    "route": "search-property",
    "isMandatory": true,
    "component": "CPTSearchProperty",
    "key": "cptsearchproperty",
    "withoutLabel": true,
    "nextStep": "search-results",
    "hideInEmployee": true
  },
  {
    "type": "component",
    "route": "search-results",
    "isMandatory": true,
    "component": "CPTSearchResults",
    "key": "cptsearchresults",
    "withoutLabel": true,
    "nextStep": "location",
    "hideInEmployee": true
  },
  {
    "type": "component",
    "route": "create-property",
    "isMandatory": true,
    "component": "CPTCreateProperty",
    "key": "cptcreateproperty",
    "withoutLabel": true,
    "nextStep": "acknowledge-create-property",
    "hideInEmployee": true
  },
  {
    "type": "component",
    "route": "acknowledge-create-property",
    "isMandatory": true,
    "component": "CPTAcknowledgement",
    "key": "cptacknowledgement",
    "withoutLabel": true,
    "nextStep": "location",
    "hideInEmployee": true
  },
    {
        "route": "location",
        "component": "LocationDetails",
        "nextStep": "owner-details",
        "hideInEmployee": true,
        "key": "address",
        "texts": {
            "headerCaption": "BPA_STEPPER_SCRUTINY_DETAILS_HEADER",
            "header": "BPA_NEW_TRADE_DETAILS_HEADER_DETAILS",
            "cardText": "",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipAndContinueText": ""
        }
    },
    {
        "route": "owner-details",
        "component": "OwnerDetails",
        "nextStep": "document-details",
        "key": "owners",
        "texts": {
            "headerCaption": "BPA_OWNER_AND_DOCUMENT_DETAILS_LABEL",
            "header": "BPA_APPLICANT_DETAILS_HEADER",
            "submitBarLabel": "CS_COMMON_NEXT"
        }
    },
    {
        "route": "document-details",
        "component": "DocumentDetails",
        "nextStep": "noc-details",
        "key": "documents",
        "texts": {
            "headerCaption": "BPA_OWNER_AND_DOCUMENT_DETAILS_LABEL",
            "header": "BPA_DOCUMENT_DETAILS_LABEL",
            "submitBarLabel": "CS_COMMON_NEXT"
        }
    },
    {
        "route": "noc-details",
        "component": "NOCDetails",
        "nextStep": null,
        "key": "nocDocuments",
        "texts": {
            "headerCaption": "BPA_NOC_DETAILS_SUMMARY",
            "header": "",
            "submitBarLabel": "CS_COMMON_NEXT",
            "skipText": "CORE_COMMON_SKIP_CONTINUE"
        }
    }
]