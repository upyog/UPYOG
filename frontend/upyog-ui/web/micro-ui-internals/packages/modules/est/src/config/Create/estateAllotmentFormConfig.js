// Flat routeConfig for DynamicForm — same shape as estateFormConfig.js used by NewRegistration.
// payloadKey "Allotments" matches the state/payload key used by the static ESTAssignAssets.js
// and by NewRegistration's onSelect(config.key, { Allotments: [...] }) contract.

const estateAllotmentFormConfig = {
  key: "Allotments",
  payloadKey: "Allotments",
  uploadModule: "ESTATE",
  pageHeading: {
    create: "EST_COMMMON_ASSIGN_ASSETS",
    edit: "EST_COMMMON_ASSIGN_ASSETS",
  },
  form: [
    // ── Asset details (read-only, prefilled from the previous step) ─────
    {
      order: 0,
      key: "EST_ASSET_NUMBER",
      field: { code: "EST_ASSET_NUMBER", name: "assetNo", type: "text" },
      validation: { required: true, disabled: true, readOnly: true },
    },
    {
      order: 1,
      key: "EST_ASSET_REFERENCE_NUMBER",
      field: { code: "EST_ASSET_REFERENCE_NUMBER", name: "assetRefNumber", type: "text" },
      validation: { disabled: true, readOnly: true },
    },
    {
      order: 2,
      key: "EST_BUILDING_NAME",
      field: { code: "EST_BUILDING_NAME", name: "buildingName", type: "text" },
      validation: { disabled: true, readOnly: true },
      // Display-only copy of asset data captured in the newRegistration step — the Allotment
      // API only needs a reference to the asset (assetNo/assetRefNumber), not its full attributes.
      excludeFromPayload: true,
    },
    {
      order: 3,
      key: "EST_LOCALITY",
      field: { code: "EST_LOCALITY", name: "localityDisplay", type: "text" },
      validation: { disabled: true, readOnly: true },
      excludeFromPayload: true,
    },
    {
      order: 4,
      key: "EST_TOTAL_AREA",
      field: { code: "EST_TOTAL_AREA", name: "totalFloorArea", type: "text", unit: "(In sq.ft)" },
      validation: { disabled: true, readOnly: true },
      excludeFromPayload: true,
    },
    {
      order: 5,
      key: "EST_FLOOR",
      field: { code: "EST_FLOOR", name: "buildingFloor", type: "text" },
      validation: { disabled: true, readOnly: true },
      excludeFromPayload: true,
    },
    {
      order: 6,
      key: "EST_RATE",
      field: { code: "EST_RATE", name: "assetRate", type: "text", unit: "(Per sq.ft)" },
      validation: { disabled: true, readOnly: true },
      excludeFromPayload: true,
    },

    // ── Personal details of allottee ─────────────────────────────────────
    {
      order: 7,
      key: "EST_PERSONAL_DETAILS_OF_ALLOTTEE",
      label: { code: "EST_PERSONAL_DETAILS_OF_ALLOTTEE" },
      type: "sectionHeader",
    },
    {
      order: 8,
      key: "EST_ALLOTMENT_TYPE",
      field: {
        code: "EST_ALLOTMENT_TYPE",
        name: "allotmentType",
        type: "radio",
        dataSource: { type: "MDMS", moduleName: "Estate", moduleData: "AllotmentType" },
      },
      // Falls back to these two static options if MDMS Estate/AllotmentType returns nothing,
      // matching the static component's fallbackAllotmentTypes behavior.
      options: [
        { code: "RENT", i18nKey: "EST_ALLOTMENT_TYPE_RENT" },
        { code: "LEASE", i18nKey: "EST_ALLOTMENT_TYPE_LEASE" },
      ],
      validation: { required: true, disabled: false },
      messages: { error: "EST_ALLOTMENT_TYPE_REQUIRED" },
    },
    {
      order: 9,
      key: "EST_ALLOTTEE_NAME",
      field: {
        code: "EST_ALLOTTEE_NAME",
        name: "alloteeName",
        placeholder: "EST_ENTER_ALLOTTEE_NAME",
        type: "text",
      },
      validation: {
        pattern: "^[a-zA-Z ]+$",
        regex: { pattern: "[^a-zA-Z ]", flags: "g" },
        required: true,
        disabled: false,
        readOnly: false,
      },
      messages: { error: "EST_INVALID_ALLOTTEE_NAME" },
    },
    {
      order: 10,
      key: "EST_PHONE_NUMBER",
      field: {
        code: "EST_PHONE_NUMBER",
        name: "mobileNo",
        placeholder: "EST_ENTER_PHONE_NUMBER",
        type: "text",
      },
      validation: {
        maxLength: 10,
        pattern: "^[0-9]{10}$",
        regex: { pattern: "\\D", flags: "g" },
        required: true,
        disabled: false,
        readOnly: false,
      },
      messages: { error: "EST_INVALID_PHONE_NUMBER" },
    },
    {
      order: 11,
      key: "EST_ALTERNATE_PHONE_NUMBER",
      field: {
        code: "EST_ALTERNATE_PHONE_NUMBER",
        name: "alternateMobileNo",
        placeholder: "EST_ENTER_ALTERNATE_PHONE_NUMBER",
        type: "text",
      },
      validation: {
        maxLength: 10,
        pattern: "^[0-9]{10}$",
        regex: { pattern: "\\D", flags: "g" },
        required: false,
        disabled: false,
        readOnly: false,
      },
      messages: { error: "EST_INVALID_ALTERNATE_PHONE_NUMBER" },
    },
    {
      order: 12,
      key: "EST_EMAIL_ID",
      field: {
        code: "EST_EMAIL_ID",
        name: "emailId",
        placeholder: "EST_ENTER_EMAIL_ID",
        type: "text",
      },
      validation: {
        pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        required: true,
        disabled: false,
        readOnly: false,
      },
      messages: { error: "EST_INVALID_EMAIL_ID" },
    },

    // ── Allotment & invoice details ───────────────────────────────────────
    {
      order: 13,
      key: "EST_ALLOTMENT_INVOICE_DETAILS",
      label: { code: "EST_ALLOTMENT_INVOICE_DETAILS" },
      type: "sectionHeader",
    },
    {
      order: 14,
      key: "EST_AGREEMENT_START_DATE",
      field: { code: "EST_AGREEMENT_START_DATE", name: "agreementStartDate", type: "date" },
      validation: { required: true, disabled: false },
      messages: { error: "EST_INVALID_AGREEMENT_START_DATE" },
    },
    {
      order: 15,
      key: "EST_AGREEMENT_END_DATE",
      field: { code: "EST_AGREEMENT_END_DATE", name: "agreementEndDate", type: "date" },
      validation: { required: true, disabled: false },
      messages: { error: "EST_INVALID_AGREEMENT_END_DATE" },
    },
    {
      order: 16,
      key: "EST_DURATION_IN_MONTHS",
      field: {
        code: "EST_DURATION_IN_MONTHS",
        name: "duration",
        type: "text",
        unit: "In months",
        // Must match the actual field `name`s below — DynamicForm's
        // applyComputedFields matches on changed field names, so the old
        // ["startDate","endDate"] never fired and duration stayed blank.
        computeFrom: ["agreementStartDate", "agreementEndDate"],
        computeFn: "calculateDuration",
      },
      validation: { required: false, disabled: true, readOnly: true },
    },
    {
      order: 17,
      key: "EST_BILLING_CYCLE",
      field: { code: "EST_BILLING_CYCLE", name: "billingCycle", type: "dropdown", placeholder: "EST_SELECT_BILLING_CYCLE" },
      options: [
        { code: "MONTHLY", i18nKey: "EST_BILLING_CYCLE_MONTHLY" },
        { code: "QUARTERLY", i18nKey: "EST_BILLING_CYCLE_QUARTERLY" },
        { code: "YEARLY", i18nKey: "EST_BILLING_CYCLE_YEARLY" },
      ],
      validation: { required: true, disabled: false },
      messages: { error: "EST_BILLING_CYCLE_REQUIRED" },
    },
    {
      order: 18,
      key: "EST_RATE_PER_SQFT",
      field: {
        code: "EST_RATE_PER_SQFT",
        name: "rentRate",
        placeholder: "EST_ENTER_RATE",
        type: "text",
        unit: "(Per sq ft)",
      },
      validation: {
        maxLength: 12,
        pattern: "^[0-9]+(\\.[0-9]{1,2})?$",
        regex: { pattern: "[^0-9.]", flags: "g" },
        required: true,
        disabled: false,
        readOnly: false,
      },
      messages: { error: "EST_INVALID_AMOUNT" },
    },
    {
      order: 19,
      key: "EST_MONTHLY_RENT_IN_INR",
      field: {
        code: "EST_MONTHLY_RENT_IN_INR",
        name: "monthlyRent",
        placeholder: "EST_ENTER_MONTHLY_RENT",
        type: "text",
        unit: "In INR",
      },
      validation: {
        maxLength: 12,
        pattern: "^[0-9]+(\\.[0-9]{1,2})?$",
        regex: { pattern: "[^0-9.]", flags: "g" },
        required: true,
        disabled: false,
        readOnly: false,
      },
      messages: { error: "EST_INVALID_AMOUNT" },
    },
    {
      order: 20,
      key: "EST_ADVANCE_PAYMENT_IN_INR",
      field: {
        code: "EST_ADVANCE_PAYMENT_IN_INR",
        name: "advancePayment",
        placeholder: "EST_ENTER_ADVANCE_PAYMENT",
        type: "text",
        unit: "In INR",
      },
      validation: {
        maxLength: 12,
        pattern: "^[0-9]+(\\.[0-9]{1,2})?$",
        regex: { pattern: "[^0-9.]", flags: "g" },
        required: true,
        disabled: false,
        readOnly: false,
      },
      messages: { error: "EST_INVALID_AMOUNT" },
    },
    {
      order: 21,
      key: "EST_ADVANCE_PAYMENT_DATE",
      field: { code: "EST_ADVANCE_PAYMENT_DATE", name: "advancePaymentDate", type: "date" },
      validation: { required: false, disabled: false },
    },

    // ── Document upload ───────────────────────────────────────────────────
    {
      order: 22,
      key: "EST_DOCUMENT_UPLOAD",
      label: { code: "EST_DOCUMENT_UPLOAD" },
      type: "sectionHeader",
    },
    {
      order: 23,
      key: "FILE_REFERENCE_NUMBER",
      field: {
        code: "FILE_REFERENCE_NUMBER",
        name: "eOfficeFileNo",
        placeholder: "EST_ENTER_FILE_NO",
        type: "text",
      },
      validation: {
        maxLength: 20,
        pattern: "^[0-9]+$",
        regex: { pattern: "\\D", flags: "g" },
        required: false,
        disabled: false,
        readOnly: false,
      },
      messages: { error: "EST_INVALID_FILE_NO" },
    },
    {
      order: 24,
      key: "EST_CITIZEN_REQUEST_LETTER",
      field: {
        code: "EST_CITIZEN_REQUEST_LETTER",
        name: "citizenLetter",
        type: "file",
        accept: ".png,.jpg,.jpeg,.pdf",
      },
      validation: { required: false, disabled: false },
    },
    {
      order: 25,
      key: "EST_ALLOTMENT_LETTER",
      field: {
        code: "EST_ALLOTMENT_LETTER",
        name: "allotmentLetter",
        type: "file",
        accept: ".png,.jpg,.jpeg,.pdf",
      },
      validation: { required: false, disabled: false },
    },
    {
      order: 26,
      key: "EST_SIGNED_DEED",
      field: {
        code: "EST_SIGNED_DEED",
        name: "signedDeed",
        type: "file",
        accept: ".png,.jpg,.jpeg,.pdf",
      },
      validation: { required: false, disabled: false },
    },
  ],
  crossFieldValidations: [
    {
      id: "agreementDateRange",
      // Same rename as computeFrom above — with the old ["startDate","endDate"]
      // this rule always saw undefined and returned true, so an end date before
      // the start date sailed through validation.
      fields: ["agreementStartDate", "agreementEndDate"],
      validate: (formData) => {
        // Let the per-field `required` rule handle emptiness — this only checks ordering
        // once both dates are present, so it doesn't fire false positives mid-entry.
        if (!formData.agreementStartDate || !formData.agreementEndDate) return true;
        const start = formData.agreementStartDate instanceof Date ? formData.agreementStartDate : new Date(formData.agreementStartDate);
        const end = formData.agreementEndDate instanceof Date ? formData.agreementEndDate : new Date(formData.agreementEndDate);
        return end.getTime() >= start.getTime();
      },
      message: "EST_INVALID_AGREEMENT_END_DATE",
    },
  ],
  actionButton: {
    text: { create: "SUBMIT", edit: "UPDATE" },
    variant: "contained",
    color: "primary",
  },
};

export default estateAllotmentFormConfig;
