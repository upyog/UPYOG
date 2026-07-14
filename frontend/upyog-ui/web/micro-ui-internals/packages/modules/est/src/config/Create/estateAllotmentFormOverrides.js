/**
 * Local overrides for MDMS assignAssetConfig — NOT a full form duplicate.
 * Matched to MDMS fields by `key`; MDMS drives structure/order/options.
 */
const estateAllotmentFormOverrides = {
  key: "Allotments",
  payloadKey: "Allotments",
  uploadModule: "ESTATE",
  dateFormat: "dd-MM-yyyy",
  staticFields: (tenantId, flatData) => ({
    assetNo: flatData?.assetNo || "",
    assetReferenceNo: flatData?.assetRefNumber || "",
    allotmentStatus: "INITIATED",
  }),
  pageHeading: {
    create: "EST_COMMMON_ASSIGN_ASSETS",
    edit: "EST_COMMMON_ASSIGN_ASSETS",
  },
  draftButton: {
    label: "EST_SAVE_AS_DRAFT",
    successMessage: "EST_DRAFT_SAVED",
  },
  form: [
    {
      key: "EST_ALLOTMENT_TYPE",
      apiFieldName: "propertyType",
      options: [
        { code: "RENT", i18nKey: "EST_ALLOTMENT_TYPE_RENT" },
        { code: "LEASE", i18nKey: "EST_ALLOTMENT_TYPE_LEASE" },
      ],
    },
    {
      key: "EST_PHONE_NUMBER",
      field: { name: "mobileNo" },
    },
    {
      key: "EST_ALTERNATE_PHONE_NUMBER",
      field: { name: "alternateMobileNo" },
    },
    {
      key: "EST_EMAIL_ID",
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
        computeFrom: ["agreementStartDate", "agreementEndDate"],
        computeFn: "calculateDuration",
        numeric: true,
      },
      validation: { required: false, disabled: true, readOnly: true },
    },
    {
      key: "EST_BILLING_CYCLE",
      field: {
        dataSource: {
          type: "MDMS",
          moduleName: "Estate",
          masterName: "BillingCycle",
        },
      },
      options: [],
      validation: { required: true, disabled: false },
      messages: { error: "EST_BILLING_CYCLE_REQUIRED" },
    },
    {
      key: "EST_RATE_PER_SQFT",
      field: {
        name: "rentRate",
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
        labelBy: {
          field: "billingCycle",
          optionKey: "rentLabelKey",
          defaultKey: "EST_MONTHLY_RENT_IN_INR",
        },
        computeFrom: ["rentRate", "totalFloorArea", "billingCycle"],
        computeFn: "calculateRentByBillingCycle",
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
