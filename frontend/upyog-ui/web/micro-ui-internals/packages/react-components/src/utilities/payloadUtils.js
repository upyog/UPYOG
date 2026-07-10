// payloadUtils.js
// Generic API payload building. Module behavior comes through routeConfig only.
//
// toDropdownOption/resolveOption live in formUtils — re-exported here for
// backward-compatible imports from react-components.

import { toDropdownOption, resolveOption } from "./formUtils";
export { toDropdownOption, resolveOption };

/**
 * Generic RequestInfo builder. Pass apiId explicitly per module.
 */
export const getRequestInfo = (apiId, extra = {}) => ({
  apiId,
  authToken: Digit?.UserService?.getUser()?.access_token || "",
  userInfo: Digit?.UserService?.getUser()?.info || {},
  ...extra,
});

/**
 * Format a form date value for API submission.
 * routeConfig.dateFormat or field.dateFormat controls output (default dd-MM-yyyy).
 */
export const formatDateForApi = (value, format = "dd-MM-yyyy") => {
  if (!value) return null;
  const d = value instanceof Date ? value : new Date(value);
  if (isNaN(d.getTime())) return null;

  const dd = String(d.getDate()).padStart(2, "0");
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const yyyy = String(d.getFullYear());

  if (format === "dd-MM-yyyy") return `${dd}-${mm}-${yyyy}`;
  if (format === "yyyy-MM-dd") return `${yyyy}-${mm}-${dd}`;
  return d.toISOString();
};

const shouldCastNumeric = (field, fieldConfig, routeConfig) => {
  if (field.numeric === true) return true;
  const extra = routeConfig?.numericFields;
  if (!extra) return false;
  if (extra instanceof Set) return extra.has(field.name);
  if (Array.isArray(extra)) return extra.includes(field.name);
  return false;
};

/** API file fields are plain fileStoreId strings, not { filestoreId } objects. */
export const extractFileStoreId = (value) => {
  if (value === null || value === undefined || value === "") return null;
  if (typeof value === "string") return value;
  if (typeof value === "object") {
    return value.filestoreId || value.fileStoreId || value.documentuuid || null;
  }
  return null;
};

const serializeFileValue = extractFileStoreId;

const assignFieldValue = (payload, payloadKey, field, fieldConfig, flatData, routeConfig) => {
  const { name, type } = field;
  const defaultDateFormat = routeConfig?.dateFormat || "dd-MM-yyyy";

  if (type === "dropdown") {
    payload[payloadKey] = flatData[name] || "";
    payload[`${payloadKey}Name`] = flatData[`${name}Name`] || "";
    return;
  }

  if (type === "date") {
    payload[payloadKey] = formatDateForApi(
      flatData[name],
      field.dateFormat || defaultDateFormat
    );
    return;
  }

  if (type === "file") {
    payload[payloadKey] = serializeFileValue(flatData[name]);
    return;
  }

  const raw = flatData[name] ?? "";
  payload[payloadKey] = shouldCastNumeric(field, fieldConfig, routeConfig)
    ? Number(raw) || 0
    : raw;
};

/**
 * Builds the API-ready payload from routeConfig.form + flat form values.
 *
 * Per-field (fieldConfig + field):
 *   - excludeFromPayload: true → skipped
 *   - apiFieldName           → renames payload key
 *   - type dropdown          → code + codeName
 *   - type date              → formatted string (see formatDateForApi)
 *   - type file              → fileStoreId string (extracted from upload object)
 *   - type radio/text/number → raw value; numeric when field.numeric or routeConfig.numericFields
 *
 * Per routeConfig:
 *   - staticFields           → object | (tenantId, flatData) => object
 *   - computedFields         → [{ compute, removeKeys? }]
 *   - dateFormat             → default date output format
 *   - numericFields          → optional Set/array of field names to cast as Number
 */
export const buildApiPayload = (routeConfig, flatData = {}, tenantId) => {
  const payload = {};

  const processField = (fieldConfig) => {
    if (fieldConfig.type === "group") {
      (fieldConfig.children || []).forEach(processField);
      return;
    }
    if (fieldConfig.excludeFromPayload || fieldConfig.type === "sectionHeader") return;

    const { field, apiFieldName } = fieldConfig;
    if (!field) return;

    assignFieldValue(payload, apiFieldName || field.name, field, fieldConfig, flatData, routeConfig);
  };

  (routeConfig?.form || []).forEach(processField);

  const staticFields =
    typeof routeConfig?.staticFields === "function"
      ? routeConfig.staticFields(tenantId, flatData)
      : routeConfig?.staticFields || {};

  Object.assign(payload, staticFields, { tenantId });

  (routeConfig?.computedFields || []).forEach(({ compute, removeKeys = [] }) => {
    Object.assign(payload, compute(flatData, payload));
    removeKeys.forEach((k) => delete payload[k]);
  });

  return payload;
};
