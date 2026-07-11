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
  if (typeof codeOrObj === "object") {
    return enrichDropdownSelection(codeOrObj, options);
  }
  const matched =
    options.find((o) => optionCode(o) === optionCode(codeOrObj)) ||
    options.find((o) => o.code === codeOrObj);
  return matched || toDropdownOption(codeOrObj, nameHint || codeOrObj);
};

/** Merge a dropdown selection with the full MDMS/config option (multiplier, rentLabelKey, …). */
export const enrichDropdownSelection = (selected, options = []) => {
  if (!selected) return selected;
  if (!Array.isArray(options) || options.length === 0) {
    return typeof selected === "object" ? selected : selected;
  }
  const code = optionCode(selected);
  if (!code) return selected;
  const matched = options.find((o) => optionCode(o) === code);
  if (!matched) {
    return typeof selected === "object" ? selected : toDropdownOption(selected, selected);
  }
  if (typeof selected === "object") {
    return { ...matched, ...selected, code: matched.code || selected.code };
  }
  return matched;
};

/* ── shared field-value helpers ─────────────────────────────────────── */

/** Normalize dropdown/radio values to an uppercase code string. */
export const optionCode = (val) => {
  if (val === null || val === undefined || val === "") return "";
  if (typeof val === "object" && val.code) return String(val.code).trim().toUpperCase();
  return String(val).trim().toUpperCase();
};

/** Uppercase billing-cycle code from a dropdown value or plain string. */
export const normalizeBillingCycleCode = (val) => optionCode(val);

/** Legacy fallbacks when MDMS option metadata is missing (string codes only). */
const LEGACY_BILLING_CYCLE_MULTIPLIERS = {
  MONTHLY: 1,
  QUARTERLY: 3,
  YEARLY: 12,
};

/**
 * Rent multiplier from the selected billing-cycle option (MDMS-driven).
 * Option may carry `multiplier`, `rentMultiplier`, or `cycleMultiplier`.
 */
export const resolveBillingCycleMultiplier = (billingCycle, options = []) => {
  const enriched = enrichDropdownSelection(billingCycle, options);
  if (enriched && typeof enriched === "object") {
    for (const key of ["multiplier", "rentMultiplier", "cycleMultiplier"]) {
      const num = Number(enriched[key]);
      if (Number.isFinite(num) && num > 0) return num;
    }
  }
  const cycle = normalizeBillingCycleCode(enriched ?? billingCycle);
  const fromOptions = options.find((o) => optionCode(o) === cycle);
  if (fromOptions) {
    for (const key of ["multiplier", "rentMultiplier", "cycleMultiplier"]) {
      const num = Number(fromOptions[key]);
      if (Number.isFinite(num) && num > 0) return num;
    }
  }
  return LEGACY_BILLING_CYCLE_MULTIPLIERS[cycle] ?? 1;
};

const resolveLabelByCode = (labelBy, formValues = {}) => {
  const selected = formValues[labelBy.field];
  if (labelBy.optionKey && selected && typeof selected === "object") {
    const fromOption = selected[labelBy.optionKey];
    if (fromOption) return fromOption;
  }
  if (labelBy.optionKey) {
    const flatMetaKey = `${labelBy.field}${labelBy.optionKey.charAt(0).toUpperCase()}${labelBy.optionKey.slice(1)}`;
    if (formValues[flatMetaKey]) return formValues[flatMetaKey];
  }
  const raw = optionCode(selected);
  return labelBy.map?.[raw] || labelBy.defaultKey;
};

/**
 * Resolve the i18n label key for a field. Supports optional `field.labelBy`:
 *   labelBy: {
 *     field: "billingCycle",
 *     optionKey: "rentLabelKey",   // preferred — read from MDMS option
 *     map: { MONTHLY: "EST_..." },  // optional legacy fallback
 *     defaultKey: "EST_...",
 *   }
 */
export const resolveFieldLabelKey = (fieldConfig, formValues = {}) => {
  const labelBy = fieldConfig?.field?.labelBy;
  if (!labelBy) return fieldConfig?.summaryLabel || fieldConfig?.key;

  return resolveLabelByCode(labelBy, formValues);
};

/** Find a leaf field config by `field.name`. */
export const findFieldConfig = (formConfig = [], fieldName) =>
  flattenFormConfig(formConfig).find((fc) => fc.field?.name === fieldName);

