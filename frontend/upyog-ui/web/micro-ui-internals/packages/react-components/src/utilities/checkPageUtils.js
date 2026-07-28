/**
 * checkPageUtils.js
 *
 * Shared helpers for DynamicCheckPage and module-specific check/summary wrappers.
 * Works with the same `routeConfig.form` metadata that DynamicForm / DynamicFormField
 * use, and bridges wizard session data into human-readable summary sections.
 *
 * Responsibilities
 * ----------------
 * 1. Normalize empty summary values and flexible date inputs for check-page display.
 * 2. Resolve and merge MDMS wizard route configs with module-local overrides.
 * 3. Persist and retrieve routeConfig snapshots in wizard session data so check
 *    pages reuse the exact form metadata the user saw on each step.
 * 4. Extract flat form values from wizard onSelect payloads.
 * 5. Flatten form configs into ordered summary sections and collect file fields.
 * 6. Resolve file-store preview URLs and map uploaded files to summary entries.
 * 7. Resolve per-field display text on the check page (dropdowns, dates, units).
 *
 * Exports
 * -------
 * Re-exported from formUtils:
 *   - resolveFieldLabelKey
 *
 * Summary / display:
 *   - defaultCheckNA
 *   - parseFlexibleDate
 *   - formatCheckPageDate
 *   - resolveSummaryFieldValue
 *
 * Route config:
 *   - resolveRouteConfigFromSteps
 *   - mergeRouteConfig
 *   - ROUTE_CONFIG_SESSION_KEY
 *   - ROUTE_CONFIG_STEP_MARKER
 *   - stripRouteConfigMarker
 *   - attachRouteConfigToStepData
 *   - mergeSessionStepWithRouteConfig
 *   - resolveActiveRouteConfig
 *
 * Wizard session / form values:
 *   - extractWizardFormValues
 *   - flattenForSummary
 *   - buildSummarySections
 *
 * File preview:
 *   - resolveFilePreviewUrl
 *   - extractUrlFromFilefetchResponse
 *   - collectFormFileEntries
 *
 * @see DynamicCheckPage
 * @see DynamicFormStep
 * @see mergeRouteConfig
 */

import { sortByOrder, resolveFieldLabelKey, mergeFormFieldConfigs } from "./formUtils";
import { extractFileStoreId } from "./payloadUtils";

export { resolveFieldLabelKey };

/**
 * Return a fallback string when a summary value is empty.
 * Treats `undefined`, `null`, and `""` as empty; all other values pass through.
 *
 * @param {*}    value    Raw field or summary value.
 * @param {string} [fallback="NA"] Text shown when value is empty.
 * @returns {*} The original value, or `fallback` when empty.
 */
export const defaultCheckNA = (value, fallback = "NA") =>
  value === undefined || value === null || value === "" ? fallback : value;

/**
 * Parse a flexible date input into a native `Date` (or `null` when invalid).
 * Supports epoch milliseconds/seconds, DD-MM-YYYY strings, ISO strings, and
 * existing `Date` instances. Shared by summary pages and any module that
 * needs tolerant date display.
 *
 * @param {*} value - Epoch number/string, DD-MM-YYYY, ISO date string, or Date.
 * @returns {Date|null} Parsed date, or `null` when input is empty or invalid.
 */
export const parseFlexibleDate = (value) => {
  if (value === null || value === undefined || value === "") return null;
  if (value instanceof Date) return isNaN(value.getTime()) ? null : value;

  if (typeof value === "number" || /^\d+$/.test(String(value))) {
    let num = Number(value);
    if (String(num).length === 10) num *= 1000;
    const d = new Date(num);
    return isNaN(d.getTime()) ? null : d;
  }

  const ddmmyyyy = String(value).match(/^(\d{2})-(\d{2})-(\d{4})$/);
  if (ddmmyyyy) {
    const d = new Date(+ddmmyyyy[3], +ddmmyyyy[2] - 1, +ddmmyyyy[1]);
    return isNaN(d.getTime()) ? null : d;
  }

  const d = new Date(value);
  return isNaN(d.getTime()) ? null : d;
};

