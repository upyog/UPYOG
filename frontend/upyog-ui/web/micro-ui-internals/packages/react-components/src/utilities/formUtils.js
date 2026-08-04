/**
 * formUtils.js
 *
 * Single source of truth for form-config walking, option shaping, prefill,
 * payload flattening, and small DOM/date helpers used by DynamicForm and
 * related wizard/check-page utilities.
 *
 * Responsibilities
 * ----------------
 * 1. Walk and normalize MDMS-driven form configs (flatten groups, sort by order,
 *    merge local overrides onto MDMS field definitions).
 * 2. Shape dropdown/radio option objects into the canonical `{ code, name, value, i18nKey }`
 *    format expected by DynamicFormField (i18nKey = display label, never raw code).
 * 3. Resolve option codes, enrich selections with MDMS metadata (multiplier,
 *    rentLabelKey, …), and derive billing-cycle rent multipliers.
 * 4. Build initial form values from raw API/edit assets and build flattened
 *    session payloads for wizard storage.
 * 5. Provide shared date parsing/formatting and scroll-to-error DOM helpers.
 *
 * Exports
 * -------
 * Config walking:
 *   - flattenFormConfig       Expand group children into a flat field list.
 *   - sortByOrder             Sort top-level items and group children by `order`.
 *   - findFieldConfig         Find a leaf field config by `field.name`.
 *   - mergeFormFieldConfigs   Overlay local field overrides onto an MDMS form.
 *
 * Dropdown / option shaping:
 *   - toDropdownOption        Build canonical option object from code + name.
 *   - resolveOption           Resolve a code string or object to a full option.
 *   - enrichDropdownSelection Merge a selection with matching MDMS/config option.
 *   - optionCode              Normalize a value to an uppercase code string.
 *   - normalizeBillingCycleCode  Uppercase billing-cycle code helper.
 *   - resolveBillingCycleMultiplier  Rent multiplier from billing-cycle option.
 *   - rehydrateBillingCycleOption    Rebuild billing-cycle object from flat session data.
 *
 * Field metadata / visibility:
 *   - resolveFieldLabelKey    Resolve i18n label key (supports `field.labelBy`).
 *   - getFieldWatchNames      Names whose changes should re-render a field.
 *   - isFieldVisible          Config-driven show/hide (hidden / visibleWhen).
 *
 * Prefill / payload:
 *   - buildInitialData        Generic initial values from form config + raw asset.
 *   - buildPayload            Flatten formData for wizard session storage.
 *
 * Date / DOM helpers:
 *   - toDate                  Parse string/epoch/Date → Date | null.
 *   - toInputDate             Format value as local yyyy-MM-dd for `<input type="date">`.
 *   - scrollToFirstError      Scroll first `[data-field-error="true"]` into view.
 *
 * NOTE: toDropdownOption/resolveOption used to exist here AND in
 * payloadUtils.js with CONFLICTING shapes (i18nKey=name vs i18nKey=code).
 * This file is now the only implementation; payloadUtils re-exports it.
 *
 * @see DynamicForm
 * @see DynamicFormField
 * @see payloadUtils.buildApiPayload  Richer API mapping (apiFieldName, numeric casts, …).
 */

/* ── config walking ─────────────────────────────────────────────────── */

/**
 * Flattens a form config into a single list of leaf fieldConfigs,
 * expanding `group` children. Shared by DynamicForm, useDynamicMDMS,
 * validators — do NOT re-implement locally.
 *
 * @param {Array<object>} [formConfig=[]] Top-level form config array (may contain groups).
 * @returns {Array<object>} Flat list of leaf field config objects (groups removed).
 */
export const flattenFormConfig = (formConfig = []) =>
  formConfig.reduce((acc, fc) => {
    if (fc?.type === "group") return [...acc, ...(fc.children || [])];
    return [...acc, fc];
  }, []);

/**
 * Sorts top-level form items by `order`, and any group's children too.
 * Returns a new array; does not mutate the input.
 *
 * @param {Array<object>} [formConfig=[]] Top-level form config array.
 * @returns {Array<object>} Sorted copy of formConfig (group children sorted in-place on copies).
 */
export const sortByOrder = (formConfig = []) => {
  const byOrder = (a, b) => (a.order ?? 0) - (b.order ?? 0);
  return [...formConfig].sort(byOrder).map((item) =>
    item.type === "group" && Array.isArray(item.children)
      ? { ...item, children: [...item.children].sort(byOrder) }
      : item
  );
};

