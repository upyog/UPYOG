/** Flow keys for ESTDynamicCheckPage — one config-driven check page for all EST wizards. */
export const EST_CHECK_FLOWS = {
  registration: {
    stepKey: "newRegistration",
    logTag: "EST_CREATE",
    summaryHeaderCode: "EST_REGISTRATION_SUMMARY",
    defaultSectionHeaderCode: "EST_ASSET_DETAILS",
  },
  allotment: {
    stepKey: "Allotments",
    logTag: "EST_ALLOT",
    summaryHeaderCode: "EST_ASSIGN_ASSETS_SUMMARY",
    defaultSectionHeaderCode: "EST_ASSET_DETAILS",
  },
};