/**
 * Format any supported date value for check-page display using `toLocaleDateString`.
 * Delegates parsing to `parseFlexibleDate`; returns a fallback when parsing fails.
 *
 * @param {*} value - Date input accepted by `parseFlexibleDate`.
 * @param {object} [options]
 * @param {string} [options.fallback="N/A"] Text when the date cannot be parsed.
 * @param {string} [options.locale="en-IN"] BCP 47 locale passed to `toLocaleDateString`.
 * @returns {string} Localized date string or the fallback text.
 */
export const formatCheckPageDate = (value, { fallback = "N/A", locale = "en-IN" } = {}) => {
  const d = parseFlexibleDate(value);
  if (!d) return fallback;
  return d.toLocaleDateString(locale);
};

/**
 * Find a wizard step object from an MDMS config array (or `config.body`).
 * Matches by `stepKey` first; otherwise falls back to the first step that
 * has a non-empty `form` array.
 *
 * @param {Array|object} config  MDMS wizard config array or wrapper with `body`.
 * @param {string}       stepKey Step key to match against `step.key`.
 * @returns {object} Matched step config, or `{}` when none is found.
 */
export const resolveRouteConfigFromSteps = (config, stepKey) => {
  const steps = Array.isArray(config) ? config : config?.body || [];
  return (
    steps.find((step) => step.key === stepKey) ||
    steps.find((step) => Array.isArray(step.form) && step.form.length > 0) ||
    {}
  );
};

/** MDMS route keys overridden by module-local behavior config (always wins when present). */
const LOCAL_ROUTE_BEHAVIOR_KEYS = [
  "staticFields",
  "computedFields",
  "crossFieldValidations",
  "dateFormat",
  "uploadModule",
  "editPayloadExtras",
];

/** MDMS route keys filled from module-local config only when MDMS value is null/undefined. */
const LOCAL_ROUTE_FALLBACK_KEYS = [
  "payloadKey",
  "key",
  "apiId",
  "pageHeading",
  "draftButton",
  "searchLayout",
  "searchFormClassName",
];

/**
 * Merge an MDMS route config with module-local behavior overrides.
 * Behavior keys (staticFields, computedFields, etc.) always override MDMS;
 * fallback keys (payloadKey, pageHeading, etc.) apply only when MDMS is absent.
 * When either side defines `form`, field configs are deep-merged via
 * `mergeFormFieldConfigs` (local first, then MDMS).
 *
 * @param {object} [mdmsRouteConfig={}] Route/step config from MDMS.
 * @param {object} [localFormConfig={}] Module-local overrides from the wizard step.
 * @returns {object} Merged routeConfig consumed by DynamicForm and check pages.
 * @see mergeFormFieldConfigs
 */
export const mergeRouteConfig = (mdmsRouteConfig = {}, localFormConfig = {}) => {
  const merged = { ...mdmsRouteConfig };

  LOCAL_ROUTE_BEHAVIOR_KEYS.forEach((key) => {
    if (localFormConfig[key] != null) merged[key] = localFormConfig[key];
  });

  LOCAL_ROUTE_FALLBACK_KEYS.forEach((key) => {
    if (merged[key] == null && localFormConfig[key] != null) {
      merged[key] = localFormConfig[key];
    }
  });

  if (Array.isArray(mdmsRouteConfig.form) || Array.isArray(localFormConfig.form)) {
    merged.form = mergeFormFieldConfigs(
      localFormConfig.form || [],
      mdmsRouteConfig.form || []
    );
  }

  return merged;
};

/** Session object key where each wizard step's resolved routeConfig snapshot is stored. */
export const ROUTE_CONFIG_SESSION_KEY = "routeConfigs";

/** Property name attached to step data when a form page passes its routeConfig upstream. */
export const ROUTE_CONFIG_STEP_MARKER = "__routeConfig";

