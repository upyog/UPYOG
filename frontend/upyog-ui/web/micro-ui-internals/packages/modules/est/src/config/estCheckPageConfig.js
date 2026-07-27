/**
 * Flow keys for ESTDynamicCheckPage — one config-driven check page for all EST wizards.
 *
 * - stepKey: wizard session / route step key
 * - errorLogPrefix: prefix for console.error on submit failures (create vs allot)
 * - summaryHeaderCode / defaultSectionHeaderCode: i18n keys for the check page UI
 */
export const EST_CHECK_FLOWS = {
  registration: {
    stepKey: "newRegistration",
    errorLogPrefix: "EST_CREATE",
    summaryHeaderCode: "EST_REGISTRATION_SUMMARY",
    defaultSectionHeaderCode: "EST_ASSET_DETAILS",
  },
  allotment: {
    stepKey: "Allotments",
    errorLogPrefix: "EST_ALLOT",
    summaryHeaderCode: "EST_ASSIGN_ASSETS_SUMMARY",
    defaultSectionHeaderCode: "EST_ASSET_DETAILS",
  },
};
