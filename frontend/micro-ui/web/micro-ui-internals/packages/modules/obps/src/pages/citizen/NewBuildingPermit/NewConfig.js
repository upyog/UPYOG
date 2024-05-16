export const newConfig1=[
    {
        "route": "docs-required",
        "component": "DocsRequired",
        "key": "data",
        "nextStep": "noc-number"
    },
    {
        "route": "noc-number",
        "component": "NOCNumber",
        "nextStep": "basic-details",
        "key": "nocnumber",
        "texts": {
            "headerCaption": "BPA_NOC_NUMBER_DETAILS",
            "header": "",
            "submitBarLabel": "CS_COMMON_NEXT"
        }

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
            //"skipText": "CORE_COMMON_SKIP_CONTINUE"
        },
        "inputs": [
            {
                "label": "BPA_BOUNDARY_LAND_REG_DETAIL_LABEL",
                "type": "textarea",
                "validation": {},
                "name": "registrationDetails"
            },
            {
                "label": "BPA_BOUNDARY_WALL_LENGTH_LABEL_INPUT",
                "type": "text",
                "validation": {
                    "pattern":"^[0-9]*$",
                    "title": "Enter in Numbers[0-9]",
                    "required": true
                },
                "name": "boundaryWallLength"
            },
            {
                "label": "BPA_WARD_NUMBER_LABEL",
                "type": "text",
                "validation": {
                    "pattern": "^[a-zA-Z0-9 -]+$",
                    "required": true
                  },
                "name": "wardnumber"
            },
            {
                "label": "BPA_ZONE_NUMBER_LABEL",
                "type": "text",
                "validation": {},
                "name": "zonenumber"
            },
            {
                "label": "BPA_KHASRA_NUMBER_LABEL",
                "type": "text",
                "validation": {"required": true},
                "name": "khasraNumber"
            },
            {
                "label": "BPA_ARCHITECT_ID",
                "type": "text",
                "validation": {"required": true},
                "name": "architectid"
            },
            {
                "label": "BPA_PROPERTY_UID",
                "type": "text",
                "validation": {},
                "name": "propertyuid"
            },
            {
                "label": "BPA_NUMBER_OF_BATHS",
                "type": "text",
                "validation": {},
                "name": "bathnumber"
            },
            {
                "label": "BPA_NUMBER_OF_KITCHENS",
                "type": "text",
                "validation": {},
                "name": "kitchenNumber"
            },
            {
                "label": "BPA_APPROX_INHABITANTS_FOR_ACCOMODATION",
                "type": "text",
                "validation": {},
                "name": "approxinhabitants"
            },
            {
                "label": "BPA_DISTANCE_FROM_SEWER",
                "type": "text",
                "validation": {},
                "name": "distancefromsewer"
            },
            {
                "label": "BPA_SOURCE_OF_WATER",
                "type": "text",
                "validation": {},
                "name": "sourceofwater"
            },
            {
                "label": "BPA_NUMBER_OF_WATER_CLOSETS",
                "type": "text",
                "validation": {},
                "name": "watercloset"
            },
            {
                "label": "BPA_MATERIAL_TO-BE_USED_IN_WALLS",
                "type": "text",
                "validation": {},
                "name": "materialused"
            },
            {
                "label": "BPA_MATERIAL_TO-BE_USED_IN_FLOOR",
                "type": "text",
                "validation": {},
                "name": "materialusedinfloor"
            },
            {
                "label": "BPA_MATERIAL_TO-BE_USED_IN_ROOFS",
                "type": "text",
                "validation": {},
                "name": "materialusedinroofs"
            },

        ]
    },
    {
        "route": "scrutiny-details",
        "component": "ScrutinyDetails",
        "nextStep": "location",
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
        "nextStep": "additional-building-details",
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
        "route": "additional-building-details",
        "component": "BPANewBuildingdetails",
        "nextStep": "owner-details",
        "key": "owners",
        "texts": {
            "header": "BPA_ADDITIONAL_BUILDING_DETAILS",
            "submitBarLabel": "CS_COMMON_NEXT"
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
        "nextStep": null,
        "key": "documents",
        "texts": {
            "headerCaption": "BPA_OWNER_AND_DOCUMENT_DETAILS_LABEL",
            "header": "BPA_DOCUMENT_DETAILS_LABEL",
            "submitBarLabel": "CS_COMMON_NEXT"
        }
    },
    // {
    //     "route": "noc-details",
    //     "component": "NOCDetails",
    //     "nextStep": null,
    //     "key": "nocDocuments",
    //     "texts": {
    //         "headerCaption": "BPA_NOC_DETAILS_SUMMARY",
    //         "header": "",
    //         "submitBarLabel": "CS_COMMON_NEXT",
    //         "skipText": "CORE_COMMON_SKIP_CONTINUE"
    //     }
    // }
]