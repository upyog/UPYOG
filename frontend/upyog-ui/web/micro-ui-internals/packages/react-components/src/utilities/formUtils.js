export const toDropdownOption = (code, name) => ({
  code,
  name,
  value: code,
  i18nKey: name,
});

// codeOrObj may be a raw code string, or an already-hydrated option object
export const resolveOption = (codeOrObj, nameHint, options = []) => {
  if (!codeOrObj) return null;
  if (typeof codeOrObj === "object") return codeOrObj;
  const found = options.find((o) => o.code === codeOrObj);
  if (found) return found;
  return toDropdownOption(codeOrObj, nameHint || codeOrObj);
};

// Walks ANY module's form config (including groups) and builds initialData
// generically — no hardcoded field names, so it works for estate, or any
// other module reusing DynamicForm.
export const buildInitialData = (formConfig = [], rawAsset = {}, dropdownData = {}, tenantId) => {
  const result = {};

  const walk = (items) => {
    items.forEach((item) => {
      if (item.type === "group" && Array.isArray(item.children)) {
        walk(item.children);
        return;
      }

      const field = item.field;
      if (!field) return;
      const { name, type, dataSource } = field;

      if (type === "dropdown") {
        if (dataSource?.defaultValueSource === "tenantId") {
          result[name] = toDropdownOption(tenantId, tenantId);
          return;
        }
        const rawVal = rawAsset[name];
        const rawNameHint = rawAsset[`${name}Name`];
        const options = dropdownData[name] || dropdownData[item.key] || [];
        result[name] = rawVal ? resolveOption(rawVal, rawNameHint, options) : null;
        return;
      }

      result[name] = rawAsset[name] ?? "";
    });
  };

  walk(formConfig);
  return result;
};

// ── sortByOrder ─────────────────────────────────────────────────────────
// Sorts top-level form items by "order", and sorts any group's children too.
export const sortByOrder = (formConfig = []) => {
  const sorted = [...formConfig].sort((a, b) => (a.order ?? 0) - (b.order ?? 0));
  return sorted.map((item) => {
    if (item.type === "group" && Array.isArray(item.children)) {
      return { ...item, children: [...item.children].sort((a, b) => (a.order ?? 0) - (b.order ?? 0)) };
    }
    return item;
  });
};

// ── buildPayload ────────────────────────────────────────────────────────
// Flattens formData for submission:
//   { city: { code: "pg.citya", name: "pg.citya", ... } }
//     → { city: "pg.citya", cityName: "pg.citya" }
//   { assetType: { code: "ASSET_TYPE_RESIDENTIAL", name: "Residential" } }
//     → { assetType: "ASSET_TYPE_RESIDENTIAL", assetTypeName: "Residential" }
// Non-dropdown (plain string/number) values pass through unchanged.
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

// ── scrollToFirstError ────────────────────────────────────────────────
// Scrolls the first element with a validation error into view.
// Assumes DynamicFormField (or its wrapper) tags errored fields with
// data-field-error="true" — add that attribute there if not present yet.
export const scrollToFirstError = () => {
  requestAnimationFrame(() => {
    const el = document.querySelector('[data-field-error="true"]');
    if (el) {
      el.scrollIntoView({ behavior: "smooth", block: "center" });
    }
  });
};