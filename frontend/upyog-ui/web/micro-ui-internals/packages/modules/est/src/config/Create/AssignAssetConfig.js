
export const Config = [
  {
    head: "EST_COMMON_ALLOTMENT",
    body: [
      {
        route: "info",
        component: "ESTAssignAstRequiredDoc",
        nextStep: "assign-assets",
        key: "Documents",
      },
      {
        key: "Allotments",
        route: "assign-assets",
        component: "ESTAssignAssets",
        nextStep: null, // → goNext() routes straight to "check" (ESTAssignAssetsCheckPage)
        isPreview: false,
        withoutLabel: true,
        type: "component",
        hideInEmployee: false,
        isMandatory: true,
        sectionHeading: null,
        texts: {
          submitBarLabel: "COMMON_SAVE_NEXT",
          header: "EST_ASSIGN_PROPERTY_DETAILS",
        },
        // Form fields, validation, crossFieldValidations, and actionButton
        // live in estateAllotmentFormConfig.js — merged at runtime by
        // ESTAssignAssets.js and ESTAssignAssetsCheckPage.js.
      },
    ],
  },
];

export default Config;
