/**
 * allotmentPayloadUtils.js
 * -------------------------
 * Mirrors assetPayloadUtils.js's buildDynamicAssetPayload — same walk-the-config
 * pattern, same "single source of truth used by both the preview call in
 * ESTAssignAssets and the real submit call in whatever the Allotment equivalent
 * of ESTRegCheckPage is" intent. Kept as a separate file (not merged into
 * assetPayloadUtils.js) since Allotment and Asset are different payload shapes,
 * but the shape of the function itself is deliberately identical so the two
 * don't drift apart in how they're used.
 *
 * Extends the pattern with one thing the Asset config never needed: a "date"
 * field type, converted to epoch millis for the API.
 */

// Only fields that should actually be cast to Number for the API — mirrors
// assetPayloadUtils.js's NUMERIC_FIELDS approach (explicit list, not pattern-sniffed).
export const ALLOTMENT_NUMERIC_FIELDS = new Set([
  "rate",
  "monthlyRent",
  "advancePayment",
]);

const toEpoch = (value) => {
  if (!value) return null;
  const d = value instanceof Date ? value : new Date(value);
  return isNaN(d.getTime()) ? null : d.getTime();
};

/**
 * Walks routeConfig.form and builds the API allotment object from config field
 * definitions — same contract as buildDynamicAssetPayload(routeConfig, flatAsset, tenantId).
 *
 * - type "text"     → copies value, casts to Number for ALLOTMENT_NUMERIC_FIELDS
 * - type "dropdown" → copies code and label (name + nameName), same as assetPayloadUtils
 * - type "date"     → converts to epoch millis
 * - type "radio"    → copies raw code value (already a plain string from DynamicFormField)
 * - type "file"     → passes the {filestoreId, documentuuid, documentType} object through as-is
 * - apiFieldName    → renames the key in the payload, same as assetPayloadUtils
 * - type "group"    → recurses into children
 * - excludeFromPayload: true → skipped entirely (used for read-only asset-info display fields)
 */
export const buildDynamicAllotmentPayload = (routeConfig, flatAllotment = {}, tenantId) => {
  const allotment = {};

  const processField = (fieldConfig) => {
    if (fieldConfig.type === "group") {
      (fieldConfig.children || []).forEach(processField);
      return;
    }
    if (fieldConfig.excludeFromPayload) return;

    const { field, apiFieldName } = fieldConfig;
    if (!field) return;

    const { name, type } = field;
    const payloadKey = apiFieldName || name;

    if (type === "dropdown") {
      allotment[payloadKey] = flatAllotment[name] || "";
      allotment[`${payloadKey}Name`] = flatAllotment[`${name}Name`] || "";
      return;
    }

    if (type === "date") {
      allotment[payloadKey] = toEpoch(flatAllotment[name]);
      return;
    }

    const raw = flatAllotment[name] ?? "";
    allotment[payloadKey] = ALLOTMENT_NUMERIC_FIELDS.has(name) ? Number(raw) || 0 : raw;
  };

  (routeConfig?.form || []).forEach(processField);

  // ── Static required fields not driven by form config ──
  // Best-effort guess at the real Allotment API contract — please verify/replace
  // against whatever the actual Allotment service expects, the same way
  // assetPayloadUtils.js's static block was verified against the real Asset API.
  allotment.tenantId = tenantId;
  allotment.assetId = flatAllotment.assetNo || "";
  allotment.assetRefNumber = flatAllotment.assetRefNumber || "";
  allotment.allotmentStatus = "INITIATED";

  return allotment;
};

export default buildDynamicAllotmentPayload;
