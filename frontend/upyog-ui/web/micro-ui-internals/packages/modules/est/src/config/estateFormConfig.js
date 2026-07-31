/**
 * estateFormConfig.js
 *
 * Local behavior overrides for the MDMS Estate.Config **registration** (new asset)
 * wizard step. Form field structure / labels / options come from MDMS; this file
 * supplies what MDMS does not yet own:
 *
 *   - cross-field validation rules
 *   - API staticFields / computedFields for buildApiPayload
 *   - thin per-field overlays (e.g. numeric: true on buildingFloor)
 *   - payloadKey / apiId / editPayloadExtras for DynamicFormStep + submit
 *
 * Consumed as `localOverrides` (or equivalent) and merged onto the MDMS route
 * config via mergeRouteConfig / mergeFormFieldConfigs.
 *
 * Typical wiring
 * --------------
 *   import estateFormConfig from "../config/estateFormConfig";
 *   <DynamicFormStep config={mdmsStep} localOverrides={estateFormConfig} ... />
 *
 * Exports
 * -------
 * - estateCrossFieldValidations — named export; also under default.crossFieldValidations
 * - default object:
 *     crossFieldValidations, payloadKey, apiId, form,
 *     staticFields, computedFields, editPayloadExtras
 *
 * @see mergeRouteConfig
 * @see buildApiPayload
 * @see DynamicFormStep
 * @see estateAllotmentFormOverrides (allotment step counterpart)
 */

/**
 * Cross-field rules for DynamicForm.validateCrossField.
 * Each rule: { id, fields, message, validate(formData) => boolean }.
 * On failure, listed fields are marked invalid and `message` is shown.
 *
 * DIMENSION_WITHIN_PLOT_AREA — length × width must not exceed totalFloorArea
 * once an area has been entered (skipped while totalFloorArea is empty/0).
 *
 * @type {Array<{
 *   id: string,
 *   fields: string[],
 *   message: string,
 *   validate: (formData: object) => boolean
 * }>}
 */
export const estateCrossFieldValidations = [
  {
    id: "DIMENSION_WITHIN_PLOT_AREA",
    fields: ["dimensionLength", "dimensionWidth"],
    message: "EST_DIMENSION_ERROR_LENGTH_WIDTH_EXCEEDS_PLOT_AREA",
    /**
     * @param {object} formData - Live DynamicForm state.
     * @returns {boolean} True when valid (or nothing to compare yet).
     */
    validate: (formData) => {
      const length = parseFloat(formData.dimensionLength) || 0;
      const width = parseFloat(formData.dimensionWidth) || 0;
      const totalArea = parseFloat(formData.totalFloorArea) || 0;
      if (totalArea <= 0) return true; // nothing to compare against yet
      return length * width <= totalArea;
    },
  },
];

/**
 * Static Asset API keys that are not driven by a visible form field.
 * Previously hardcoded inside buildDynamicAssetPayload; now passed to
 * buildApiPayload via routeConfig.staticFields.
 *
 * Notes
 * -----
 * - tenantId is appended automatically by buildApiPayload — do not list it here.
 * - assetParentCategory follows flatData.assetType (LAND/BUILDING from MDMS);
 *   hardcoding "LAND" used to stamp every asset as LAND regardless of selection.
 * - department falls back to "DEPT_2" (legacy create payload) to avoid empty
 *   required-department backend validation.
 *
 * @param {object} flatData - Flattened form values from buildPayload / buildApiPayload.
 * @returns {object} Keys merged into the create/update Asset body.
 */
const estateStaticFields = (flatData) => ({
  assetStatus: "1",
  assetClassification: "IMMOVABLE",
  // The EST_ASSET_TYPE dropdown sources from MDMS assetParentCategory
  // (LAND/BUILDING), so the parent category must follow the user's selection —
  // hardcoding "LAND" stamped every asset as LAND regardless of what was picked.
  assetParentCategory: flatData?.assetType || "LAND",
  assetType: flatData?.assetType || "",
  assetSubCategory: null,
  assetAllotmentType: "DONATED",
  assetAllotmentStatus: "INITIATED",
  assetName: flatData?.buildingName || "",
  description: "",
  // Old create payload sent "DEPT_2"; sending "" can trip backend validation
  // on required department.
  department: flatData?.department || "DEPT_2",
  // Asset-module number (PG-1013-…) — from existing-asset search or explicit ref.
  // Do not put this in estateNo; enrichment generates EST-… estate numbers.
  refAssetNo: (() => {
    const candidates = [
      flatData?.refAssetNo,
      flatData?.assetRef,
      flatData?.searchEstateNo,
    ].filter(Boolean);
    // Prefer a PG-… asset ref when present; otherwise first non-empty candidate.
    return candidates.find((v) => /^PG-/i.test(v)) || candidates[0] || "";
  })(),
  // Only pass through a real estate number (edits). Empty on create so
  // EnrichmentService generates estateNo (EST-…). Never send PG-… here.
  // todo: will create utility function to check if the estateNo is a PG-... number
  estateNo: (() => {
    const value = (flatData?.estateNo || "").trim();
    return /^PG-/i.test(value) ? "" : value;
  })(),
});

