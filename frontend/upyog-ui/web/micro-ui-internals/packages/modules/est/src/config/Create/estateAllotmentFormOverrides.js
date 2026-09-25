/**
 * estateAllotmentFormOverrides
 * ----------------------------
 * WHY THIS FILE EXISTS
 * MDMS (Estate.AssignAssetConfig) owns the *shape* of the assign-assets form:
 * which fields exist, their order, labels, placeholders, and dropdown sources.
 * Only behavior that CANNOT live in MDMS JSON stays here:
 *
 *   - staticFields / crossFieldValidations use JS functions → not expressible in JSON.
 *   - pageHeading / draftButton are thin UI wiring fallbacks.
 *
 * Structural field changes (labels, options, dropdown masters, apiFieldName,
 * computeFn, validation patterns) → edit MDMS AssignAssetConfig / AllotmentType.
 *
 * WHERE IT'S USED
 *   - ESTAssignAssets.js            → the assign-assets form step (localOverrides)
 *   - ESTDynamicCheckPage.js        → allotment review/submit
 *   - ESTApplicationDetails.js      → citizen/employee summary (same config)
 *   - acknowledgementUtils.js       → allotment ack PDF/section builder
 *   - utils/index.js                → apiId fallback for payload build
 */

import { parseAdditionalDetails } from "../../utils/estMdmsUtils";

const estateAllotmentFormOverrides = {
  // Wizard step key + API envelope key: payload is sent as { Allotments: [...] }.
  key: "Allotments",
  payloadKey: "Allotments",
  // RequestInfo.apiId for the create/update call.
  apiId: "Rainmaker",
  // Filestore module used when uploading the document fields on this step.
  uploadModule: "ESTATE",
  // Output format for date fields (agreement start/end, advance payment date).
  dateFormat: "dd-MM-yyyy",

  // Keys that aren't user-editable form fields but must be in the payload.
  // Built from already-collected form values (the selected asset) at submit time.
  staticFields: (tenantId, flatData) => {
    const existingDetails = parseAdditionalDetails(flatData?.additionalDetails);
    const fileReferenceNumber = String(flatData?.fileReferenceNumber || "").trim();
    // Drop legacy uppercase key when rewriting additionalDetails.
    const { FILE_REFERENCE_NUMBER: _legacy, ...restDetails } = existingDetails;
    return {
      // Allotment API key is assetNo; value is the estate number from the form.
      assetNo: flatData?.estateNo || flatData?.assetNo || "",
      // New allotments always start in INITIATED; status transitions happen server-side.
      allotmentStatus: "INITIATED",
      // Persist only as additionalDetails.fileReferenceNumber.
      additionalDetails: {
        ...restDetails,
        ...(fileReferenceNumber ? { fileReferenceNumber } : {}),
      },
    };
  },

  // Page title shown for create vs edit modes of the step.
  pageHeading: {
    create: "EST_ALLOT_ESTATE",
    edit: "EST_ALLOT_ESTATE",
    fallback: "Allot Estate",
  },
  // "Save as draft" button label + success toast for the assign-assets step.
  draftButton: {
    label: "EST_SAVE_AS_DRAFT",
    successMessage: "EST_DRAFT_SAVED",
  },

  // Minimal field overlays only — everything else lives in MDMS AssignAssetConfig.
  form: [
    {
      // Compat: older AssignAssetConfig used EST_ASSET_NUMBER / assetNo.
      key: "EST_ASSET_NUMBER",
      field: { name: "estateNo" },
      summaryLabel: "EST_ESTATE_NUMBER",
      apiFieldName: "assetNo",
    },
  ],

  // Validations that span more than one field (can't live on a single field
  // in MDMS). Here: agreement end date must not precede the start date.
  crossFieldValidations: [
    {
      id: "agreementDateRange",
      fields: ["agreementStartDate", "agreementEndDate"],
      validate: (formData) => {
        if (!formData.agreementStartDate || !formData.agreementEndDate) return true;
        const start =
          formData.agreementStartDate instanceof Date
            ? formData.agreementStartDate
            : new Date(formData.agreementStartDate);
        const end =
          formData.agreementEndDate instanceof Date
            ? formData.agreementEndDate
            : new Date(formData.agreementEndDate);
        return end.getTime() >= start.getTime();
      },
      message: "EST_INVALID_AGREEMENT_END_DATE",
    },
  ],
};

export default estateAllotmentFormOverrides;
