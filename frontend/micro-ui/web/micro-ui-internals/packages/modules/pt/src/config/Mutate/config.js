export const newConfigMutate = [
  {
    head: "PT_MUTATION_TRANSFEROR_DETAILS",
    body: [
      {
        route: "info",
        component: "PropertyTax",
        nextStep: "transferer-details",
        hideInEmployee: true,
        key: "_Documents",
        isMutation: true,
        hideInEmployee: true,
      },
      // {
      //   component: "TransfererDetails",
      //   key: "TransfererDetails",
      //   texts: {
      //     header: "PT_GEOLOCATON_HEADER",
      //     cardText: "PT_GEOLOCATION_TEXT",
      //     nextText: "PT_COMMON_NEXT",
      //     skipAndContinueText: "CORE_COMMON_SKIP_CONTINUE",
      //   },
      // },
      {
        route: "search-property",
        component: "SearchPropertyCitizen",
        nextStep: "search-results",
        key: "searchParams",
        texts: {
          header: "SEARCH_PROPERTY",
          submitButtonLabel: "PT_HOME_SEARCH_RESULTS_BUTTON_SEARCH",
          text: "CS_PT_HOME_SEARCH_RESULTS_DESC",
        },
        inputs:  [
          {
            label: "PT_HOME_SEARCH_RESULTS_OWN_MOB_LABEL",
            type: "mobileNumber",
            name: "mobileNumber",
            validation:{pattern:{  value: /[6789][0-9]{9}/,
            message: "CORE_COMMON_MOBILE_ERROR",}},
            error: "CORE_COMMON_MOBILE_ERROR",
          },
          {
            label: "PT_PROPERTY_UNIQUE_ID",
            description: "CS_PROPERTY_ID_FORMAT_MUST_BE",
            type: "text",
            name: "propertyIds",
            error: "ERR_INVALID_PROPERTY_ID",
            validation: {
              pattern: {
                value: /^[a-zA-Z0-9-]*$/i,
                message: "ERR_INVALID_PROPERTY_ID",
              },
            },
          },
          {
            label: "PT_EXISTING_PROPERTY_ID",
            type: "text",
            name: "oldPropertyId",
            error: "ERR_INVALID_PROPERTY_ID",
            validation: {
              pattern: {
                value: /^[a-zA-Z0-9-]*$/i,
                message: "ERR_INVALID_PROPERTY_ID",
              },
            },
          }, {
            label: "PT_SEARCHPROPERTY_TABEL_OWNERNAME",
            type: "text",
            name: "name",
            validation: {
              pattern: {
                value: "[A-Za-z .`'-]{3,63}",
                message: "PAYMENT_INVALID_NAME",
              },
            },
            error: "PAYMENT_INVALID_NAME",
          }, {
            label: "PT_SEARCHPROPERTY_TABEL_DOOR_NO",
            type: "text",
            name: "doorNo",
            validation:{pattern: {
              value: "[A-Za-z0-9#,/ -()]{1,63}",
              message: "ERR_INVALID_DOOR_NO",
            }},
            error: "ERR_INVALID_DOOR_NO",
          },
        ],
        action: "MUTATION",
        hideInEmployee: true,
      },
      {
        route: "search-results",
        component: "SearchResultCitizen",
        key: "searchResult",
        nextStep: "transferer-details",
        nextStep: "info",
        action: "MUTATION",
        hideInEmployee: true,
        texts: {
          header: "CS_SEARCH_RESULTS",
          actionButtonLabel: "PT_OWNERSHIP_TRANSFER",
          nextText: "PT_COMMON_NEXT",
        },
        labels: [
          {
            label: "PT_TOTAL_DUES",
            key: "total_due",
            noteStyle: {
              fontSize: "16px",
              fontWeight: "bold",
            },
            // notePrefix: "₹ ",
          },
          {
            label: "PT_PROPERTY_PTUID",
            key: "property_id",
          },
          {
            label: "PT_OWNERSHIP_INFO_NAME",
            key: "owner_name",
          },
          {
            label: "PT_PROPERTY_ADDRESS_SUB_HEADER",
            key: "property_address",
          },
          {
            label: "PT_DUE_DATE",
            key: "bil_due__date",
          },
        ],
      },
      {
        route: "transferer-details",
        nextStep: "owner-ship-details@0",
        key: "transfererDetails",
        type: "component",
        withoutLabel: true,
        component: "TransfererDetails",
        texts: {
          nextText: "PT_COMMON_NEXT",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        labels: [
          {
            label: "PT_OWNERSHIP_INSTI_NAME",
            keyPath: ["searchResult", "property", "institution", "name"],
            ownershipType: "INSTITUTIONAL",
          },
          {
            label: "PT_OWNERSHIP_INFO_DESIGNATION",
            keyPath: ["searchResult", "property", "institution", "designation"],
            ownershipType: "INSTITUTIONAL",
          },
          {
            label: "PT_INSTI_OWNERSHIP_TYPE",
            keyPath: ["searchResult", "property", "institution", "type"],
            ownershipType: "INSTITUTIONAL",
          },
          {
            label: "PT_OWNERSHIP_INFO_NAME_OF_AUTH",
            keyPath: ["searchResult", "property", "institution", "nameOfAuthorizedPerson"],
            ownershipType: "INSTITUTIONAL",
          },
          {
            label: "PT_OWNERSHIP_INFO_TEL_NO",
            keyPath: ["searchResult", "property", "owners", "_index_", "altContactNumber"],
            ownershipType: "INSTITUTIONAL",
          },
          {
            label: "PT_OWNERSHIP_INFO_NAME",
            keyPath: ["searchResult", "property", "owners", "_index_", "name"],
            ownershipType: "INDIVIDUAL",
          },
          {
            label: "PT_GUARDIAN_NAME",
            keyPath: ["searchResult", "property", "owners", "_index_", "fatherOrHusbandName"],
            ownershipType: "INDIVIDUAL",
          },
          {
            label: "PT_OWNERSHIP_INFO_MOBILE_NO",
            keyPath: ["searchResult", "property", "owners", "_index_", "mobileNumber"],
            ownershipType: "ALL",
          },
          {
            label: "PT_OWNERSHIP_INFO_EMAIL_ID",
            keyPath: ["searchResult", "property", "owners", "_index_", "emailId"],
            ownershipType: "INDIVIDUAL",
          },
          {
            label: "PT_OWNERSHIP_INFO_USER_CATEGORY",
            keyPath: ["searchResult", "property", "owners", "_index_", "ownerType"],
            ownershipType: "INDIVIDUAL",
          },
          {
            label: "PT_OWNERSHIP_INFO_CORR_ADDR",
            keyPath: ["searchResult", "property", "owners", "_index_", "correspondenceAddress"],
            ownershipType: "ALL",
          },
        ],
      },
      // ownership or transferee

      // mutation starts here
    ],
  },
  {
    head: "PT_MUTATION_TRANSFEREE_DETAILS_HEADER",
    body: [
      {
        type: "component",
        route: "owner-ship-details@0",
        isMandatory: true,
        component: "SelectOwnerShipDetails",
        texts: {
          headerCaption: "PT_MUTATION_TRANSFEREE_DETAILS_HEADER",
          header: "PT_PROVIDE_OWNERSHIP_DETAILS",
          cardText: "PT_PROVIDE_OWNERSHI_DETAILS_SUB_TEXT",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        key: "ownershipCategory",
        withoutLabel: true,
        nextStep: {
          INSTITUTIONALPRIVATE: "inistitution-details",
          INSTITUTIONALGOVERNMENT: "inistitution-details",
          "INDIVIDUAL.SINGLEOWNER": "multiple-owners",
          "INDIVIDUAL.MULTIPLEOWNERS": "multiple-owners",
        },
      },
      {
        route: "multiple-owners",
        component: "OwnerMutate",
        withoutLabel: true,
        nextStep: "is-mutatation-pending",
        key: "Owners",
        hideInEmployee: true,
      },
      {
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
      },
      {
        isMandatory: true,
        type: "component",
        route: "owner-details",
        key: "owners",
        component: "SelectOwnerDetails",
        texts: {
          headerCaption: "PT_MUTATION_TRANSFEREE_DETAILS_HEADER",
          header: "PT_OWNERSHIP_INFO_SUB_HEADER",
          cardText: "PT_FORM3_HEADER_MESSAGE",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        withoutLabel: true,
        nextStep: "special-owner-category",
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "special-owner-category",
        isMandatory: true,
        component: "SelectSpecialOwnerCategoryType",
        texts: {
          headerCaption: "PT_OWNERS_DETAILS",
          header: "PT_SPECIAL_OWNER_CATEGORY",
          cardText: "PT_FORM3_HEADER_MESSAGE",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        key: "owners",
        withoutLabel: true,
        nextStep: "owner-address",
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "owner-address",
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
        nextStep: "special-owner-category-proof",
        hideInEmployee: true,
      },
      {
        type: "component",
        component: "SelectAltContactNumber",
        key: "owners",
        withoutLabel: true,
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "special-owner-category-proof",
        isMandatory: true,
        component: "SelectSpecialProofIdentity",
        texts: {
          headerCaption: "PT_OWNERS_DETAILS",
          header: "PT_SPECIAL_OWNER_CATEGORY_PROOF_HEADER",
          cardText: "",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        key: "owners",
        withoutLabel: true,
        nextStep: "proof-of-identity",
        hideInEmployee: true,
      },
      {
        type: "component",
        route: "proof-of-identity",
        isMandatory: true,
        component: "SelectProofIdentity",
        texts: {
          headerCaption: "PT_OWNERS_DETAILS",
          header: "PT_PROOF_IDENTITY_HEADER",
          cardText: "",
          submitBarLabel: "PT_COMMON_NEXT",
          addMultipleText: "PT_COMMON_ADD_APPLICANT_LABEL",
        },
        key: "owners",
        withoutLabel: true,
        nextStep: null,
        hideInEmployee: true,
      },
      {
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
      },
      {
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
      },
      {
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
        nextStep: "is-mutatation-pending",
        hideInEmployee: true,
      },
      {
        type: "component",
        component: "PTEmployeeOwnershipDetails",
        key: "owners",
        withoutLabel: true,
        hideInCitizen: true,
      },
    ],
  },
  {
    head: "PT_MUTATION_DETAILS",
    body: [
      {
        key: "additionalDetails",
        texts: {
          headerCaption: "PT_MUTATION_DETAILS",
          header: "PT_MUTATION_COURT_PENDING_OR_NOT",
          cardText: "PT_MUTATION_PENDING_MESSAGE",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        route: "is-mutatation-pending",
        withoutLabel: true,
        component: "IsMutationPending",
        nextStep: "is-under-govt-aquisition",
        type: "component",
        // nextStep: "reason",
      },
      {
        key: "additionalDetails",
        route: "is-under-govt-aquisition",
        withoutLabel: true,
        texts: {
          headerCaption: "PT_MUTATION_DETAILS",
          header: "PT_MUTATION_STATE_ACQUISITION",
          cardText: "PT_STATE_AQUISTION_MESSAGE",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        component: "UnderStateAquire",
        nextStep: "market-value",
        type: "component",
      },
    ],
  },
  {
    head: "PT_MUTATION_REGISTRATION_DETAILS",
    body: [
      {
        key: "additionalDetails",
        route: "reason",
        texts: {
          headerCaption: "PT_MUTATION_DETAILS",
          header: "PT_MUTATION_TRANSFER_REASON",
          // cardText: "PT_FORM3_HEADER_MESSAGE",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        withoutLabel: true,
        component: "PTReasonForTransfer",
        nextStep: "market-value",
        type: "component",
      },
      {
        key: "additionalDetails",
        route: "market-value",
        texts: {
          headerCaption: "PT_MUTATION_DETAILS",
          header: "PT_MUTATION_MARKET_VALUE",
          cardText: "PT_MARKET_VALUE_MESSAGE",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        withoutLabel: true,
        component: "PropertyMarketValue",
        nextStep: "registration-doc",
        type: "component",
      },
      {
        key: "additionalDetails",
        route: "registration-doc",
        texts: {
          headerCaption: "PT_MUTATION_DETAILS",
          header: "PT_MUTATION_DOCUMENT_DETAILS",
          cardText: "PT_REGISTRATION_DOC_MESSAGE",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        withoutLabel: true,
        component: "PTRegistrationDocument",
        nextStep: "comments",
        type: "component",
      },
      {
        key: "additionalDetails",
        route: "comments",
        withoutLabel: true,
        component: "PTComments",
        texts: {
          headerCaption: "PT_MUTATION_DETAILS",
          header: "PT_MUTATION_REMARKS",
          cardText: "PT_REMARKS_MESSAGE",
          submitBarLabel: "PT_COMMON_NEXT",
        },
        nextStep: "transfer-reason-doc",
        type: "component",
      },
      {
        type: "component",
        route: "transfer-reason-doc",
        component: "TransferProof",
        withoutLabel: true,
        texts: {
          headerCaption: "PT_TRANSFER_DOC_CAPTION",
          header: "PT_TRANSFER_DOC_HEADER",
          cardText: "PT_TRANSFER_DOC_MESSAGE",
          nextText: "PT_COMMONS_NEXT",
          submitBarLabel: "PT_COMMONS_NEXT",
        },
        key: "transferReasonProof",
        hideInEmployee: true,
        nextStep: "address-proof",
      },
      {
        type: "component",
        route: "address-proof",
        component: "Proof",
        nextStep: "exemption-details",
        withoutLabel: true,
        texts: {
          headerCaption: "PT_PROPERTY_LOCATION_CAPTION",
          header: "Proof of Address",
          cardText: "",
          nextText: "PT_COMMONS_NEXT",
          submitBarLabel: "PT_COMMONS_NEXT",
        },
        key: "addressProof",
        hideInEmployee: true,
      },
      {
        component: "SelectDocuments",
        withoutLabel: true,
        key: "documents",
        type: "component",
      },
    ],
  },
  {
    "head": "ES_NEW_APPLICATION_PROPERTY_TAX_EXEMPTION",
    "body": [
        
        {
            "type": "component",
            "route": "exemption-details",
            "isMandatory": true,
            "component": "ExemptionDetails",
            "texts": {
                "headerCaption": "",
                "header": "PT_EXEMPTION_DETAILS",
                "cardText": "",
                "submitBarLabel": "PT_COMMONS_NEXT"
            },
            "nextStep": "property-photo",
            "key": "exemption",
            "withoutLabel": true
        }
    ]
  },
  {
      "head": "ES_NEW_APPLICATION_PROPERTY_PHOTO",
      "body": [
          
          {
              "type": "component",
              "route": "property-photo",
              "isMandatory": false,
              "component": "PropertyPhoto",
              "texts": {
                  "headerCaption": "",
                  "header": "PT_PROPERTY_PHOTO",
                  "cardText": "",
                  "submitBarLabel": "PT_COMMONS_NEXT"
              },
              "key": "propertyPhoto",
              "withoutLabel": true
          }
      ]
  }
];
