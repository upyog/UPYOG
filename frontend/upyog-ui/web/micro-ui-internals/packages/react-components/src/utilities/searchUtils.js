// searchUtils.js
// Shared helpers for config-driven employee search pages.

import { flattenFormConfig } from "./formUtils";

export const DEFAULT_SEARCH_PAGINATION = {
  offset: 0,
  limit: 10,
  sortBy: "createdDate",
  sortOrder: "DESC",
};

/** Client-side slice for APIs that return the full result set. */
export const paginateArray = (list = [], offset = 0, limit = 10) => {
  if (!Array.isArray(list)) return [];
  return list.slice(offset, offset + limit);
};

/**
 * Normalize MDMS / locality rows into Dropdown options.
 *   { code, name } → { code, name, i18nKey, label }
 */
export const toSearchDropdownOptions = (list = [], mapItem) =>
  (Array.isArray(list) ? list : [])
    .map((item) => {
      if (typeof mapItem === "function") return mapItem(item);
      const code = item?.code;
      if (!code) return null;
      const name = item.name || item.label || item.i18nKey || code;
      return {
        ...item,
        code,
        name,
        i18nKey: item.i18nKey || name,
        label: item.label || name,
      };
    })
    .filter(Boolean);

/**
 * Build the search API payload from RHF form values + dropdown selections.
 *
 * Field config shape (dropdown):
 *   { name, type: "dropdown", submitKey: "localityCode" }
 * Text fields pass through via formData[name].
 */
export const buildSearchPayload = (formData = {}, dropdownValues = {}, fields = []) => {
  const payload = { ...formData };

  fields.forEach((field) => {
    if (field.type !== "dropdown") return;
    const selected = dropdownValues[field.name];
    const submitKey = field.submitKey || field.name;
    const code = selected?.code;
    if (code !== undefined && code !== null && code !== "") {
      payload[submitKey] = code;
    } else {
      delete payload[submitKey];
    }
    // Avoid leaking UI-only selection objects into the API payload.
    if (submitKey !== field.name) delete payload[field.name];
  });

  return payload;
};

/** Map DynamicForm flat payload → search API filters using apiFieldName/submitKey. */
export const mapFormToSearchFilters = (flat = {}, formConfig = []) => {
  const result = {};
  flattenFormConfig(formConfig).forEach((fc) => {
    const name = fc?.field?.name;
    if (!name) return;
    const key = fc.apiFieldName || fc.submitKey || name;
    const val = flat[name];
    if (val === undefined || val === null || val === "") return;
    result[key] = val;
  });
  return result;
};
