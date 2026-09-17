/**
 * Configuration file for NOC (No Objection Certificate) application flow.
 * Defines the step-by-step form workflow for citizen application creation,
 * including step routes, page components, routing sequence (nextStep),
 * form data keys, localization text keys, and timeline step indicators.
 */
export const newConfig = [
  {
    head: "NOC_APPLICATION_DETAILS",
    body: [
      {
        route: "document-required",
        component: "NOCServiceDoc",
        nextStep: "noc-type",
        key: "documentsRequired",
        withoutLabel: true
      },
      {
        route: "noc-type",
        component: "NocTypeSelection",
        nextStep: "property-details",
        key: "nocType",
        withoutLabel: true,
        texts: {
          header: "NOC_TYPE_HEADER",
          cardText: "NOC_TYPE_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        timeLine: [
          {
            currentStep: 1,
            actions: "NOC_DETAILS",
          },
        ],
      },
      {
        route: "property-details",
        component: "NocPropertyDetails",
        nextStep: "owner-details",
        key: "property",
        withoutLabel: true,
        texts: {
          header: "",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        timeLine: [
          {
            currentStep: 2,
            actions: "NOC_PROPERTY_DETAILS",
          },
        ],
      },
      {
        route: "owner-details",
        component: "NocOwnerDetails",
        nextStep: "document-details",
        key: "owners",
        withoutLabel: true,
        texts: {
          header: "NOC_APPLICANT_DETAILS_HEADER",
          cardText: "",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        timeLine: [
          {
            currentStep: 3,
            actions: "NOC_APPLICANT_DETAILS",
          },
        ],
      },
      {
        route: "document-details",
        component: "NocDocumentDetails",
        nextStep: null,
        key: "documents",
        withoutLabel: true,
        texts: {
          header: "NOC_DOCUMENT_DETAILS_HEADER",
          cardText: "NOC_DOCUMENT_DETAILS_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
        timeLine: [
          {
            currentStep: 4,
            actions: "NOC_DOCUMENT_DETAILS",
          },
        ],
      },
    ],
  },
];
