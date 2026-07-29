/**
 * validators.js
 *
 * Field-level and cross-field validation plus computed-field helpers for
 * DynamicForm. Each field rule is a pure function: (value, ctx) => boolean
 * where ctx = { fieldConfig, formData }. Field rules run per visible leaf;
 * cross-field rules run once against the whole formData.
 *
 * Exports
 * -------
 * - fieldRules / registerFieldRule — extensible per-field validators
 * - validateFields                 — scan form config → { [fieldName]: true }
 * - validateCrossField             — routeConfig.crossFieldValidations
 * - calculateDuration / formatDurationDisplay
 * - calculateRentByBillingCycle / MAX_TAX_AMOUNT
 *
 * @see DynamicForm (COMPUTE_REGISTRY, goNext)
 * @see formUtils.isFieldVisible
 */

import { flattenFormConfig, resolveBillingCycleMultiplier, enrichDropdownSelection, toDate, isFieldVisible } from "./formUtils";

/**
 * True when a field value is considered empty for required checks.
 * Dropdowns need a selected option with .code.
 *
 * @param {*}      value
 * @param {string} type - field.type
 * @returns {boolean}
 */
const isEmptyValue = (value, type) => {
  if (type === "dropdown") return !value || !value.code;
  return value === undefined || value === null || String(value).trim() === "";
};

/**
 * Built-in field validators keyed by validation block property names
 * (required, pattern, maxLength, maxAmount). Unknown keys are ignored.
 */
export const fieldRules = {
  /**
   * @param {*} value
   * @param {{ fieldConfig: object }} ctx
   * @returns {boolean}
   */
  required: (value, { fieldConfig }) =>
    !isEmptyValue(value, fieldConfig.field.type),

  /**
   * Regex test against validation.pattern. Empty values pass (use required).
   * Prefer patterns that avoid `\.` escaping (use `[.]`) so MDMS/JSON
   * double-escaping cannot turn a valid email into a false failure.
   *
   * @param {*} value
   * @param {{ fieldConfig: object }} ctx
   * @returns {boolean}
   */
  pattern: (value, { fieldConfig }) => {
    const { type } = fieldConfig.field;
    const { pattern } = fieldConfig.validation;
    if (!value || type === "dropdown") return true;
    const normalized = String(value).trim();
    if (!normalized) return true; // empty handled by `required`
    try {
      return new RegExp(pattern).test(normalized);
    } catch (e) {
      console.error("Invalid validation.pattern:", pattern, e);
      return true;
    }
  },

  /**
   * @param {*} value
   * @param {{ fieldConfig: object }} ctx
   * @returns {boolean}
   */
  maxLength: (value, { fieldConfig }) => {
    const { maxLength } = fieldConfig.validation;
    if (!value) return true;
    return String(value).length <= maxLength;
  },

  /**
   * Billing amounts must fit numeric(12,2) — max 9999999999.99 by default.
   * Override via validation.maxAmount.
   *
   * @param {*} value
   * @param {{ fieldConfig: object }} ctx
   * @returns {boolean}
   */
  maxAmount: (value, { fieldConfig }) => {
    const max = fieldConfig.validation.maxAmount ?? 9999999999.99;
    if (value === null || value === undefined || value === "") return true;
    const num = Number(String(value).replace(/,/g, "").trim());
    return Number.isFinite(num) && num >= 0 && num <= max;
  },
};

/**
 * Register a custom field rule at runtime, e.g. registerFieldRule('min', fn).
 *
 * @param {string}   name - Key matching validation.<name> in field config.
 * @param {Function} fn   - (value, ctx) => boolean.
 */
export function registerFieldRule(name, fn) {
  fieldRules[name] = fn;
}

/**
 * Validates every visible leaf field (groups flattened) against its
 * `validation` block using fieldRules. Unknown validation keys (e.g. "regex",
 * "disabled") are ignored — they configure UI behavior, not rules.
 *
 * @param {array}  formConfig - routeConfig.form
 * @param {object} formData   - Current DynamicForm state
 * @returns {object} Map of fieldName → true when invalid
 */
export function validateFields(formConfig, formData) {
  const errors = {};

  flattenFormConfig(formConfig).forEach((fieldConfig) => {
    const { field, validation = {} } = fieldConfig;
    if (!field) return;
    if (!isFieldVisible(fieldConfig, formData)) return;

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
 * Cross-field validators declared in routeConfig.crossFieldValidations:
 * [{ id, fields: [...names], validate: (formData) => boolean, message }]
 * Kept config-driven so dimension/area-style checks aren't hardcoded in the form.
 *
 * @param {array}  [crossFieldValidations]
 * @param {object} formData
 * @returns {{ errors: object, failures: array }}
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

// ── computed-field helpers (COMPUTE_REGISTRY in DynamicForm) ────────────

/**
 * Whole months between start and end dates as a string (API stores int months),
 * or "" until both dates are valid / end >= start.
 * Accepts "yyyy-MM-dd" strings, epoch millis, or Date objects.
 * Use formatDurationDisplay() for the auto-populated UI text.
 *
 * @param {string|number|Date} startDate
 * @param {string|number|Date} endDate
 * @returns {string}
 */
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

/**
 * UI label for duration months: "N months" or "Y years M months" when > 12.
 *
 * @param {string|number|null|undefined} totalMonths
 * @returns {string}
 */
export const formatDurationDisplay = (totalMonths) => {
  if (totalMonths === null || totalMonths === undefined || totalMonths === "") return "";
  const months = Number(totalMonths);
  if (!Number.isFinite(months) || months < 0) return "";
  if (months <= 12) return  `${months} ${months === 1 ? "month" : "months"}`;

  const years = Math.floor(months / 12);
  const rem = months % 12;
  const parts = [];
  if (years) parts.push(`${years} ${years === 1 ? "year" : "years"}`);
  if (rem) parts.push(`${rem} ${rem === 1 ? "month" : "months"}`);
  return parts.join(" ");
};

/** Matches billing DB column numeric(12,2) — values must be below 10^10. */
export const MAX_TAX_AMOUNT = 9999999999.99;

/**
 * Parse a positive number from a form string (strips commas). Returns 0 if invalid.
 * @param {*} value
 * @returns {number}
 */
const toPositiveNumber = (value) => {
  const num = Number(String(value ?? "").replace(/,/g, "").trim());
  return Number.isFinite(num) && num > 0 ? num : 0;
};

/**
 * Format a computed rent amount for form storage; "" if out of range.
 * @param {number} value
 * @returns {string}
 */
const formatRentAmount = (value) => {
  if (!Number.isFinite(value) || value <= 0 || value > MAX_TAX_AMOUNT) return "";
  const rounded = Math.round(value * 100) / 100;
  return Number.isInteger(rounded) ? String(rounded) : rounded.toFixed(2);
};

/**
 * Rent for the selected billing period = rate/sqft × plot area × cycle multiplier.
 * Used via field.computeFrom + field.computeFn in route config.
 * DynamicForm passes billingCycleOptions as a trailing arg.
 *
 * @param {*}      rentRate
 * @param {*}      totalFloorArea
 * @param {*}      billingCycle         Code or option object.
 * @param {array}  [billingCycleOptions] MDMS options with multipliers.
 * @returns {string}
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
