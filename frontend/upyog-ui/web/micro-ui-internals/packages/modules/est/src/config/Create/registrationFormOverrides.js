/**
 * Local form overrides for New Registration — existing asset vs new building gate,
 * search lookup, and conditional visibility for detail fields.
 * MDMS Estate.Config may omit these; merged at runtime until MDMS is updated.
 */

const REGISTRATION_GATE_FIELDS = [
  {
    order: 0,
    key: "EST_ASSET_REGISTRATION_TYPE",
    field: {
      code: "EST_ASSET_REGISTRATION_TYPE",
      name: "assetRegistrationType",
      type: "radio",
    },
    options: [
      { code: "EXISTING_ASSET", i18nKey: "EST_EXISTING_ASSET" },
      { code: "NEW_BUILDING", i18nKey: "EST_NEW_BUILDING" },
    ],
    validation: {
      required: true,
      disabled: false,
      readOnly: false,
    },
    messages: {
      error: "EST_ASSET_REGISTRATION_TYPE_REQUIRED",
    },
    excludeFromPayload: true,
  },
  {
    order: 1,
    key: "EST_ASSET_NUMBER",
    field: {
      code: "EST_ASSET_NUMBER",
      name: "searchEstateNo",
      placeholder: "EST_ENTER_ASSET_NUMBER",
      type: "text",
      searchCard: true,
      resultLabel: "EST_ASSET_NUMBER",
      selectLabel: "CS_COMMON_SELECT",
      notFoundLabel: "EST_ASSET_NOT_FOUND",
      createNewLabel: "EST_CREATE_NEW_REGISTRATION",
    },
    visibleWhen: {
      field: "assetRegistrationType",
      equals: "EXISTING_ASSET",
    },
    validation: {
      maxLength: 64,
      required: true,
      disabled: false,
      readOnly: false,
    },
    messages: {
      error: "EST_ASSET_NUMBER_REQUIRED",
    },
    excludeFromPayload: true,
  },
];

const DETAILS_VISIBILITY = {
  visibleWhen: {
    field: "showRegistrationDetails",
    equals: "YES",
  },
};

const hasRegistrationGate = (form = []) =>
  form.some((fc) => fc?.field?.name === "assetRegistrationType");

/** Inject gate fields and showRegistrationDetails visibility onto MDMS form rows. */
export const patchRegistrationFormFields = (mdmsForm = []) => {
  if (!Array.isArray(mdmsForm) || mdmsForm.length === 0) {
    return [...REGISTRATION_GATE_FIELDS];
  }
  if (hasRegistrationGate(mdmsForm)) {
    return mdmsForm;
  }

  const detailFields = mdmsForm.map((fc, index) => {
    if (fc?.field?.name === "assetRegistrationType" || fc?.field?.name === "searchEstateNo") {
      return fc;
    }
    const order = index + REGISTRATION_GATE_FIELDS.length;
    if (fc?.type === "group") {
      return { ...fc, order, ...DETAILS_VISIBILITY };
    }
    if (fc?.visibleWhen) {
      return { ...fc, order };
    }
    return { ...fc, order, ...DETAILS_VISIBILITY };
  });

  return [...REGISTRATION_GATE_FIELDS, ...detailFields];
};

export default REGISTRATION_GATE_FIELDS;