/* ── dropdown option shaping (canonical) ────────────────────────────── */

/**
 * Build the canonical dropdown option shape.
 * DynamicFormField renders `optionKey="i18nKey"`, so i18nKey must be the
 * DISPLAY label (name), never the raw code. `value` is kept for legacy
 * consumers that keyed on it.
 *
 * @param {string} code  Option code (stored value / API key).
 * @param {string} [name] Display label; falls back to `code` when absent.
 * @returns {{ code: string, name: string, value: string, i18nKey: string }}
 */
export const toDropdownOption = (code, name) => ({
  code,
  name: name || code,
  value: code,
  i18nKey: name || code,
});

/**
 * Resolve a raw code string or an already-hydrated option object into a
 * full option. When given a string, looks up `options` by code; synthesizes
 * via toDropdownOption when no match is found.
 *
 * @param {string|object|null|undefined} codeOrObj  Raw code or option object.
 * @param {string} [nameHint]                       Fallback display name when synthesizing.
 * @param {Array<object>} [options=[]]              Available options for lookup.
 * @returns {object|null} Resolved option object, or null when input is falsy.
 */
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

/**
 * Merge a dropdown selection with the full MDMS/config option so metadata
 * such as multiplier, rentLabelKey, etc. is available on the selected value.
 *
 * @param {string|object|null|undefined} selected  Current selection (code or object).
 * @param {Array<object>} [options=[]]             Full option list from MDMS/config.
 * @returns {string|object|null|undefined} Enriched selection, or original when no match.
 */
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

/**
 * Normalize dropdown/radio values to an uppercase code string.
 * Objects with a `code` property use that; plain strings are trimmed and uppercased.
 *
 * @param {string|object|null|undefined} val  Field value or option object.
 * @returns {string} Uppercase code, or empty string when value is null/undefined/"".
 */
export const optionCode = (val) => {
  if (val === null || val === undefined || val === "") return "";
  if (typeof val === "object" && val.code) return String(val.code).trim().toUpperCase();
  return String(val).trim().toUpperCase();
};

/**
 * Uppercase billing-cycle code from a dropdown value or plain string.
 * Thin wrapper around optionCode for billing-cycle-specific call sites.
 *
 * @param {string|object|null|undefined} val  Billing-cycle value or option.
 * @returns {string} Uppercase billing-cycle code, or empty string.
 */
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
 * Falls back to LEGACY_BILLING_CYCLE_MULTIPLIERS when MDMS metadata is absent.
 *
 * @param {string|object|null|undefined} billingCycle  Selected billing-cycle value.
 * @param {Array<object>} [options=[]]                   Billing-cycle option list from MDMS.
 * @returns {number} Positive rent multiplier (defaults to 1).
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

/**
 * Resolve an i18n label key from a `labelBy` rule and current form values.
 * Prefers the selected option's `optionKey` property, then a flattened
 * meta key on formValues, then the legacy `map` lookup by code, then `defaultKey`.
 *
 * @param {object} labelBy                    Label resolution rule from field config.
 * @param {string} labelBy.field              Form field name whose value drives the label.
 * @param {string} [labelBy.optionKey]        Property on the selected option (e.g. rentLabelKey).
 * @param {object} [labelBy.map]              Legacy code → i18n key map.
 * @param {string} [labelBy.defaultKey]       Fallback i18n key.
 * @param {object} [formValues={}]            Current form data.
 * @returns {string|undefined} Resolved i18n label key, or undefined when none match.
 */
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
 *
 * When no labelBy is configured, returns summaryLabel or the field's config key.
 *
 * @param {object} fieldConfig              Leaf field config object.
 * @param {object} [formValues={}]          Current form data for labelBy resolution.
 * @returns {string|undefined} i18n label key for the field heading/label.
 */
export const resolveFieldLabelKey = (fieldConfig, formValues = {}) => {
  const labelBy = fieldConfig?.field?.labelBy;
  if (!labelBy) return fieldConfig?.summaryLabel || fieldConfig?.key;

  return resolveLabelByCode(labelBy, formValues);
};

