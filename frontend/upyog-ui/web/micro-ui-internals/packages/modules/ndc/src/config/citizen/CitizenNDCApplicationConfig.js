//config for citizen NDC application form 
export const config = [
  {
    head: "",
    stepNumber: 1,
    body: [
      {
        name: "NDC_COMMON_NDC_REASON",
        type: "component",
        component: "SelectNDCReason",
        key: "NDCReason",
        withoutLabel: true,
      },
      {
        name: "",
        type: "component",
        component: "NDCPropertySearch",
        key: "cpt",
        withoutLabel: true,
      },
      {
        name: "NDC_COMMON_PROPERTY_DETAILS",
        type: "component",
        component: "PropertyDetailsFormCitizen",
        key: "PropertyDetails",
        withoutLabel: true,
      },
    ],
  },
  {
    head: "",
    stepNumber: 2,
    body: [
      {
        type: "component",
        component: "SelectNDCDocuments",
        key: "documents",
        withoutLabel: true,
      },
    ],
  },
  {
    head: "Summary",
    stepNumber: 3,
    body: [
      {
        type: "component",
        component: "NDCSummary",
        key: "NDCSummary",
        withoutLabel: true,
      },
    ],
  }
];
