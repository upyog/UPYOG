// formUtils.js
// Single source of truth for form-config walking, option shaping, prefill,
// payload flattening, and small DOM/date helpers used by DynamicForm.
//
// NOTE: toDropdownOption/resolveOption used to exist here AND in
// payloadUtils.js with CONFLICTING shapes (i18nKey=name vs i18nKey=code).
// This file is now the only implementation; payloadUtils re-exports it.

/* ── config walking ─────────────────────────────────────────────────── */

// Flattens a form config into a single list of leaf fieldConfigs,
// expanding "group" children. Shared by DynamicForm, useDynamicMDMS,
// validators — do NOT re-implement locally.
export const flattenFormConfig = (formConfig = []) =>
  formConfig.reduce((acc, fc) => {
    if (fc?.type === "group") return [...acc, ...(fc.children || [])];
    return [...acc, fc];
  }, []);

// Sorts top-level form items by "order", and any group's children too.
export const sortByOrder = (formConfig = []) => {
  const byOrder = (a, b) => (a.order ?? 0) - (b.order ?? 0);
  return [...formConfig].sort(byOrder).map((item) =>
    item.type === "group" && Array.isArray(item.children)
      ? { ...item, children: [...item.children].sort(byOrder) }
      : item
  );
};

/* ── dropdown option shaping (canonical) ────────────────────────────── */

// Canonical option shape. DynamicFormField renders optionKey="i18nKey",
// so i18nKey must be the DISPLAY label (name), never the raw code.
// `value` is kept for legacy consumers that keyed on it.
export const toDropdownOption = (code, name) => ({
  code,
  name: name || code,
  value: code,
  i18nKey: name || code,
});

// codeOrObj may be a raw code string, or an already-hydrated option object.
export const resolveOption = (codeOrObj, nameHint, options = []) => {
  if (!codeOrObj) return null;
  if (typeof codeOrObj === "object") return codeOrObj;
  return (
    options.find((o) => o.code === codeOrObj) ||
    toDropdownOption(codeOrObj, nameHint || codeOrObj)
  );
};

/* ── shared field-value helpers ─────────────────────────────────────── */

/** Normalize dropdown/radio values to an uppercase code string. */
export const optionCode = (val) => {
  if (val === null || val === undefined || val === "") return "";
  if (typeof val === "object" && val.code) return String(val.code).trim().toUpperCase();
  return String(val).trim().toUpperCase();
};

/**
 * Resolve the i18n label key for a field. Supports optional `field.labelBy`:
 *   labelBy: { field: "billingCycle", map: { MONTHLY: "EST_..." }, defaultKey: "EST_..." }
 */
export const resolveFieldLabelKey = (fieldConfig, formValues = {}) => {
  const labelBy = fieldConfig?.field?.labelBy;
  if (!labelBy) return fieldConfig?.summaryLabel || fieldConfig?.key;

  const code = optionCode(formValues[labelBy.field]);
  return labelBy.map?.[code] || labelBy.defaultKey || fieldConfig.key;
};

/** Names whose formData changes should re-render a field (value, label, compute). */
export const getFieldWatchNames = (fieldConfig) => {
  if (fieldConfig?.type === "group") {
    return (fieldConfig.children || []).flatMap(getFieldWatchNames);
  }
  const field = fieldConfig?.field;
  if (!field) return [];

  const names = new Set([field.name]);
  if (field.labelBy?.field) names.add(field.labelBy.field);
  (field.computeFrom || []).forEach((n) => names.add(n));
  if (field.prefillFrom) names.add(field.prefillFrom);
  return [...names];
};

/* ── prefill ────────────────────────────────────────────────────────── */

// Walks ANY module's form config (including groups) and builds initialData
// generically — no hardcoded field names.
export const buildInitialData = (formConfig = [], rawAsset = {}, dropdownData = {}, tenantId) => {
  const result = {};

  flattenFormConfig(formConfig).forEach((item) => {
    const field = item.field;
    if (!field) return;
    const { name, type, dataSource } = field;

    if (type === "dropdown") {
      if (dataSource?.defaultValueSource === "tenantId") {
        // Prefer the hook-built option (it carries the TRANSLATED city name);
        // synthesize from the raw tenantId only as a fallback.
        result[name] = dropdownData[name]?.[0] || toDropdownOption(tenantId, tenantId);
        return;
      }
      const rawVal = rawAsset[name];
      const rawNameHint = rawAsset[`${name}Name`];
      const options = dropdownData[name] || dropdownData[item.key] || [];
      result[name] = rawVal ? resolveOption(rawVal, rawNameHint, options) : null;
      return;
    }

    if (type === "radio") {
      const apiAlias = item.apiFieldName;
      const rawVal =
        rawAsset[name] ??
        (apiAlias ? rawAsset[apiAlias] : undefined) ??
        "";
      if (rawVal && typeof rawVal === "object" && rawVal.code) {
        result[name] = String(rawVal.code).trim().toUpperCase();
      } else {
        result[name] = String(rawVal || "").trim().toUpperCase();
      }
      return;
    }

    const raw = rawAsset[name];
    const isEmpty = raw === undefined || raw === null || raw === "";
    if (isEmpty && field.prefillFrom) {
      const fromVal = rawAsset[field.prefillFrom];
      result[name] = fromVal !== undefined && fromVal !== null ? fromVal : "";
      return;
    }
    result[name] = raw ?? "";
  });

  return result;
};

/* ── submit payload ─────────────────────────────────────────────────── */

// Flattens formData for submission:
//   { assetType: { code: "LAND", name: "Land" } } → { assetType: "LAND", assetTypeName: "Land" }
// Non-dropdown (plain string/number) values pass through unchanged.
// For the richer API mapping (apiFieldName renames, numeric casts,
// staticFields/computedFields) use payloadUtils.buildApiPayload — this one
// intentionally stays a dumb flattener for wizard session storage.
export const buildPayload = (formData = {}) => {
  const payload = {};
  Object.entries(formData).forEach(([key, value]) => {
    if (value && typeof value === "object" && "code" in value) {
      payload[key] = value.code;
      payload[`${key}Name`] = value.name ?? value.i18nKey ?? value.code;
    } else {
      payload[key] = value;
    }
  });
  return payload;
};

/* ── date helpers (shared by DynamicFormField / summary pages) ──────── */

// Accepts "yyyy-MM-dd" strings, epoch millis, or Date objects → Date | null.
export const toDate = (v) => {
  if (!v && v !== 0) return null;
  const d = v instanceof Date ? v : new Date(v);
  return isNaN(d.getTime()) ? null : d;
};

// Native <input type="date"> needs a "yyyy-MM-dd" STRING. Built from LOCAL
// date parts — toISOString() shifts IST-midnight epochs back a day (UTC).
export const toInputDate = (v) => {
  if (typeof v === "string" && /^\d{4}-\d{2}-\d{2}$/.test(v)) return v;
  const d = toDate(v);
  if (!d) return "";
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
};

/* ── DOM helper ─────────────────────────────────────────────────────── */

// Scrolls the first element tagged data-field-error="true" into view.
// DynamicFormField sets that attribute on errored fields.
export const scrollToFirstError = () => {
  requestAnimationFrame(() => {
    document
      .querySelector('[data-field-error="true"]')
      ?.scrollIntoView({ behavior: "smooth", block: "center" });
  });
};