/**
 * Derived API keys that need values from more than one form field.
 * Each entry: { compute(flatData, payload) => object, removeKeys?: string[] }.
 * After compute merges into the payload, removeKeys are deleted (UI-only names).
 *
 * Here: split the single "serviceType" locality dropdown into Asset API's
 * `locality` (display name) and `localityCode` (code), then drop the form keys.
 *
 * @type {Array<{
 *   compute: (flatData: object, payload?: object) => object,
 *   removeKeys?: string[]
 * }>}
 */
const estateComputedFields = [
  {
    /**
     * @param {object} flatData
     * @returns {{ locality: string, localityCode: string }}
     */
    compute: (flatData) => ({
      locality: flatData.serviceTypeName || flatData.serviceType || "",
      localityCode: flatData.serviceType || "",
    }),
    removeKeys: ["serviceType", "serviceTypeName"],
  },
];

// Example of how the "city is locked after selection" behavior is
// expressed in the field's own config instead of a hardcoded key check
// inside DropdownField:
//
// {
//   key: "EST_CITY",
//   field: { name: "city", type: "dropdown" },
//   validation: { required: true, disabled: true },
// }
//
// Numeric fields (buildingFloor, totalFloorArea, dimensionLength,
// dimensionWidth, rate) are no longer tracked in a separate NUMERIC_FIELDS
// set by name — mark them directly on the field itself in the route config:
//
// {
//   key: "EST_BUILDING_FLOOR",
//   field: { name: "buildingFloor", type: "text", numeric: true },
//   apiFieldName: "floor",
// }

/**
 * Per-field overlays merged onto MDMS Estate.Config form entries by
 * mergeFormFieldConfigs (matched by field.name / key).
 *
 * TODO: move into MDMS. Until then, local merge adds `numeric: true` on
 * buildingFloor (MDMS lacks that flag) and renames the API key to `floor`.
 *
 * @type {Array<object>}
 */
const estateFormFieldOverrides = [
  {
    // Search field holds asset-module number (PG-…), not estateNo (EST-…).
    key: "EST_ASSET_NUMBER",
    field: { name: "searchEstateNo", prefillFrom: "assetRef" },
  },
  {
    // Create-asset only deals with immovable estates — hide MOVABLE MDMS rows.
    key: "EST_ASSET_TYPE",
    field: {
      name: "assetType",
      dataSource: {
        type: "MDMS",
        moduleName: "ASSET",
        masterName: "assetParentCategory",
        filter: {
          assetClassification: "IMMOVABLE",
        },
      },
    },
  },
  {
    key: "EST_BUILDING_FLOOR",
    field: { name: "buildingFloor", type: "text", numeric: true },
    apiFieldName: "floor",
  },
];

/**
 * Default export — localOverrides shape for mergeRouteConfig.
 *
 * @property {Array}    crossFieldValidations - Passed to DynamicForm validation.
 * @property {string}   payloadKey            - Wizard session / API array key ("Assets").
 * @property {string}   apiId                 - RequestInfo.apiId for Asset APIs.
 * @property {Array}    form                  - Field overlays (numeric, apiFieldName, …).
 * @property {Function} staticFields          - (tenantId, flatData) => static Asset keys.
 * @property {Array}    computedFields        - Derived locality / localityCode, etc.
 * @property {Function} editPayloadExtras     - (editData) => extras for edit mutate payload.
 */
export default {
  crossFieldValidations: estateCrossFieldValidations,
  payloadKey: "Assets",
  apiId: "Rainmaker",
  form: estateFormFieldOverrides,
  /**
   * buildApiPayload calls staticFields(tenantId, flatData).
   * tenantId is ignored here — estateStaticFields only needs flatData;
   * tenantId is assigned separately by buildApiPayload.
   *
   * @param {string} tenantId
   * @param {object} flatData
   * @returns {object}
   */
  staticFields: (tenantId, flatData) => estateStaticFields(flatData),
  computedFields: estateComputedFields,
  /**
   * Merged into DynamicForm edit-mode updateMutation payload for this step.
   * Preserves estateNo from the existing record when updating.
   *
   * @param {object} editData - Existing asset / application record.
   * @returns {{ estateNo: * }}
   */
  editPayloadExtras: (editData) => ({ estateNo: editData.estateNo }),
};