/**
 * Remove the routeConfig marker from step payload before persisting form values.
 * Strips `ROUTE_CONFIG_STEP_MARKER` so only user-entered field data remains
 * in wizard session storage.
 *
 * @param {object} [stepData={}] Step payload that may include `__routeConfig`.
 * @returns {object} Step data without the routeConfig marker; non-objects pass through.
 */
export const stripRouteConfigMarker = (stepData = {}) => {
  if (!stepData || typeof stepData !== "object") return stepData;
  const { [ROUTE_CONFIG_STEP_MARKER]: _removed, ...rest } = stepData;
  return rest;
};

/**
 * Attach the active routeConfig onto step data so the wizard parent can store
 * it in session under `ROUTE_CONFIG_SESSION_KEY`.
 *
 * @param {object} stepData    Step payload from DynamicForm onSelect.
 * @param {object} routeConfig Merged route config for this wizard step.
 * @returns {object} Step data with `__routeConfig` set, or unchanged when routeConfig is falsy.
 */
export const attachRouteConfigToStepData = (stepData = {}, routeConfig) => {
  if (!routeConfig) return stepData;
  return { ...stepData, [ROUTE_CONFIG_STEP_MARKER]: routeConfig };
};

/**
 * Persist form step values and an optional routeConfig snapshot from the form page.
 * Cleans the routeConfig marker from step data, stores field values under `stepKey`,
 * and merges any routeConfig into `session[ROUTE_CONFIG_SESSION_KEY][stepKey]`.
 * Call from parent handleSelect so check pages reuse the same config.
 *
 * @param {object} prevSession Previous wizard session object.
 * @param {string} stepKey    Wizard step key.
 * @param {object} stepData   Step payload from onSelect (may include `__routeConfig`).
 * @returns {object} Updated session with cleaned step values and optional routeConfig snapshot.
 * @see attachRouteConfigToStepData
 * @see stripRouteConfigMarker
 */
export const mergeSessionStepWithRouteConfig = (prevSession = {}, stepKey, stepData) => {
  const routeConfig = stepData?.[ROUTE_CONFIG_STEP_MARKER];
  const cleanedStepData = stripRouteConfigMarker(stepData);

  return {
    ...prevSession,
    [stepKey]: cleanedStepData,
    ...(routeConfig
      ? {
          [ROUTE_CONFIG_SESSION_KEY]: {
            ...(prevSession[ROUTE_CONFIG_SESSION_KEY] || {}),
            [stepKey]: routeConfig,
          },
        }
      : {}),
  };
};

/**
 * Resolve the active route config for a check/submit page.
 * Prefers the session snapshot saved when the user completed the form step;
 * falls back to MDMS wizard steps when no snapshot exists yet.
 *
 * @param {object} sessionValue Wizard session (includes `routeConfigs` when set).
 * @param {Array|object} mdmsSteps MDMS wizard step list or config wrapper.
 * @param {string} stepKey Wizard step key.
 * @returns {object} Resolved routeConfig for summary rendering and payload building.
 * @see resolveRouteConfigFromSteps
 */
export const resolveActiveRouteConfig = (sessionValue, mdmsSteps, stepKey) =>
  sessionValue?.[ROUTE_CONFIG_SESSION_KEY]?.[stepKey] ||
  resolveRouteConfigFromSteps(mdmsSteps, stepKey);

/**
 * Read flat form values saved by DynamicForm's onSelect callback:
 *   `onSelect(stepKey, { [payloadKey]: [formVal] })`
 * Unwraps the first array element when present; tries alternate payload keys
 * when the primary key is missing.
 *
 * @param {object} [value={}] Full wizard session or parent value object.
 * @param {string} stepKey    Wizard step key.
 * @param {string} payloadKey Primary payload property on step data.
 * @param {object} [options]
 * @param {string[]} [options.alternatePayloadKeys=[]] Fallback keys to try on step data.
 * @returns {object} Flat form field map for the step, or `{}` when nothing is saved.
 */