/**
 * Find a leaf field config by `field.name` within a (possibly grouped) form config.
 *
 * @param {Array<object>} [formConfig=[]] Form config array (groups are flattened internally).
 * @param {string} fieldName              Target `field.name` value.
 * @returns {object|undefined} Matching leaf field config, or undefined.
 */
export const findFieldConfig = (formConfig = [], fieldName) =>
  flattenFormConfig(formConfig).find((fc) => fc.field?.name === fieldName);

/**
 * Merge a single local field override onto its MDMS counterpart.
 * Local wins for compute bindings (computeFrom, computeFn, labelBy, prefillFrom),
 * validation/messages, apiFieldName, submitKey, excludeFromPayload, and hidden.
 * MDMS options replace local options when MDMS provides a non-empty options array.
 *
 * @param {object} local  Local field config override.
 * @param {object} mdms   MDMS field config baseline.
 * @returns {object} Merged field config object.
 */
const mergeFormField = (local, mdms) => {
  const mergedField = { ...local.field, ...mdms.field };
  // Local bindings for compute/label/prefill must survive MDMS field overrides.
  ["name", "computeFrom", "computeFn", "labelBy", "prefillFrom", "dataSource", "numeric", "unit", "defaultValue", "minDate", "createNewPath", "searchButton"].forEach((key) => {
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
  ["apiFieldName", "submitKey", "excludeFromPayload", "hidden", "summaryLabel"].forEach((key) => {
    if (local[key] != null) merged[key] = local[key];
  });
  if (Array.isArray(mdms.options) && mdms.options.length > 0) {
    merged.options = mdms.options;
  }
  return merged;
};

/**
 * Merge local field overrides onto an MDMS-driven form.
 * MDMS defines structure/order/options; local wins for compute, validation, prefill, labelBy.
 * When either side is empty, returns the other side sorted by order.
 *
 * @param {Array<object>} [localForm=[]]  Module-local form config overrides.
 * @param {Array<object>} [mdmsForm=[]]   MDMS-driven form config baseline.
 * @returns {Array<object>} Merged, order-sorted form config array.
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

/**
 * Rehydrate a flattened billing-cycle code using session metadata or form options.
 * When flatData already holds an object, returns it unchanged. Otherwise resolves
 * from routeConfig options or reconstructs from flattened multiplier/rentLabelKey keys.
 *
 * @param {object} [flatData={}]     Flattened session/wizard data (may contain billingCycle code).
 * @param {object} [routeConfig={}]  Route config with `form` array for option lookup.
 * @returns {string|object|null|undefined} Rehydrated billing-cycle option object or raw code.
 */
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

/**
 * Collect form field names whose value changes should trigger a re-render
 * of the given field (value, dynamic label, compute dependencies, visibility).
 * Recurses into group children.
 *
 * @param {object} fieldConfig  Field or group config object.
 * @returns {Array<string>} Unique list of watched form field names.
 */
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
  if (fieldConfig.visibleWhen?.field) names.add(fieldConfig.visibleWhen.field);
  return [...names];
};

/**
 * Config-driven show/hide for a field.
 * Supports `hidden: true`, or `visibleWhen: { field, equals }` / `{ field, in: [...] }`.
 * Dropdown/object values are compared by uppercase `code`; plain strings are uppercased.
 *
 * @param {object} fieldConfig         Leaf or group field config.
 * @param {object} [formData={}]       Current form values.
 * @returns {boolean} True when the field should be rendered.
 */
export const isFieldVisible = (fieldConfig, formData = {}) => {
  if (fieldConfig?.hidden === true) return false;

  const rule = fieldConfig?.visibleWhen;
  if (!rule?.field) return true;

  const raw = formData[rule.field];
  const current =
    raw && typeof raw === "object" && raw.code != null
      ? String(raw.code).trim().toUpperCase()
      : String(raw ?? "").trim().toUpperCase();

  if (rule.equals !== undefined) {
    return current === String(rule.equals).trim().toUpperCase();
  }
  if (Array.isArray(rule.in)) {
    return rule.in.map((v) => String(v).trim().toUpperCase()).includes(current);
  }
  return true;
};

/* ── prefill ────────────────────────────────────────────────────────── */

/**
 * Walk any module's form config (including groups) and build initialData
 * generically — no hardcoded field names.
 *
 * Handles dropdown (with tenantId defaultValueSource), radio, file, date,
 * prefillFrom, and defaultValue (including `"today"` for date fields).
 *
 * @param {Array<object>} [formConfig=[]]   Form config array.
 * @param {object} [rawAsset={}]            Raw API/edit asset keyed by field name.
 * @param {object} [dropdownData={}]        Hook-built dropdown options keyed by field name.
 * @param {string} [tenantId]               Current tenant id (for tenantId dataSource fields).
 * @returns {object} Initial form values keyed by field.name.
 */
export const buildInitialData = (formConfig = [], rawAsset = {}, dropdownData = {}, tenantId, options = {}) => {
  const { applyDefaults = true } = options;
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
      const rawVal = rawAsset[name] ?? (applyDefaults ? field.defaultValue : undefined);
      const rawNameHint = rawAsset[`${name}Name`];
      const staticOptions = item.options || [];
      const optionsList =
        dropdownData[name] || dropdownData[item.key] || staticOptions;
      result[name] = rawVal ? resolveOption(rawVal, rawNameHint, optionsList) : null;
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

    const raw =
      rawAsset[name] ??
      (item.apiFieldName ? rawAsset[item.apiFieldName] : undefined);
    const isEmpty = raw === undefined || raw === null || raw === "";
    if (applyDefaults && isEmpty && field.prefillFrom) {
      const fromVal = rawAsset[field.prefillFrom];
      result[name] = fromVal !== undefined && fromVal !== null ? fromVal : "";
      return;
    }
    if (applyDefaults && isEmpty && field.defaultValue !== undefined && field.defaultValue !== null) {
      // "today" → local yyyy-MM-dd via toInputDate (avoids UTC day-shift).
      if (field.defaultValue === "today" && type === "date") {
        result[name] = toInputDate(new Date());
        return;
      }
      result[name] = field.defaultValue;
      return;
    }
    result[name] =
      raw === undefined || raw === null
        ? ""
        : typeof raw === "number"
          ? String(raw)
          : raw;
  });

  return result;
};

