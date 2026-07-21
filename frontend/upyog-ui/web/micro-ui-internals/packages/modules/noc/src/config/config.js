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
      },
      {
        route: "property-details",
        component: "NocPropertyDetails",
        nextStep: "owner-details",
        key: "property",
        withoutLabel: true,
        texts: {
          header: "NOC_PROPERTY_DETAILS_HEADER",
          cardText: "NOC_PROPERTY_DETAILS_TEXT",
          submitBarLabel: "CS_COMMON_NEXT",
        },
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
      },
    ],
  },
];
