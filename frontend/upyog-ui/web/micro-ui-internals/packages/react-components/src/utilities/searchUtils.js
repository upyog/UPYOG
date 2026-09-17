/**
 * searchUtils.js
 *
 * Shared helpers for config-driven employee search pages that reuse
 * DynamicForm (mode="search") or standalone search field configs.
 *
 * Exports
 * -------
 * - DEFAULT_SEARCH_PAGINATION — default offset/limit/sort for search forms
 * - paginateArray             — client-side slice of a full result list
 * - toSearchDropdownOptions   — normalize MDMS / locality rows for Dropdown
 * - buildSearchPayload        — RHF form + dropdown selections → API payload
 * - mapFormToSearchFilters    — DynamicForm flat payload → filter map
 *
 * @see DynamicForm (mode="search")
 * @see useClientPagination
 */

import { flattenFormConfig } from "./formUtils";

/**
 * Default pagination / sort values for employee search forms (RHF defaults).
 * @type {{ offset: number, limit: number, sortBy: string, sortOrder: string }}
 */
export const DEFAULT_SEARCH_PAGINATION = {
  offset: 0,
  limit: 10,
  sortBy: "createdDate",
  sortOrder: "DESC",
};

/**
 * Client-side slice for APIs that return the full result set.
 *
 * @param {array}  [list]   Full result array.
 * @param {number} [offset] Start index.
 * @param {number} [limit]  Page size.
 * @returns {array}
 */
export const paginateArray = (list = [], offset = 0, limit = 10) => {
  if (!Array.isArray(list)) return [];
  return list.slice(offset, offset + limit);
};

/**
 * Normalize MDMS / locality rows into Dropdown options.
 * Default map: { code, name } → { code, name, i18nKey, label }.
 * Pass mapItem to customize per module.
 *
 * @param {array}    [list]    Raw master / locality rows.
 * @param {Function} [mapItem] Optional (item) => option | null.
 * @returns {object[]}
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
 *
 * @param {object} [formData]        RHF (and other) field values.
 * @param {object} [dropdownValues]  Map of field.name → selected option object.
 * @param {array}  [fields]          Field descriptors with type / submitKey.
 * @returns {object} API-ready filter payload.
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

/**
 * Normalize dropdown objects / plain values to an API filter code string.
 *
 * @param {*} val - Option object, string, or empty.
 * @returns {string}
 */
const toFilterCode = (val) => {
  if (val === undefined || val === null || val === "") return "";
  if (typeof val === "object") {
    return String(val.code ?? val.value ?? "").trim();
  }
  return String(val).trim();
};

/**
 * Map DynamicForm flat payload → search API filters using apiFieldName/submitKey.
 * Used by DynamicForm goNext when mode === "search".
 *
 * @param {object} [flat]       Flattened form values from buildPayload.
 * @param {array}  [formConfig] routeConfig.form (groups supported via flatten).
 * @returns {object} Filter map keyed by apiFieldName | submitKey | field.name.
 */
export const mapFormToSearchFilters = (flat = {}, formConfig = []) => {
  const result = {};
  flattenFormConfig(formConfig).forEach((fc) => {
    const name = fc?.field?.name;
    if (!name) return;
    const key = fc.apiFieldName || fc.submitKey || name;
    const code = toFilterCode(flat[name]);
    if (!code) return;
    result[key] = code;
  });
  return result;
};
