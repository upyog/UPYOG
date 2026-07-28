/**
 * estateAllotmentFormOverrides
 * ----------------------------
 * WHY THIS FILE EXISTS
 * MDMS (Estate.AssignAssetConfig) owns the *shape* of the assign-assets form:
 * which fields exist, their order, labels, placeholders, and dropdown sources.
 * But some behavior CANNOT live in MDMS JSON because it is either code
 * (functions) or app-specific wiring the backend/MDMS shouldn't dictate:
 *
 *   - staticFields / crossFieldValidations use JS functions → not expressible in JSON.
 *   - computeFn / prefillFrom / labelBy / apiFieldName are frontend binding rules.
 *   - payloadKey / apiId / uploadModule / dateFormat are request-plumbing defaults.
 *
 * So this object is a set of LOCAL OVERRIDES, merged onto the MDMS form by
 * `mergeRouteConfig(mdmsStep, estateAllotmentFormOverrides)`. It is NOT a full
 * copy of the form — entries are matched to MDMS rows by `key`, and only the
 * listed props override/extend the MDMS field. MDMS still drives structure.
 *
 * WHERE IT'S USED
 *   - ESTAssignAssets.js            → the assign-assets form step (localOverrides)
 *   - ESTDynamicCheckPage.js        → allotment review/submit
 *   - ESTApplicationDetails.js      → citizen/employee summary (same config)
 *   - acknowledgementUtils.js       → allotment ack PDF/section builder
 *   - utils/index.js                → apiId fallback for payload build
 *
 * If a change is purely structural (add/rename a field, reorder, change label
 * or dropdown master) → edit MDMS. If it's behavior/compute/validation → here.
 */
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
  staticFields: (tenantId, flatData) => ({
    assetNo: flatData?.assetNo || "",
    assetReferenceNo: flatData?.assetRefNumber || "",
    // New allotments always start in INITIATED; status transitions happen server-side.
    allotmentStatus: "INITIATED",
  }),

  // Page title shown for create vs edit modes of the step.
  pageHeading: {
    create: "EST_COMMMON_ASSIGN_ASSETS",
    edit: "EST_COMMMON_ASSIGN_ASSETS",
  },
  // "Save as draft" button label + success toast for the assign-assets step.
  draftButton: {
    label: "EST_SAVE_AS_DRAFT",
    successMessage: "EST_DRAFT_SAVED",
  },

  // Per-field overrides. Each entry is matched to the MDMS field by `key`;
  // MDMS supplies everything not restated here (label, placeholder, order…).
  form: [
    {
      key: "EST_ALLOTMENT_TYPE",
      // Form field is `allotmentType`, but the API/persister key is `propertyType`.
      apiFieldName: "propertyType",
      // Static options for the type dropdown (rent vs lease).
      options: [
        { code: "RENT", i18nKey: "EST_ALLOTMENT_TYPE_RENT" },
        { code: "LEASE", i18nKey: "EST_ALLOTMENT_TYPE_LEASE" },
      ],
    },
    {
      key: "EST_PHONE_NUMBER",
      // Rename MDMS field to the API's `mobileNo` (Allotment.mobileNo).
      field: { name: "mobileNo" },
    },
    {
      key: "EST_ALTERNATE_PHONE_NUMBER",
      // Rename MDMS field to the API's `alternateMobileNo`.
      field: { name: "alternateMobileNo" },
    },
    {
      key: "EST_EMAIL_ID",
      // Stricter email pattern than MDMS (allows 2+ char TLDs like .com/.info).
      validation: {
        pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+[.][a-zA-Z]{2,}$",
        required: true,
        disabled: false,
        readOnly: false,
      },
      messages: { error: "EST_INVALID_EMAIL_ID" },
    },
    {
      key: "EST_DURATION",
      field: {
        name: "duration",
        // Auto-computed (in months) from the agreement date range; read-only.
        computeFrom: ["agreementStartDate", "agreementEndDate"],
        computeFn: "calculateDuration",
        numeric: true,
      },
      validation: { required: false, disabled: true, readOnly: true },
    },
    {
      key: "EST_BILLING_CYCLE",
      field: {
        // Billing cycle options come from MDMS; each carries multiplier +
        // rentLabelKey used by the monthly-rent field below.
        dataSource: {
          type: "MDMS",
          moduleName: "Estate",
          masterName: "BillingCycle",
        },
        // Applied by buildInitialData when the field is empty.
        defaultValue: "MONTHLY",
      },
      options: [],
      validation: { required: true, disabled: false },
      messages: { error: "EST_BILLING_CYCLE_REQUIRED" },
    },
    {
      key: "EST_RATE_PER_SQFT",
      field: {
        name: "rentRate",
        // Pre-fill from the selected asset's rate; user can override.
        prefillFrom: "assetRate",
        numeric: true,
      },
      validation: {
        maxLength: 12,
        maxAmount: 9999999999.99,
        pattern: "^[0-9]+(\\.[0-9]{1,2})?$",
        regex: { pattern: "[^0-9.]", flags: "g" },
        required: true,
      },
      messages: { error: "EST_INVALID_AMOUNT" },
    },
    {
      key: "EST_MONTHLY_RENT_IN_INR",
      field: {
        name: "monthlyRent",
        // Label switches with billing cycle (Monthly/Quarterly/Yearly rent) via
        // the selected option's rentLabelKey; falls back to the monthly label.
        labelBy: {
          field: "billingCycle",
          optionKey: "rentLabelKey",
          defaultKey: "EST_MONTHLY_RENT_IN_INR",
        },
        // rent = rentRate × area × cycle multiplier; recomputed on any change.
        computeFrom: ["rentRate", "totalFloorArea", "billingCycle"],
        computeFn: "calculateRentByBillingCycle",
        numeric: true,
      },
      validation: {
        maxLength: 12,
        maxAmount: 9999999999.99,
        pattern: "^[0-9]+(\\.[0-9]{1,2})?$",
        required: true,
        // Derived value — not directly editable.
        disabled: true,
        readOnly: true,
      },
      messages: { error: "EST_INVALID_AMOUNT" },
    },
    {
      key: "EST_ADVANCE_PAYMENT_DATE",
      field: {
        name: "advancePaymentDate",
        // buildInitialData resolves "today" → local yyyy-MM-dd via toInputDate.
        defaultValue: "today",
      },
    },
    {
      key: "EST_ADVANCE_PAYMENT_IN_INR",
      field: {
        name: "advancePayment",
        // Auto-fill from calculated rent; keep field disabled for user edits.
        computeFrom: ["monthlyRent"],
        computeFn: "copyValue",
        numeric: true,
      },
      validation: {
        maxLength: 12,
        maxAmount: 9999999999.99,
        pattern: "^[0-9]+(\\.[0-9]{1,2})?$",
        required: true,
        disabled: true,
        readOnly: true,
      },
      messages: { error: "EST_INVALID_AMOUNT" },
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