export const extractWizardFormValues = (
  value = {},
  stepKey,
  payloadKey,
  { alternatePayloadKeys = [] } = {}
) => {
  const stepData = value?.[stepKey] || {};
  let saved = stepData[payloadKey];

  if ((saved === undefined || saved === null) && alternatePayloadKeys.length) {
    for (const altKey of alternatePayloadKeys) {
      if (stepData[altKey] !== undefined && stepData[altKey] !== null) {
        saved = stepData[altKey];
        break;
      }
    }
  }

  if (Array.isArray(saved)) return saved[0] || {};
  return saved || {};
};

/**
 * Flatten a form config array for summary rendering.
 * Sorts top-level entries by order, expands `group` children (also sorted),
 * and returns a single ordered list of field configs.
 *
 * @param {Array} [formConfig=[]] Route config `form` array from MDMS or merged config.
 * @returns {Array} Ordered flat list of field config objects.
 * @see sortByOrder
 */
export const flattenForSummary = (formConfig = []) =>
  sortByOrder(formConfig).reduce((acc, fc) => {
    if (fc.type === "group") {
      return [...acc, ...sortByOrder(fc.children || [])];
    }
    return [...acc, fc];
  }, []);

/**
 * Split a form config into summary sections and file fields.
 * `sectionHeader` entries start a new section; fields with `hideInSummary`
 * are skipped; `file` fields are collected separately for preview/download UI.
 *
 * @param {Array} [formConfig=[]] Route config `form` array.
 * @returns {{ sections: Array<{ headerCode: string|null, fields: Array }>, fileFields: Array }}
 *   `sections` — grouped non-file fields; `fileFields` — file-type field configs.
 * @see flattenForSummary
 */
export const buildSummarySections = (formConfig = []) => {
  const sections = [];
  const fileFields = [];
  let current = { headerCode: null, fields: [] };

  flattenForSummary(formConfig).forEach((fc) => {
    if (fc.hideInSummary) return;

    if (fc.type === "sectionHeader") {
      if (current.fields.length) sections.push(current);
      current = { headerCode: fc.label?.code || fc.key, fields: [] };
      return;
    }

    if (fc.field?.type === "file") {
      fileFields.push(fc);
      return;
    }

    if (fc.field) current.fields.push(fc);
  });

  if (current.fields.length) sections.push(current);
  return { sections, fileFields };
};

/**
 * Pick the full document preview URL from a file-store response string.
 * Uses `Digit.Utils.getFileUrl` when available; otherwise takes the first
 * comma-separated URL segment (avoids small/medium thumbnail variants).
 *
 * @param {string} [rawUrl=""] Raw URL or comma-separated URL list from filestore.
 * @returns {string} Resolved preview URL, or `""` when input is empty.
 */
export const resolveFilePreviewUrl = (rawUrl = "") => {
  if (!rawUrl) return "";
  if (typeof rawUrl === "string" && Digit?.Utils?.getFileUrl) {
    return Digit.Utils.getFileUrl(rawUrl) || rawUrl.split(",")[0] || "";
  }
  return typeof rawUrl === "string" ? rawUrl.split(",")[0] || "" : "";
};

/**
 * Resolve a preview URL from a Filefetch API response for a given fileStoreId.
 * Looks up by id in `response.data[fileStoreId]`, then in `response.data.fileStoreIds`,
 * and finally falls back to the entry at `index`.
 *
 * @param {object} response   Filefetch hook/API response (`{ data: ... }`).
 * @param {string} fileStoreId Target file store id.
 * @param {number} [index=0] Fallback index into `data.fileStoreIds` when id match fails.
 * @returns {string} Resolved preview URL, or `""` when not found.
 * @see resolveFilePreviewUrl
 */
