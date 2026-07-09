// checkPageUtils.js
// Shared helpers for DynamicCheckPage and module-specific check wrappers.
// Works with the same routeConfig.form that DynamicForm / DynamicFormField use.

import { sortByOrder } from "./formUtils";

/** Summary fallback when a value is empty. */
export const defaultCheckNA = (value, fallback = "NA") =>
  value === undefined || value === null || value === "" ? fallback : value;

/**
 * Parse epoch (ms/s), DD-MM-YYYY, ISO, or Date into a Date (or null).
 * Shared by summary pages and any module that needs flexible date display.
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

/** Format any supported date value for check-page display. */
export const formatCheckPageDate = (value, { fallback = "N/A", locale = "en-IN" } = {}) => {
  const d = parseFlexibleDate(value);
  if (!d) return fallback;
  return d.toLocaleDateString(locale);
};

/**
 * Find a wizard step from an MDMS config array (or config.body).
 * Falls back to the first step that has a non-empty form.
 */
export const resolveRouteConfigFromSteps = (config, stepKey) => {
  const steps = Array.isArray(config) ? config : config?.body || [];
  return (
    steps.find((step) => step.key === stepKey) ||
    steps.find((step) => Array.isArray(step.form) && step.form.length > 0) ||
    {}
  );
};

/** Merge MDMS route entry with a module's local form-config overrides. */
export const mergeRouteConfig = (rawRouteConfig = {}, localFormConfig = {}) => ({
  ...rawRouteConfig,
  ...localFormConfig,
});

/** Session key where each wizard step's resolved routeConfig is stored. */
export const ROUTE_CONFIG_SESSION_KEY = "routeConfigs";

/** Marker attached to step data when a form page passes its routeConfig upstream. */
export const ROUTE_CONFIG_STEP_MARKER = "__routeConfig";

/** Remove the routeConfig marker from step payload before persisting form values. */
export const stripRouteConfigMarker = (stepData = {}) => {
  if (!stepData || typeof stepData !== "object") return stepData;
  const { [ROUTE_CONFIG_STEP_MARKER]: _removed, ...rest } = stepData;
  return rest;
};

/** Attach the active routeConfig so the wizard parent can store it in session. */
export const attachRouteConfigToStepData = (stepData = {}, routeConfig) => {
  if (!routeConfig) return stepData;
  return { ...stepData, [ROUTE_CONFIG_STEP_MARKER]: routeConfig };
};

/**
 * Persist form step values + routeConfig snapshot from the form page.
 * Call from parent handleSelect so check pages reuse the same config.
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
 * Route config for check/submit: prefer session snapshot from form step,
 * fall back to MDMS wizard steps when session has no snapshot yet.
 */
export const resolveActiveRouteConfig = (sessionValue, mdmsSteps, stepKey) =>
  sessionValue?.[ROUTE_CONFIG_SESSION_KEY]?.[stepKey] ||
  resolveRouteConfigFromSteps(mdmsSteps, stepKey);

/**
 * Read flat form values saved by DynamicForm's onSelect:
 *   onSelect(stepKey, { [payloadKey]: [formVal] })
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

/** Flatten form config for summary rendering (groups expanded, ordered). */
export const flattenForSummary = (formConfig = []) =>
  sortByOrder(formConfig).reduce((acc, fc) => {
    if (fc.type === "group") {
      return [...acc, ...sortByOrder(fc.children || [])];
    }
    return [...acc, fc];
  }, []);

/**
 * Split a form config into summary sections and file fields.
 * sectionHeader entries start a new section; file fields are collected separately.
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
 * Resolve one field's display text on the check page.
 * Mirrors DynamicFormField value shapes (dropdown/radio objects, dates, units).
 */
export const resolveSummaryFieldValue = (
  fieldConfig,
  { formValues = {}, extraData = {}, formatDate = formatCheckPageDate, checkNA = defaultCheckNA, t = (k) => k } = {}
) => {
  const { field, options = [] } = fieldConfig;
  if (!field) return t("NA");

  const formVal = formValues[field.name];
  const raw =
    formVal !== undefined && formVal !== null && formVal !== ""
      ? formVal
      : extraData[field.name];

  if (raw === undefined || raw === null || raw === "") return t("NA");

  switch (field.type) {
    case "date":
      return formatDate(raw);

    case "dropdown":
    case "radio": {
      if (typeof raw === "object") return t(raw.i18nKey || raw.code || "NA");
      const opt = options.find((o) => o.code === raw);
      return opt ? t(opt.i18nKey || opt.code) : checkNA(raw);
    }

    default: {
      const text = checkNA(raw);
      return field.unit ? `${text} ${field.unit}` : text;
    }
  }
};