/* ── submit payload ─────────────────────────────────────────────────── */

/**
 * Flatten formData for wizard session storage / step persistence:
 *   `{ assetType: { code: "LAND", name: "Land" } }`
 *     → `{ assetType: "LAND", assetTypeName: "Land" }`
 *
 * Non-dropdown (plain string/number) values pass through unchanged.
 * Preserves MDMS option metadata (multiplier, rentLabelKey) as flattened keys.
 *
 * For richer API mapping (apiFieldName renames, numeric casts,
 * staticFields/computedFields) use payloadUtils.buildApiPayload — this
 * intentionally stays a dumb flattener.
 *
 * @param {object} [formData={}] Live form values keyed by field name.
 * @returns {object} Flattened payload suitable for wizard session storage.
 */
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

/**
 * Parse a value into a Date object.
 * Accepts "yyyy-MM-dd" strings, epoch millis, or Date objects.
 *
 * @param {string|number|Date|null|undefined} v  Input value.
 * @returns {Date|null} Parsed Date, or null when input is invalid/empty.
 */
export const toDate = (v) => {
  if (!v && v !== 0) return null;
  const d = v instanceof Date ? v : new Date(v);
  return isNaN(d.getTime()) ? null : d;
};

/**
 * Format a value as a local yyyy-MM-dd string for native `<input type="date">`.
 * Built from local date parts — toISOString() shifts IST-midnight epochs back a day (UTC).
 * Already-formatted yyyy-MM-dd strings pass through unchanged.
 *
 * @param {string|number|Date|null|undefined} v  Input value.
 * @returns {string} yyyy-MM-dd string, or empty string when unparseable.
 */
export const toInputDate = (v) => {
  if (typeof v === "string" && /^\d{4}-\d{2}-\d{2}$/.test(v)) return v;
  const d = toDate(v);
  if (!d) return "";
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
};

/* ── DOM helper ─────────────────────────────────────────────────────── */

/**
 * Scroll the first element tagged `data-field-error="true"` into view.
 * DynamicFormField sets that attribute on errored fields.
 * Uses requestAnimationFrame so the DOM has updated before querying.
 *
 * @returns {void}
 */
export const scrollToFirstError = () => {
  requestAnimationFrame(() => {
    document
      .querySelector('[data-field-error="true"]')
      ?.scrollIntoView({ behavior: "smooth", block: "center" });
  });
};