export const extractUrlFromFilefetchResponse = (response, fileStoreId, index = 0) => {
  const data = response?.data;
  if (!data) return "";

  if (fileStoreId && data[fileStoreId]) {
    return resolveFilePreviewUrl(data[fileStoreId]);
  }

  const arr = data.fileStoreIds;
  if (Array.isArray(arr)) {
    const match =
      arr.find((o) => (o?.fileStoreId || o?.id) === fileStoreId) || arr[index];
    return resolveFilePreviewUrl(match?.url);
  }

  return "";
};

/**
 * Collect uploaded file references from form values for check-page preview.
 * Maps each file field config to `{ id, label, fileName, reference }` when a
 * fileStoreId can be extracted from the saved value.
 *
 * @param {Array}    fileFields File field configs from `buildSummarySections`.
 * @param {object}   formValues Flat form values for the step.
 * @param {Function} [t=(k) => k] i18n translator for field labels.
 * @returns {Array<{ id: string, label: string, fileName: string|null, reference: string }>}
 *   Non-null entries only (fields without an upload are omitted).
 * @see extractFileStoreId
 * @see resolveFieldLabelKey
 */
export const collectFormFileEntries = (fileFields = [], formValues = {}, t = (k) => k) =>
  fileFields
    .map((fc) => {
      const { field, apiFieldName } = fc;
      if (!field) return null;
      const raw =
        formValues[field.name] ??
        (apiFieldName ? formValues[apiFieldName] : undefined);
      const id = extractFileStoreId(raw);
      if (!id) return null;
      return {
        id,
        label: t(resolveFieldLabelKey(fc, formValues)),
        fileName: typeof raw === "object" ? raw.fileName || null : null,
        reference: id,
      };
    })
    .filter(Boolean);

/**
 * Resolve one field's display text on the check/summary page.
 * Mirrors DynamicFormField value shapes: dropdown/radio objects and option
 * lookups, date formatting, default text with optional units, and `apiFieldName`
 * / `extraData` fallbacks when the primary form value is empty.
 *
 * @param {object} fieldConfig Form field config entry from routeConfig.form.
 * @param {object} [options]
 * @param {object}   [options.formValues={}] Flat form values for the step.
 * @param {object}   [options.extraData={}] Supplemental values (e.g. computed fields).
 * @param {Function} [options.formatDate=formatCheckPageDate] Date formatter.
 * @param {Function} [options.checkNA=defaultCheckNA] Empty-value fallback helper.
 * @param {Function} [options.t=(k) => k] i18n translator.
 * @returns {string} Localized display string for the field.
 * @see formatCheckPageDate
 * @see defaultCheckNA
 */
export const resolveSummaryFieldValue = (
  fieldConfig,
  { formValues = {}, extraData = {}, formatDate = formatCheckPageDate, checkNA = defaultCheckNA, t = (k) => k } = {}
) => {
  const { field, options = [], apiFieldName } = fieldConfig;
  if (!field) return t("NA");

  const formVal = formValues[field.name];
  const apiAliasVal = apiFieldName ? formValues[apiFieldName] : undefined;
  const raw =
    (formVal !== undefined && formVal !== null && formVal !== ""
      ? formVal
      : undefined) ??
    (apiAliasVal !== undefined && apiAliasVal !== null && apiAliasVal !== ""
      ? apiAliasVal
      : undefined) ??
    extraData[field.name] ??
    (apiFieldName ? extraData[apiFieldName] : undefined);

  if (raw === undefined || raw === null || raw === "") return t("NA");

  switch (field.type) {
    case "date":
      return formatDate(raw);

    case "dropdown":
    case "radio": {
      if (typeof raw === "object") return t(raw.i18nKey || raw.name || raw.code || "NA");
      const opt = options.find((o) => String(o.code) === String(raw));
      return opt ? t(opt.i18nKey || opt.name || opt.code) : checkNA(raw);
    }

    default: {
      const text = checkNA(raw);
      return field.unit ? `${text} ${field.unit}` : text;
    }
  }
};
