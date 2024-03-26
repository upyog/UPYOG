// export const newConfig =[
//   {
//       "head": "ES_NEW_APPLICATION_LOCATION_DETAILS",
//       "body": [
//           {
//               "route": "map",
//               "component": "PTSelectGeolocation",
//               "nextStep": "pincode",
//               "hideInEmployee": true,
//               "key": "address",
//               "texts": {
//                   "header": "PT_GEOLOCATON_HEADER",
//                   "cardText": "PT_GEOLOCATION_TEXT",
//                   "nextText": "PT_COMMON_NEXT",
//                   "skipAndContinueText": "CORE_COMMON_SKIP_CONTINUE"
//               }
//           },
//           {
//               "route": "pincode",
//               "component": "PTSelectPincode",
//               "texts": {
//                   "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
//                   "header": "PT_PINCODE_LABEL",
//                   "cardText": "PT_PINCODE_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT",
//                   "skipText": "CORE_COMMON_SKIP_CONTINUE"
//               },
//               "withoutLabel": true,
//               "key": "address",
//               "nextStep": "address",
//               "type": "component"
//           },
//           {
//               "route": "address",
//               "component": "PTSelectAddress",
//               "withoutLabel": true,
//               "texts": {
//                   "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
//                   "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
//                   "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_CITY_MOHALLA_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "address",
//               "nextStep": "street",
//               "isMandatory": true,
//               "type": "component"
//           },
//           {
//               "type": "component",
//               "route": "street",
//               "component": "PTSelectStreet",
//               "key": "address",
//               "withoutLabel": true,
//               "texts": {
//                   "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
//                   "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
//                   "cardText": "PT_STREET_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "nextStep": "landmark"
//           },
//           {
//               "type": "component",
//               "route": "landmark",
//               "component": "PTSelectLandmark",
//               "withoutLabel": true,
//               "texts": {
//                   "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
//                   "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TITLE",
//                   "cardText": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT",
//                   "skipText": "CORE_COMMON_SKIP_CONTINUE"
//               },
//               "key": "address",
//               "nextStep": "proof",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "proof",
//               "component": "Proof",
//               "withoutLabel": true,
//               "texts": {
//                   "headerCaption": "PT_PROPERTY_LOCATION_CAPTION",
//                   "header": "PT_PROOF_OF_ADDRESS_HEADER",
//                   "cardText": "",
//                   "nextText": "PT_COMMONS_NEXT",
//                   "submitBarLabel": "PT_COMMONS_NEXT"
//               },
//               "key": "address",
//               "nextStep": "owner-ship-details@0",
//               "hideInEmployee": true
//           }
//       ]
//   },
//   {
//       "head": "ES_NEW_APPLICATION_PROPERTY_ASSESSMENT",
//       "body": [
//           {
//               "route": "info",
//               "component": "PropertyTax",
//               "nextStep": "property-type",
//               "hideInEmployee": true,
//               "key": "Documents"
//           },
//           {
//               "type": "component",
//               "route": "isResidential",
//               "isMandatory": true,
//               "component": "IsResidential",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_PROPERTY_DETAILS_RESIDENTIAL_PROPERTY_HEADER",
//                   "cardText": "PT_PROPERTY_DETAILS_RESIDENTIAL_PROPERTY_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "isResdential",
//               "withoutLabel": true,
//               "hideInEmployee": true,
//               "nextStep": {
//                   "PT_COMMON_YES": "property-type",
//                   "PT_COMMON_NO": "property-usage-type"
//               }
//           },
//           {
//               "type": "component",
//               "route": "property-usage-type",
//               "isMandatory": true,
//               "component": "PropertyUsageType",
//               "texts": {
//                   "headerCaption": "PT_ASSESMENT_INFO_USAGE_TYPE",
//                   "header": "PT_PROPERTY_DETAILS_USAGE_TYPE_HEADER",
//                   "cardText": "PT_PROPERTY_DETAILS_USAGE_TYPE_TEXT",
//                   "submitBarLabel": "PT_COMMONS_NEXT"
//               },
//               "nextStep": "property-type",
//               "key": "usageCategoryMajor",
//               "withoutLabel": true
//           },
//           {
//               "type": "component",
//               "isMandatory": true,
//               "component": "ProvideSubUsageType",
//               "key": "usageCategoryMinor",
//               "withoutLabel": true
//           },
//           {
//               "type": "component",
//               "route": "provide-sub-usage-type",
//               "isMandatory": true,
//               "component": "ProvideSubUsageType",
//               "texts": {
//                   "headerCaption": "PT_ASSESMENT_INFO_USAGE_TYPE",
//                   "header": "PT_ASSESSMENT_FLOW_SUBUSAGE_HEADER",
//                   "cardText": "PT_ASSESSMENT_FLOW_SUBUSAGE_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "units",
//               "withoutLabel": true,
//               "nextStep": {
//                   "yes": "is-any-part-of-this-floor-unoccupied",
//                   "no": "provide-sub-usage-type-of-rented-area"
//               },
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "property-type",
//               "isMandatory": true,
//               "component": "PropertyType",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_ASSESMENT1_PROPERTY_TYPE",
//                   "cardText": "",
//                   "submitBarLabel": "PT_COMMONS_NEXT"
//               },
//               "nextStep": {
//                   "COMMON_PROPTYPE_BUILTUP_INDEPENDENTPROPERTY": "landarea",
//                   "COMMON_PROPTYPE_BUILTUP_SHAREDPROPERTY": "PtUnits",
//                   "COMMON_PROPTYPE_VACANT": "area"
//               },
//               "key": "PropertyType",
//               "withoutLabel": true
//           },
//           {
//               "type": "component",
//               "isMandatory": true,
//               "component": "Area",
//               "key": "landarea",
//               "withoutLabel": true
//           },
//           {
//               "type": "component",
//               "route": "PtUnits",
//               "isMandatory": true,
//               "component": "SelectPTUnits",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_FLAT_DETAILS",
//                   "cardText": "PT_FLAT_DETAILS_DESC",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "units",
//               "withoutLabel": true,
//               "nextStep": "map",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "landarea",
//               "isMandatory": true,
//               "component": "PTLandArea",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_PLOT_SIZE_HEADER",
//                   "cardText": "",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "units",
//               "withoutLabel": true,
//               "nextStep": "number-of-floors",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "area",
//               "isMandatory": true,
//               "component": "Area",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_PLOT_SIZE_HEADER",
//                   "cardText": "PT_FORM2_PLOT_SIZE_PLACEHOLDER",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "units",
//               "withoutLabel": true,
//               "nextStep": "map",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "number-of-floors",
//               "isMandatory": true,
//               "component": "PropertyBasementDetails",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_PROPERTY_DETAILS_NO_OF_BASEMENTS_HEADER",
//                   "cardText": "",
//                   "submitBarLabel": "PT_COMMONS_NEXT"
//               },
//               "nextStep": "number-of-basements@0",
//               "key": "noOofBasements",
//               "withoutLabel": true
//           },
//           {
//               "type": "component",
//               "component": "Units",
//               "key": "units",
//               "withoutLabel": true
//           },
//           {
//               "type": "component",
//               "route": "provide-floor-no",
//               "isMandatory": true,
//               "component": "ProvideFloorNo",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_FLOOR_NUMBER_HEADER",
//                   "cardText": "",
//                   "submitBarLabel": "PT_COMMONS_NEXT"
//               },
//               "nextStep": "units",
//               "key": "Floorno",
//               "withoutLabel": true,
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "is-this-floor-self-occupied",
//               "isMandatory": true,
//               "component": "IsThisFloorSelfOccupied",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_ASSESSMENT_FLOW_FLOOR_OCC_HEADER",
//                   "cardText": "PT_ASSESSMENT_FLOW_FLOOR_OCC_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "units",
//               "withoutLabel": true,
//               "nextStep": {
//                   "PT_YES_IT_IS_SELFOCCUPIED": "provide-sub-usage-type",
//                   "PT_YES_IT_IS_SELFOCCUPIED1": "is-any-part-of-this-floor-unoccupied",
//                   "PT_PARTIALLY_RENTED_OUT": "area",
//                   "PT_PARTIALLY_RENTED_OUT1": "area",
//                   "PT_FULLY_RENTED_OUT": "provide-sub-usage-type-of-rented-area",
//                   "PT_FULLY_RENTED_OUT1": "rental-details"
//               },
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "number-of-basements@0",
//               "isMandatory": true,
//               "component": "PropertyFloorDetails",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "BPA_SCRUTINY_DETAILS_NUMBER_OF_FLOORS_LABEL",
//                   "cardText": "PT_PROPERTY_DETAILS_NO_OF_FLOORS_TEXT",
//                   "submitBarLabel": "PT_COMMONS_NEXT"
//               },
//               "nextStep": {
//                   "PT_NO_BASEMENT_OPTION": "units",
//                   "PT_ONE_BASEMENT_OPTION": "units",
//                   "PT_TWO_BASEMENT_OPTION": "units"
//               },
//               "key": "noOfFloors",
//               "withoutLabel": true,
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "units",
//               "isMandatory": true,
//               "component": "SelectPTUnits",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_FLAT_DETAILS",
//                   "cardText": "PT_FLAT_DETAILS_DESC",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "nextStep": "map",
//               "key": "units",
//               "withoutLabel": true,
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "rental-details",
//               "isMandatory": true,
//               "component": "RentalDetails",
//               "texts": {
//                   "header": "PT_ASSESSMENT_FLOW_RENTAL_DETAIL_HEADER",
//                   "cardText": "",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "units",
//               "withoutLabel": true,
//               "nextStep": "is-any-part-of-this-floor-unoccupied",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "provide-sub-usage-type-of-rented-area",
//               "isMandatory": true,
//               "component": "ProvideSubUsageTypeOfRentedArea",
//               "texts": {
//                   "headerCaption": "PT_ASSESMENT_INFO_USAGE_TYPE",
//                   "header": "PT_ASSESSMENT_FLOW_RENT_SUB_USAGE_HEADER",
//                   "cardText": "PT_ASSESSMENT_FLOW_SUBUSAGE_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "units",
//               "withoutLabel": true,
//               "nextStep": "rental-details",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "is-any-part-of-this-floor-unoccupied",
//               "isMandatory": true,
//               "component": "IsAnyPartOfThisFloorUnOccupied",
//               "texts": {
//                   "header": "PT_ASSESSMENT_FLOW_ISUNOCCUPIED_HEADER",
//                   "cardText": "PT_ASSESSMENT_FLOW_ISUNOCCUPIED_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "units",
//               "withoutLabel": true,
//               "nextStep": {
//                   "PT_COMMON_NO": "map",
//                   "PT_COMMON_YES": "un-occupied-area"
//               },
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "un-occupied-area",
//               "isMandatory": true,
//               "component": "UnOccupiedArea",
//               "texts": {
//                   "header": "PT_ASSESSMENT_FLOW_UNOCCUPIED_AREA_HEADER",
//                   "cardText": "PT_ASSESSMENT_FLOW_UNOCCUPIED_AREA_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT",
//                   "skipText": ""
//               },
//               "key": "units",
//               "withoutLabel": true,
//               "nextStep": "map",
//               "hideInEmployee": true
//           }
//       ]
//   },
//   {
//       "head": "ES_NEW_APPLICATION_OWNERSHIP_DETAILS",
//       "body": [
//           {
//               "type": "component",
//               "route": "owner-ship-details@0",
//               "isMandatory": true,
//               "component": "SelectOwnerShipDetails",
//               "texts": {
//                   "headerCaption": "PT_PROPERTIES_OWNERSHIP",
//                   "header": "PT_PROVIDE_OWNERSHIP_DETAILS",
//                   "cardText": "PT_PROVIDE_OWNERSHI_DETAILS_SUB_TEXT",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "ownershipCategory",
//               "withoutLabel": true,
//               "nextStep": {
//                   "INSTITUTIONALPRIVATE": "inistitution-details",
//                   "INSTITUTIONALGOVERNMENT": "inistitution-details",
//                   "INDIVIDUAL.SINGLEOWNER": "owner-details",
//                   "INDIVIDUAL.MULTIPLEOWNERS": "owner-details"
//               }
//           },
//           {
//               "isMandatory": true,
//               "type": "component",
//               "route": "owner-details",
//               "key": "owners",
//               "component": "SelectOwnerDetails",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_OWNERSHIP_INFO_SUB_HEADER",
//                   "cardText": "PT_FORM3_HEADER_MESSAGE",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "withoutLabel": true,
//               "nextStep": "special-owner-category",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "special-owner-category",
//               "isMandatory": true,
//               "component": "SelectSpecialOwnerCategoryType",
//               "texts": {
//                   "headerCaption": "PT_OWNERS_DETAILS",
//                   "header": "PT_SPECIAL_OWNER_CATEGORY",
//                   "cardText": "PT_FORM3_HEADER_MESSAGE",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "owners",
//               "withoutLabel": true,
//               "nextStep": "owner-address",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "owner-address",
//               "isMandatory": true,
//               "component": "SelectOwnerAddress",
//               "texts": {
//                   "headerCaption": "PT_OWNERS_DETAILS",
//                   "header": "PT_OWNERS_ADDRESS",
//                   "cardText": "",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "owners",
//               "withoutLabel": true,
//               "nextStep": "special-owner-category-proof",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "component": "SelectAltContactNumber",
//               "key": "owners",
//               "withoutLabel": true,
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "special-owner-category-proof",
//               "isMandatory": true,
//               "component": "SelectSpecialProofIdentity",
//               "texts": {
//                   "headerCaption": "PT_OWNERS_DETAILS",
//                   "header": "PT_SPECIAL_OWNER_CATEGORY_PROOF_HEADER",
//                   "cardText": "",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "owners",
//               "withoutLabel": true,
//               "nextStep": "proof-of-identity",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "proof-of-identity",
//               "isMandatory": true,
//               "component": "SelectProofIdentity",
//               "texts": {
//                   "headerCaption": "PT_DOCUMENT_DETAILS",
//                   "header": "PT_PROOF_IDENTITY_HEADER",
//                   "cardText": "",
//                   "submitBarLabel": "PT_COMMON_NEXT",
//                   "addMultipleText": "PT_COMMON_ADD_APPLICANT_LABEL"
//               },
//               "key": "owners",
//               "withoutLabel": true,
//               "nextStep": null,
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "inistitution-details",
//               "isMandatory": true,
//               "component": "SelectInistitutionOwnerDetails",
//               "texts": {
//                   "headerCaption": "",
//                   "header": "PT_INSTITUTION_DETAILS_HEADER",
//                   "cardText": "PT_FORM3_HEADER_MESSAGE",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "owners",
//               "withoutLabel": true,
//               "nextStep": "institutional-owner-address",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "institutional-owner-address",
//               "isMandatory": true,
//               "component": "SelectOwnerAddress",
//               "texts": {
//                   "headerCaption": "PT_OWNERS_DETAILS",
//                   "header": "PT_OWNERS_ADDRESS",
//                   "cardText": "",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "owners",
//               "withoutLabel": true,
//               "nextStep": "institutional-proof-of-identity",
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "route": "institutional-proof-of-identity",
//               "isMandatory": true,
//               "component": "SelectProofIdentity",
//               "texts": {
//                   "headerCaption": "PT_OWNERS_DETAILS",
//                   "header": "PT_PROOF_IDENTITY_HEADER",
//                   "cardText": "",
//                   "submitBarLabel": "PT_COMMON_NEXT"
//               },
//               "key": "owners",
//               "withoutLabel": true,
//               "nextStep": null,
//               "hideInEmployee": true
//           },
//           {
//               "type": "component",
//               "component": "PTEmployeeOwnershipDetails",
//               "key": "owners",
//               "withoutLabel": true,
//               "hideInCitizen": true
//           }
//       ]
//   },
//   {
//       "head": "ES_NEW_APPLICATION_DOCUMENTS_REQUIRED",
//       "body": [
//           {
//               "component": "SelectDocuments",
//               "withoutLabel": true,
//               "key": "documents",
//               "type": "component"
//           }
//       ]
//   }
// ];










