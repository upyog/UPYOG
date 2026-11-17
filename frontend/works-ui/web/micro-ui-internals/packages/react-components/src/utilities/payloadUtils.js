/**
 * payloadUtils.js
 *
 * Generic API payload building for config-driven create/update flows.
 * Module behavior comes through routeConfig only (staticFields, computedFields,
 * dateFormat, numericFields, per-field apiFieldName / excludeFromPayload).
 *
 * toDropdownOption / resolveOption live in formUtils — re-exported here for
 * backward-compatible imports from @nudmcdgnpm/digit-ui-react-components.
 *
 * Exports
 * -------
 * - getRequestInfo      — DIGIT RequestInfo envelope
 * - formatDateForApi    — date → dd-MM-yyyy / yyyy-MM-dd / ISO
 * - extractFileStoreId  — file field → plain fileStoreId string
 * - buildApiPayload     — routeConfig.form + flat values → API body fields
 *
 * @see buildPayload (formUtils) — dumb flattener for wizard session storage
 * @see buildApiPayload          — richer mapping for final API submit
 */

import { toDropdownOption, resolveOption } from "./formUtils";
export { toDropdownOption, resolveOption };

/**
 * Generic RequestInfo builder. Pass apiId explicitly per module.
 * Includes the DIGIT fields backends commonly echo in ResponseInfo.
 *
 * @param {string} [apiId="Rainmaker"] Module API id.
 * @param {object} [extra]             Extra keys merged into RequestInfo.
 * @returns {object}
 */
export const getRequestInfo = (apiId = "Rainmaker", extra = {}) => {
  const ts = Date.now();
  const lang =
    (typeof Digit?.StoreData?.getCurrentLanguage === "function"
      ? Digit.StoreData.getCurrentLanguage()
      : null) || "en_IN";

  return {
    apiId: apiId || "Rainmaker",
    ver: "1.0",
    ts,
    action: "create",
    did: "1",
    key: "",
    msgId: `${ts}|${lang}`,
    authToken: Digit?.UserService?.getUser()?.access_token || "",
    userInfo: Digit?.UserService?.getUser()?.info || {},
    ...extra,
  };
};

/**
 * Format a form date value for API submission.
 * routeConfig.dateFormat or field.dateFormat controls output (default dd-MM-yyyy).
 *
 * @param {string|Date|number} value
 * @param {string} [format="dd-MM-yyyy"] "dd-MM-yyyy" | "yyyy-MM-dd" | else ISO.
 * @returns {string|null}
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

/**
 * Whether a field should be cast to Number in the API payload.
 * True when field.numeric === true, or name is listed in routeConfig.numericFields
 * (Set or array).
 *
 * @param {object} field
 * @param {object} fieldConfig
 * @param {object} routeConfig
 * @returns {boolean}
 */
const shouldCastNumeric = (field, fieldConfig, routeConfig) => {
  if (field.numeric === true) return true;
  const extra = routeConfig?.numericFields;
  if (!extra) return false;
  if (extra instanceof Set) return extra.has(field.name);
  if (Array.isArray(extra)) return extra.includes(field.name);
  return false;
};

/**
 * API file fields are plain fileStoreId strings, not { filestoreId } objects.
 * Accepts string ids or upload objects from DynamicForm.
 *
 * @param {string|object|null|undefined} value
 * @returns {string|null}
 */
export const extractFileStoreId = (value) => {
  if (value === null || value === undefined || value === "") return null;
  if (typeof value === "string") return value;
  if (typeof value === "object") {
    return value.filestoreId || value.fileStoreId || value.documentuuid || null;
  }
  return null;
};

/** Alias used internally by assignFieldValue for file fields. */
const serializeFileValue = extractFileStoreId;

/**
 * Writes one leaf field into the API payload under payloadKey.
 * Handles dropdown (code + Name), date (formatDateForApi), file (fileStoreId),
 * and numeric / raw text.
 *
 * @param {object} payload
 * @param {string} payloadKey
 * @param {object} field
 * @param {object} fieldConfig
 * @param {object} flatData
 * @param {object} routeConfig
 */
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
 *
 * @param {object} routeConfig
 * @param {object} [flatData]  Flattened wizard values (from buildPayload).
 * @param {string} [tenantId]  Always assigned onto the result.
 * @returns {object}
 */
export const buildApiPayload = (routeConfig, flatData = {}, tenantId) => {
  const payload = {};

  /**
   * Recurses groups; skips sectionHeaders and excludeFromPayload fields.
   * @param {object} fieldConfig
   */
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
