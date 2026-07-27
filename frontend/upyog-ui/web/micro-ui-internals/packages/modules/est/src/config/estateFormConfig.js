// estateFormConfig.js — local behavior overrides for MDMS Estate.Config registration step.
// Form fields come from MDMS; this file adds payload static/computed/cross-field rules.

export const estateCrossFieldValidations = [
  {
    id: "DIMENSION_WITHIN_PLOT_AREA",
    fields: ["dimensionLength", "dimensionWidth"],
    message: "EST_DIMENSION_ERROR_LENGTH_WIDTH_EXCEEDS_PLOT_AREA",
    validate: (formData) => {
      const length = parseFloat(formData.dimensionLength) || 0;
      const width = parseFloat(formData.dimensionWidth) || 0;
      const totalArea = parseFloat(formData.totalFloorArea) || 0;
      if (totalArea <= 0) return true; // nothing to compare against yet
      return length * width <= totalArea;
    },
  },
];

// Static keys that aren't driven by any form field — these used to be
// hardcoded inside buildDynamicAssetPayload. `tenantId` is appended
// automatically by buildApiPayload, so it doesn't need to be listed here.
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
  estateNo: "",
});

// Computed/derived keys that need values pulled from more than one source
// field — e.g. splitting the single "serviceType" form field into the two
// API keys the Asset API expects: "locality" (label) and "localityCode" (code).
const estateComputedFields = [
  {
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

// Keeping the current naming for now.
//TODO: this will move into MDMS. For now we need a local config merge to add numeric: true on buildingFloor, since MDMS doesn’t include that flag yet.
const estateFormFieldOverrides = [
  {
    key: "EST_BUILDING_FLOOR",
    field: { name: "buildingFloor", type: "text", numeric: true },
    apiFieldName: "floor",
  },
];

export default {
  crossFieldValidations: estateCrossFieldValidations,
  payloadKey: "Assets",
  apiId: "Rainmaker",
  form: estateFormFieldOverrides,
  staticFields: (tenantId, flatData) => estateStaticFields(flatData),
  computedFields: estateComputedFields,
  editPayloadExtras: (editData) => ({ estateNo: editData.estateNo }),
};