//TL config
export const newConfig = [
  {
    head: "",
    body: [
      {
        type: "component",
        component: "TLInfoLabel",
        key: "tradedetils1",
        withoutLabel: true,
        hideInCitizen: true,
      }
    ]
  },
  {
    head: "TL_COMMON_TR_DETAILS",
    body: [
      {
        type: "component",
        component: "TLTradeDetailsEmployee",
        key: "tradedetils",
        withoutLabel: true,
        hideInCitizen: true,
      }
    ]
  },
  {
    head: "TL_TRADE_UNITS_HEADER",
    body: [
      {
        type: "component",
        component: "TLTradeUnitsEmployee",
        key: "tradeUnits",
        withoutLabel: true,
        hideInCitizen: true,
      }
    ]
  },
  {
    head: "TL_NEW_TRADE_DETAILS_HEADER_ACC",
    body: [
      {
        type: "component",
        component: "TLAccessoriesEmployee",
        key: "accessories",
        withoutLabel: true,
        hideInCitizen: true,
      }
    ]
  },
  {
    head: "TL_NEW_APPLICATION_PROPERTY",
    body: [
      {
        component: "CPTPropertySearchNSummary",
        withoutLabel: true,
        key: "cpt",
        type: "component",
        hideInCitizen: true
      },
    ],
  },
  {
    "head": "ES_NEW_APPLICATION_LOCATION_DETAILS",
    "body": [
        {
            "route": "map",
            component: "TLSelectGeolocation",
            nextStep: "pincode",
            hideInEmployee: true,
            key: "address",
            withoutLabel: true,
            texts: {
                header: "TL_GEOLOACTION_HEADER",
                cardText: "TL_GEOLOCATION_TEXT",
                nextText: "CS_COMMON_NEXT",
                skipAndContinueText: "CORE_COMMON_SKIP_CONTINUE"
            }
        },
        {
            route: "pincode",
            component: "TLSelectPincode",
            texts: {
                "headerCaption": "TL_LOCATION_CAPTION",
                "header": "TL_PINCODE_HEADER",
                "cardText": "TL_PINCODE_TEXT",
                "submitBarLabel": "CS_COMMON_NEXT",
                "skipText": "CORE_COMMON_SKIP_CONTINUE"
            },
            withoutLabel: true,
            key: "address",
            nextStep: "address",
            type: "component"
        },
        {
            "route": "address",
            "component": "TLSelectAddress",
            "withoutLabel": true,
            "texts": {
                "headerCaption": "TL_LOCATION_CAPTION",
                "header": "TL_ADDRESS_HEADER",
                "cardText": "TL_ADDRESS_TEXT",
                "submitBarLabel": "CS_COMMON_NEXT"
            },
            "key": "address",
            "nextStep": "street",
            "isMandatory": true,
            "type": "component"
        },
        {
            "type": "component",
            "route": "street",
            "component": "TLSelectStreet",
            "key": "address",
            "withoutLabel": true,
            "hideInEmployee": true,
            "texts": {
                "headerCaption": "TL_LOCATION_CAPTION",
                "header": "TL_ADDRESS_HEADER",
                "cardText": "TL_STREET_TEXT",
                "submitBarLabel": "CS_COMMON_NEXT"
            },
            "inputs": [
                {
                    "label": "TL_LOCALIZATION_STREET_NAME",
                    "type": "text",
                    "name": "street",
                    "disable": "window.location.href.includes(`edit-application`)||window.location.href.includes(`renew-trade`)",
                    // "validation": {
                    //     "maxlength": 256,
                    //     "title": "CORE_COMMON_STREET_INVALID"
                    // }
                },
                {
                    "label": "TL_LOCALIZATION_BUILDING_NO",
                    "type": "text",
                    "name": "doorNo",
                    "disable": "window.location.href.includes(`edit-application`)||window.location.href.includes(`renew-trade`)",
                    // "validation": {
                    //     "maxlength": 256,
                    //     "title": "CORE_COMMON_DOOR_INVALID"
                    // }
                }
            ],
            "nextStep": "landmark"
        },
        {
            "type": "component",
            "component": "TLSelectStreet",
            "key": "address",
            "withoutLabel": true,
            "hideInCitizen": true,
            "texts": {
                "headerCaption": "TL_LOCATION_CAPTION",
                "header": "TL_ADDRESS_HEADER",
                "cardText": "TL_STREET_TEXT",
                "submitBarLabel": "CS_COMMON_NEXT"
            },
            "inputs": [
                {
                    "label": "TL_LOCALIZATION_BUILDING_NO",
                    "type": "text",
                    "name": "doorNo",
                    // "validation": {
                    //     "maxlength": 256,
                    //     "title": "CORE_COMMON_DOOR_INVALID"
                    // }
                },
                {
                    "label": "TL_LOCALIZATION_STREET_NAME",
                    "type": "text",
                    "name": "street",
                    // "validation": {
                    //     "maxlength": 256,
                    //     "title": "CORE_COMMON_STREET_INVALID"
                    // }
                }
            ]
        },
        {
            "type": "component",
            "route": "landmark",
            "component": "TLSelectLandmark",
            "withoutLabel": true,
            "texts": {
                "headerCaption": "TL_LOCATION_CAPTION",
                "header": "CS_FILE_APPLICATION_PROPERTY_LOCATION_PROVIDE_LANDMARK_TITLE",
                "cardText": "TL_LANDMARK_TEXT",
                "submitBarLabel": "CS_COMMON_NEXT",
                "skipText": "CORE_COMMON_SKIP_CONTINUE"
            },
            "key": "address",
            "nextStep": "owner-ship-details",
            "hideInEmployee": true
        },
        {
            "type": "component",
            "route": "proof",
            "component": "Proof",
            "withoutLabel": true,
            "texts": {
                "headerCaption": "TL_OWNERS_DETAILS",
                "header": "TL_OWNERS_PHOTOGRAPH_HEADER",
                "cardText": "",
                "nextText": "CS_COMMON_NEXT",
                "submitBarLabel": "CS_COMMON_NEXT"
            },
            "key": "owners",
            "nextStep": null,
            "hideInEmployee": true
        }
    ]
  },
  {
    head: "ES_NEW_APPLICATION_PROPERTY_ASSESSMENT",
    body: [
      {
        route: "info",
        component: "TradeLicense",
        nextStep: "TradeName",
        hideInEmployee: true,
        key: "tl",
      },
      {
        route: "TradeName",
        component: "SelectTradeName",
        texts: {
          headerCaption: "",
          header: "TL_TRADE_NAME_HEADER",
          cardText: "TL_TARDE_NAME_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "",
        },
        withoutLabel: true,
        key: "TradeDetails",
        nextStep: "structure-type",
        type: "component",
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "structure-type",
        isMandatory: true,
        component: "SelectStructureType",
        texts: {
          headerCaption: "TL_STRUCTURE_TYPE",
          header: "TL_STRUCTURE_TYPE_HEADER",
          cardText: "TL_STRUCTURE_TYPE_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        key: "TradeDetails",
        withoutLabel: true,
        hideInEmployee: true,
        //nextStep: "property-usage-type",
        nextStep: {
          "TL_COMMON_YES": "Building-type",
          "TL_COMMON_NO": "vehicle-type",
        },
      },
      {
        type: "component",
        route: "vehicle-type",
        isMandatory: true,
        component: "SelectVehicleType",
        texts: {
          headerCaption: "TL_STRUCTURE_SUBTYPE_CAPTION",
          header: "TL_VEHICLE_TYPE_HEADER",
          cardText: "TL_VEHICLE_TYPE_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        key: "TradeDetails",
        withoutLabel: true,
        hideInEmployee: true,
        //nextStep: "property-usage-type",
        nextStep: "commencement-date",
      },
      {
        type: "component",
        route: "Building-type",
        isMandatory: true,
        component: "SelectBuildingType",
        texts: {
          headerCaption: "TL_STRUCTURE_SUBTYPE_CAPTION",
          header: "TL_BUILDING_TYPE_HEADER",
          cardText: "TL_BUILDING_TYPE_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        key: "TradeDetails",
        withoutLabel: true,
        hideInEmployee: true,
        //nextStep: "property-usage-type",
        nextStep: "commencement-date",
      },
      {
        type: "component",
        route: "commencement-date",
        isMandatory: true,
        component: "SelectCommencementDate",
        texts: {
          headerCaption: "",
          header: "TL_NEW_TRADE_DETAILS_TRADE_COMM_DATE_LABEL",
          cardText: "TL_TRADE_COMM_DATE_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        key: "TradeDetails",
        withoutLabel: true,
        hideInEmployee: true,
        //nextStep: "property-usage-type",
        nextStep: "units-details",
      },
      {
        isMandatory: true,
        type: "component",
        route: "units-details",
        key: "TradeDetails",
        component: "SelectTradeUnits",
        texts: {
          headerCaption: "",
          header: "TL_TRADE_UNITS_HEADER",
          cardText: "TL_TRADE_UNITS_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        withoutLabel: true,
        nextStep: "isAccessories",
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "isAccessories",
        isMandatory: true,
        component: "SelectAccessories",
        texts: {
          headerCaption: "",
          header: "TL_ISACCESSORIES_HEADER",
          cardText: "TL_ISACCESSORIES_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        key: "TradeDetails",
        withoutLabel: true,
        hideInEmployee: true,
        //nextStep: "property-usage-type",
        nextStep: {
          TL_COMMON_YES: "accessories-details",
          TL_COMMON_NO: "other-trade-details",
        },
      },
      {
        isMandatory: true,
        type: "component",
        route: "accessories-details",
        key: "TradeDetails",
        component: "SelectAccessoriesDetails",
        texts: {
          headerCaption: "",
          header: "TL_TRADE_ACCESSORIES_HEADER",
          cardText: "TL_TRADE_ACCESSORIES_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        withoutLabel: true,
        nextStep: "other-trade-details",
        hideInEmployee: true,
      },
      {
        isMandatory: true,
        type: "component",
        route: "other-trade-details",
        key: "TradeDetails",
        component: "SelectOtherTradeDetails",
        texts: {
          headerCaption: "",
          header: "TL_OTHER_TRADE_DETAILS_HEADER",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
          skipText: "CORE_COMMON_SKIP_CONTINUE"
        },
        withoutLabel: true,
        nextStep: "know-your-property",
        hideInEmployee: true,
      },
    ],
  },
  {
    head: "ES_NEW_APPLICATION_OWNERSHIP_DETAILS",
    body: [
      {
        //if want to input index in url just pul @0 after route name owner-ship-details@0
        type: "component",
        route: "owner-ship-details",
        isMandatory: true,
        component: "SelectOwnerShipDetails",
        texts: {
          headerCaption: "TL_TRADE_OWNERSHIP_CAPTION",
          header: "TL_PROVIDE_OWNERSHIP_DETAILS",
          cardText: "TL_PROVIDE_OWNERSHI_DETAILS_SUB_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        key: "ownershipCategory",
        withoutLabel: true,
        nextStep: "owner-details",
      },
      {
        isMandatory: true,
        type: "component",
        route: "owner-details",
        key: "owners",
        component: "SelectOwnerDetails",
        texts: {
          headerCaption: "",
          header: "TL_OWNERSHIP_INFO_SUB_HEADER",
          cardText: "TL_OWNER_DETAILS_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        withoutLabel: true,
        nextStep: "owner-address",
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "owner-address",
        isMandatory: true,
        component: "TLSelectOwnerAddress",
        texts: {
          headerCaption: "TL_OWNERS_DETAILS",
          header: "TL_OWNERS_ADDRESS",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        key: "owners",
        withoutLabel: true,
        nextStep: "proof-of-identity",
        hideInEmployee: true,
      },
      /* {
          type: "component",
          component: "SelectAltContactNumber",
          key: "owners",
          withoutLabel: true,
          hideInEmployee: true,
        }, */
      {
        type: "component",
        route: "proof-of-identity",
        isMandatory: true,
        component: "SelectProofIdentity",
        texts: {
          headerCaption: "TL_OWNERS_DETAILS",
          header: "TL_PROOF_IDENTITY_HEADER",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
          addMultipleText: "PT_COMMON_ADD_APPLICANT_LABEL",
        },
        key: "owners",
        withoutLabel: true,
        nextStep: "ownership-proof",
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "ownership-proof",
        isMandatory: true,
        component: "SelectOwnershipProof",
        texts: {
          headerCaption: "TL_OWNERS_DETAILS",
          header: "TL_OWNERSHIP_DOCUMENT",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        key: "owners",
        withoutLabel: true,
        nextStep: "proof",
        hideInEmployee: true,
      },
      {
        type: "component",
        component: "TLOwnerDetailsEmployee",
        key: "owners",
        withoutLabel: true,
        hideInCitizen: true,
      },
      /* {
          type: "component",
          route: "inistitution-details",
          isMandatory: true,
          component: "SelectInistitutionOwnerDetails",
          texts: {
            headerCaption: "",
            header: "PT_INSTITUTION_DETAILS_HEADER",
            cardText: "PT_FORM3_HEADER_MESSAGE",
            submitBarLabel: "PT_COMMON_NEXT",
          },
          key: "owners",
          withoutLabel: true,
          nextStep: "institutional-owner-address",
          hideInEmployee: true,
        }, */
      /* {
          type: "component",
          route: "institutional-owner-address",
          isMandatory: true,
          component: "SelectOwnerAddress",
          texts: {
            headerCaption: "PT_OWNERS_DETAILS",
            header: "PT_OWNERS_ADDRESS",
            cardText: "",
            submitBarLabel: "PT_COMMON_NEXT",
          },
          key: "owners",
          withoutLabel: true,
          nextStep: "institutional-proof-of-identity",
          hideInEmployee: true,
        }, */
      /* {
          type: "component",
          route: "institutional-proof-of-identity",
          isMandatory: true,
          component: "SelectProofIdentity",
          texts: {
            headerCaption: "PT_OWNERS_DETAILS",
            header: "PT_PROOF_IDENTITY_HEADER",
            cardText: "",
            submitBarLabel: "PT_COMMON_NEXT",
          },
          key: "owners",
          withoutLabel: true,
          //nextStep: "",
          nextStep: null,
          hideInEmployee: true,
        }, */
      /*  {
          type: "component",
          component: "PTEmployeeOwnershipDetails",
          key: "owners",
          withoutLabel: true,
          hideInCitizen: true,
        }, */
    ],
  },
  {
    head: "",
    body: [
      {
        //if want to input index in url just pul @0 after route name owner-ship-details@0
        type: "component",
        route: "know-your-property",
        isMandatory: true,
        component: "CPTKnowYourProperty", 
        texts: {
          header: "PT_DO_YOU_KNOW_YOUR_PROPERTY",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        key: "knowyourproperty",
        isCreateEnabled : true,
        withoutLabel: true,
        nextStep: {
          TL_COMMON_YES: "search-property",
          TL_COMMON_NO: "create-property",
        },
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "search-property",
        isMandatory: true,
        component: "CPTSearchProperty", 
        key: "cptsearchproperty",
        withoutLabel: true,
        nextStep: 'search-results',
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "search-results",
        isMandatory: true,
        component: "CPTSearchResults", 
        key: "cptsearchresults",
        withoutLabel: true,
        nextStep: 'property-details',
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "create-property", 
        isMandatory: true,
        component: "CPTCreateProperty", 
        key: "cptcreateproperty",
        withoutLabel: true,
        isSkipEnabled : true,
        nextStep: 'acknowledge-create-property',
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "acknowledge-create-property", 
        isMandatory: true,
        component: "CPTAcknowledgement", 
        key: "cptacknowledgement",
        withoutLabel: true,
        nextStep: 'property-details',
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "property-details",
        isMandatory: true,
        component: "CPTPropertyDetails", 
        key: "propertydetails",
        withoutLabel: true,
        nextStep: 'owner-ship-details',
        hideInEmployee: true,
      },
    ],
  },
  // {
  //   head: "ES_NEW_APPLICATION_DOCUMENTS_REQUIRED",
  //   body: [
  //     {
  //       component: "SelectDocuments",
  //       withoutLabel: true,
  //       key: "documents",
  //       type: "component",
  //     },
  //   ],
  // },
  {
    head: "TL_NEW_APPLICATION_DOCUMENTS_REQUIRED",
    body: [
      {
        component: "TLDocumentsEmployee",
        withoutLabel: true,
        key: "documents",
        type: "component",
        hideInCitizen: true
      },
    ],
  },
];