const mergeFormField = (local, mdms) => {
  const mergedField = { ...local.field, ...mdms.field };
  // Local bindings for compute/label/prefill must survive MDMS field overrides.
  ["name", "computeFrom", "computeFn", "labelBy", "prefillFrom"].forEach((key) => {
    if (local.field?.[key] != null) mergedField[key] = local.field[key];
  });

  // Local validation/messages win — MDMS often over-escapes regex patterns (e.g. \\\\.).
  const merged = {
    ...local,
    ...mdms,
    field: mergedField,
    validation: { ...(mdms.validation || {}), ...(local.validation || {}) },
    messages: { ...(mdms.messages || {}), ...(local.messages || {}) },
  };
  if (Array.isArray(mdms.options) && mdms.options.length > 0) {
    merged.options = mdms.options;
  }
  return merged;
};

/**
 * Merge local field overrides onto an MDMS-driven form.
 * MDMS defines structure/order/options; local wins for compute, validation, prefill, labelBy.
 */
export const mergeFormFieldConfigs = (localForm = [], mdmsForm = []) => {
  if (!Array.isArray(mdmsForm) || mdmsForm.length === 0) {
    return sortByOrder(localForm);
  }
  if (!Array.isArray(localForm) || localForm.length === 0) {
    return sortByOrder(mdmsForm);
  }

  const localByName = new Map();
  const localByKey = new Map();
  flattenFormConfig(localForm).forEach((fc) => {
    if (fc?.field?.name) localByName.set(fc.field.name, fc);
    if (fc?.key) localByKey.set(fc.key, fc);
  });

  const resolveLocal = (item) => {
    const name = item?.field?.name;
    return (
      (name && localByName.get(name)) ||
      (item?.key && localByKey.get(item.key)) ||
      null
    );
  };

  const overlayMdmsItem = (mdmsItem) => {
    if (mdmsItem?.type === "group") {
      return {
        ...mdmsItem,
        children: (mdmsItem.children || []).map((child) => {
          const localChild = resolveLocal(child);
          return localChild ? mergeFormField(localChild, child) : child;
        }),
      };
    }
    if (mdmsItem?.type === "sectionHeader") {
      const localHeader = localByKey.get(mdmsItem.key);
      return localHeader ? { ...mdmsItem, ...localHeader, type: "sectionHeader" } : mdmsItem;
    }
    const localItem = resolveLocal(mdmsItem);
    return localItem ? mergeFormField(localItem, mdmsItem) : mdmsItem;
  };

  return sortByOrder(mdmsForm.map(overlayMdmsItem));
};

/** Rehydrate a flattened billing-cycle code using session metadata or form options. */
export const rehydrateBillingCycleOption = (flatData = {}, routeConfig = {}) => {
  const raw = flatData.billingCycle;
  if (raw && typeof raw === "object") return raw;

  const billingField = findFieldConfig(routeConfig?.form, "billingCycle");
  const fromOptions = resolveOption(
    raw,
    flatData.billingCycleName,
    billingField?.options || []
  );
  if (fromOptions && typeof fromOptions === "object") return fromOptions;

  if (!raw) return raw;

  const multiplier =
    flatData.billingCycleMultiplier ??
    flatData.billingCycleRentMultiplier ??
    flatData.billingCycleCycleMultiplier;
  const rentLabelKey = flatData.billingCycleRentLabelKey;
  if (multiplier != null || rentLabelKey) {
    return { code: raw, multiplier, rentLabelKey };
  }
  return raw;
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
      const staticOptions = item.options || [];
      const options =
        dropdownData[name] || dropdownData[item.key] || staticOptions;
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

    if (type === "file") {
      const raw = rawAsset[name] ?? (item.apiFieldName ? rawAsset[item.apiFieldName] : undefined);
      if (!raw) {
        result[name] = null;
        return;
      }
      if (typeof raw === "object") {
        result[name] = raw;
        return;
      }
      result[name] = { filestoreId: raw, documentuuid: raw, documentType: name };
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
      // Keep MDMS option metadata for config-driven rent/label resolution after flatten.
      const multiplier =
        value.multiplier ?? value.rentMultiplier ?? value.cycleMultiplier;
      if (multiplier != null && multiplier !== "") {
        payload[`${key}Multiplier`] = multiplier;
      }
      if (value.rentLabelKey) payload[`${key}RentLabelKey`] = value.rentLabelKey;
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
