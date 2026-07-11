// validators.js
// Each rule is a pure function: (value, ctx) => true (valid) | false (invalid)
// `ctx` = { fieldConfig, formData }
// Field-level rules run per field. Cross-field rules run once against the whole formData.

import { flattenFormConfig, resolveBillingCycleMultiplier, enrichDropdownSelection, toDate } from "./formUtils";

const isEmptyValue = (value, type) => {
  if (type === "dropdown") return !value || !value.code;
  return value === undefined || value === null || String(value).trim() === "";
};

export const fieldRules = {
  required: (value, { fieldConfig }) =>
    !isEmptyValue(value, fieldConfig.field.type),

  pattern: (value, { fieldConfig }) => {
    const { type } = fieldConfig.field;
    const { pattern } = fieldConfig.validation;
    if (!value || type === "dropdown") return true;
    const normalized = String(value).trim();
    if (!normalized) return true; // empty handled by `required`
    try {
      // Prefer patterns that avoid `\.` escaping (use `[.]` instead) so MDMS/JSON
      // double-escaping cannot turn a valid email into a false failure.
      return new RegExp(pattern).test(normalized);
    } catch (e) {
      console.error("Invalid validation.pattern:", pattern, e);
      return true;
    }
  },

  maxLength: (value, { fieldConfig }) => {
    const { maxLength } = fieldConfig.validation;
    if (!value) return true;
    return String(value).length <= maxLength;
  },

  /** Billing amounts must fit numeric(12,2) — max 9999999999.99 */
  maxAmount: (value, { fieldConfig }) => {
    const max = fieldConfig.validation.maxAmount ?? 9999999999.99;
    if (value === null || value === undefined || value === "") return true;
    const num = Number(String(value).replace(/,/g, "").trim());
    return Number.isFinite(num) && num >= 0 && num <= max;
  },
};

// Register a custom rule at runtime, e.g. registerFieldRule('min', fn)
export function registerFieldRule(name, fn) {
  fieldRules[name] = fn;
}

/**
 * Validates every leaf field (groups flattened) against its `validation`
 * block using fieldRules. Unknown validation keys (e.g. "regex", "disabled")
 * are ignored — they configure behavior, not rules.
 */
export function validateFields(formConfig, formData) {
  const errors = {};

  flattenFormConfig(formConfig).forEach((fieldConfig) => {
    const { field, validation = {} } = fieldConfig;
    if (!field) return;

    const value = formData[field.name];
    const ctx = { fieldConfig, formData };

    for (const ruleName of Object.keys(validation)) {
      const rule = fieldRules[ruleName];
      if (!rule) continue;
      if (validation[ruleName] === false) continue;
      if (!rule(value, ctx)) {
        errors[field.name] = true;
        break;
      }
    }
  });

  return errors;
}

/**
 * Cross-field validators are declared in routeConfig.crossFieldValidations:
 * [{ id, fields: [...names], validate: (formData) => boolean, message }]
 * Kept config-driven so dimension/area-style checks aren't hardcoded in the form.
 */
export function validateCrossField(crossFieldValidations = [], formData) {
  const errors = {};
  const failures = [];

  crossFieldValidations.forEach((rule) => {
    if (!rule.validate(formData)) {
      rule.fields.forEach((f) => { errors[f] = true; });
      failures.push(rule);
    }
  });

  return { errors, failures };
}

// ── computed-field helpers ──────────────────────────────────────────────
// Accepts "yyyy-MM-dd" strings (live DatePicker edits), epoch millis
// (prefill from a saved record), or Date objects.
// Returns whole months as a string, or "" until both dates are valid.
// NOTE: if the bound field's label says "In years", divide by 12 here or
// register a separate calculateDurationYears in COMPUTE_REGISTRY — don't
// let the label and the math disagree.
export const calculateDuration = (startDate, endDate) => {
  const start = toDate(startDate);
  const end = toDate(endDate);
  if (!start || !end || end < start) return "";

  let months =
    (end.getFullYear() - start.getFullYear()) * 12 +
    (end.getMonth() - start.getMonth());
  if (end.getDate() < start.getDate()) months -= 1; // don't count a partial month

  return months >= 0 ? String(months) : "";
};

/** Matches billing DB column numeric(12,2) — values must be below 10^10 */
export const MAX_TAX_AMOUNT = 9999999999.99;

const toPositiveNumber = (value) => {
  const num = Number(String(value ?? "").replace(/,/g, "").trim());
  return Number.isFinite(num) && num > 0 ? num : 0;
};

const formatRentAmount = (value) => {
  if (!Number.isFinite(value) || value <= 0 || value > MAX_TAX_AMOUNT) return "";
  const rounded = Math.round(value * 100) / 100;
  return Number.isInteger(rounded) ? String(rounded) : rounded.toFixed(2);
};

/**
 * Rent for the selected billing period = rate/sqft × plot area × cycle multiplier.
 * Used via field.computeFrom + field.computeFn in route config.
 */
export const calculateRentByBillingCycle = (
  rentRate,
  totalFloorArea,
  billingCycle,
  billingCycleOptions = []
) => {
  const rate = toPositiveNumber(rentRate);
  const area = toPositiveNumber(totalFloorArea);
  if (!rate || !area) return "";

  const multiplier = resolveBillingCycleMultiplier(billingCycle, billingCycleOptions);
  return formatRentAmount(rate * area * multiplier);
};
