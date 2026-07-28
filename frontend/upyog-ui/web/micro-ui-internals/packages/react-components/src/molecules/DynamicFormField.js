/**
 * DynamicFormField.js
 *
 * Single-field (or group / section) renderer used by DynamicForm.
 * Takes one MDMS / routeConfig field definition and renders the matching
 * UI control — text, date, dropdown, radio, file upload, lookup/search card,
 * section header, or nested group — with visibility, labels, and options
 * driven by that config.
 *
 * Responsibilities
 * ----------------
 * 1. Honor fieldConfig.type:
 *    - "sectionHeader" → heading only
 *    - "group"         → label + recursive children in a row (if visible)
 *    - otherwise       → leaf control for field.type
 * 2. Hide leaf / group nodes when isFieldVisible(fieldConfig, formData) is false
 *    (visibleWhen / related visibility rules).
 * 3. Resolve display label via resolveFieldLabelKey (supports dynamic labels).
 * 4. Resolve dropdown / radio options from useDynamicMDMS dropdownData (by
 *    field name or fieldConfig.key), falling back to fieldConfig.options.
 * 5. Apply validation.regex sanitization and maxLength on text input change.
 * 6. Special display for computeFn === "calculateDuration" (years vs months
 *    via formatDurationDisplay).
 * 7. Lookup / search UI when field.searchCard or field.searchButton is set:
 *    text + search button, suggestion list, found card, or not-found / create-new.
 * 8. Memoized with a custom areEqual that only re-renders when watched form
 *    values (getFieldWatchNames), errors, or stable props change.
 *
 * Supported leaf field.type values
 * --------------------------------
 * - dropdown  — Digit Dropdown; selection enriched via enrichDropdownSelection
 * - radio     — native radio group from options
 * - date      — DatePicker (value normalized with toInputDate)
 * - file      — UploadFile; upload via onFileUpload, clear via onChange(null)
 * - text (default) — TextInput; optional search-card variant when searchButton/searchCard
 *
 * Typical MDMS field shape (leaf)
 * -------------------------------
 *   {
 *     key: "EST_BUILDING_NAME",
 *     field: { name: "buildingName", type: "text", placeholder: "..." },
 *     validation: { required: true, maxLength: 100, regex: { pattern, flags } },
 *     messages: { error: "FIELD_REQUIRED" },
 *     label: { code: "EST_BUILDING_NAME" },
 *     visibleWhen: { field: "showRegistrationDetails", value: "YES" },
 *   }
 *
 * Props
 * -----
 * @param {object}   fieldConfig              One form entry from routeConfig.form
 *                                            (or a group child / sectionHeader).
 * @param {object}   formData                 Current DynamicForm state (keyed by field.name).
 * @param {Function} onChange                 (fieldName, value, resetFields?) => void.
 * @param {object}   errors                   Map of fieldName → truthy when invalid.
 * @param {object}   [dropdownData]           MDMS / master options keyed by field name or key.
 * @param {Function} t                        i18n translator.
 * @param {boolean}  [isDisabled=false]       Disables the control (and radio / search actions).
 * @param {Function} [onFileUpload]           (fieldName, file) => void for type === "file".
 * @param {Function} [onFieldSearch]          (fieldName) => void; Enter / search-button trigger.
 * @param {boolean}  [isFieldSearching]       Disables search button while lookup is in flight.
 * @param {object}   [searchPanel]            { fieldName, status, estateNo?, matches?, prefill? }
 *                                            status: "matches" | "found" | "notFound".
 * @param {Function} [onSelectSearchResult]   (fieldName, match?) => void when user picks a hit.
 * @param {Function} [onCreateNewFromSearch]  (fieldName) => void from not-found "create new".
 *
 * @see DynamicForm
 * @see isFieldVisible
 * @see resolveFieldLabelKey
 * @see getFieldWatchNames
 */

import React, { useMemo } from "react";
import {
  CardLabel,
  Dropdown,
  TextInput,
  DatePicker,
  UploadFile,
} from "@nudmcdgnpm/digit-ui-react-components";
import {
  toInputDate,
  resolveFieldLabelKey,
  getFieldWatchNames,
  optionCode,
  enrichDropdownSelection,
  isFieldVisible,
} from "../utilities/formUtils";
import { formatDurationDisplay } from "../utilities/validators";

/* ── shared sub-renderers ─────────────────────────────────────────────── */

/**
 * Field label row: translated text, optional unit, required asterisk, error styling.
 *
 * @param {object}  props
 * @param {string}  props.text      Already-translated label text.
 * @param {boolean} [props.required] Show required asterisk when true.
 * @param {boolean} [props.hasError] Applies error label class when true.
 * @param {string}  [props.unit]    Optional unit suffix (e.g. "sq.ft").
 * @returns {JSX.Element}
 */
const FieldLabel = ({ text, required, hasError, unit }) => (
  <CardLabel className={hasError ? "dynamic-form-field__label--error" : undefined}>
    {text}
    {unit && <span className="dynamic-form-field__unit"> {unit}</span>}
    {required && (
      <span className="astericColor dynamic-form-field__required"> *</span>
    )}
  </CardLabel>
);

/**
 * Inline validation message under a control; renders nothing when `show` is falsy.
 *
 * @param {object}  props
 * @param {boolean} props.show    Whether to render the error.
 * @param {string}  props.message Already-translated error text.
 * @returns {JSX.Element|null}
 */
const FieldError = ({ show, message }) =>
  show ? (
    <p className="dynamic-form-field__error">{message}</p>
  ) : null;

/**
 * Decorative magnifying-glass SVG for the lookup / search-card button.
 * @returns {JSX.Element}
 */
const SearchIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
    <circle cx="11" cy="11" r="7" stroke="currentColor" strokeWidth="2" />
    <path d="M20 20l-3.5-3.5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
  </svg>
);

/** Stable empty array so dropdown/radio fields without options don't allocate each render. */
const EMPTY_OPTIONS = [];

/**
 * Sanitizes a text input value using optional regex strip + maxLength, then
 * calls onChange(fieldName, value). Shared by plain text and search-card inputs.
 *
 * @param {Event}    e             Input change event.
 * @param {object}   opts
 * @param {string}   opts.name     field.name to update.
 * @param {RegExp|null} opts.sanitizeRegex  Pattern whose matches are removed.
 * @param {object}   opts.validation        Field validation (maxLength, etc.).
 * @param {Function} opts.onChange          DynamicForm handleChange.
 */
const applyTextInputChange = (e, { name, sanitizeRegex, validation, onChange }) => {
  let val = e.target.value;
  if (sanitizeRegex) val = val.replace(sanitizeRegex, "");
  if (validation.maxLength) val = val.slice(0, validation.maxLength);
  onChange(name, val);
};

/**
 * Single-field (or group / sectionHeader) renderer for DynamicForm.
 * Branches on fieldConfig.type / field.type and returns the matching control.
 *
 * @param {object} props — see file-level Props section above.
 * @returns {JSX.Element|null}
 */
const DynamicFormField = ({
  fieldConfig,
  formData,
  onChange,
  errors,
  dropdownData = {},
  t,
  isDisabled = false,
  onFileUpload,
  onFieldSearch,
  isFieldSearching = false,
  searchPanel = null,
  onSelectSearchResult,
  onCreateNewFromSearch,
}) => {
  const { field, validation = {}, messages = {} } = fieldConfig;
  const { name, type, placeholder, unit } = field || {};

  /**
   * Dropdown / radio option list.
   * Prefers MDMS dropdownData[name|key]; falls back to static fieldConfig.options.
   * When an MDMS i18nKey does not translate, substitutes name/code so the UI
   * still shows a readable label.
   *
   * @returns {object[]}
   */
  const options = useMemo(() => {
    if (type !== "dropdown" && type !== "radio") return EMPTY_OPTIONS;

    const fromMdms = dropdownData[name] || dropdownData[fieldConfig.key];
    if (Array.isArray(fromMdms) && fromMdms.length > 0) {
      return fromMdms.map((o) => {
        const translated = o.i18nKey ? t(o.i18nKey) : "";
        const untranslated = !translated || translated === o.i18nKey;
        return untranslated ? { ...o, i18nKey: o.name || o.code } : o;
      });
    }
    return (fieldConfig.options || []).map((o) => ({
      code: o.code || o.value,
      name: o.value || o.code,
      value: o.i18nKey || o.localname || o.value || o.code,
      i18nKey: o.i18nKey || o.localname || o.value || o.code,
    }));
  }, [type, name, fieldConfig, dropdownData, t]);

  /**
   * Compiled validation.regex for stripping illegal characters on text input.
   * Null when the field has no regex rule.
   *
   * @returns {RegExp|null}
   */
  const sanitizeRegex = useMemo(
    () =>
      validation.regex
        ? new RegExp(validation.regex.pattern, validation.regex.flags || "")
        : null,
    [validation.regex]
  );

  // ── Non-leaf: section header ─────────────────────────────────────────
  if (fieldConfig.type === "sectionHeader") {
    return (
      <h2 className="dynamic-form-field__section-header">
        {t(fieldConfig.label?.code || fieldConfig.key)}
      </h2>
    );
  }

  // ── Non-leaf: group of children in a row (respects visibleWhen) ──────
  if (fieldConfig.type === "group") {
    if (!isFieldVisible(fieldConfig, formData)) return null;
    return (
      <div className="dynamic-form-field__group">
        <FieldLabel
          text={t(fieldConfig.label?.code || fieldConfig.key)}
          unit={fieldConfig.label?.unit}
        />
        <div className="dynamic-form-field__group-row">
          {(fieldConfig.children || []).map((child) => (
            <DynamicFormField
              key={child.key}
              fieldConfig={child}
              formData={formData}
              onChange={onChange}
              errors={errors}
              dropdownData={dropdownData}
              t={t}
              isDisabled={isDisabled}
              onFileUpload={onFileUpload}
              onFieldSearch={onFieldSearch}
              isFieldSearching={isFieldSearching}
              searchPanel={searchPanel}
              onSelectSearchResult={onSelectSearchResult}
              onCreateNewFromSearch={onCreateNewFromSearch}
            />
          ))}
        </div>
      </div>
    );
  }

  // ── Leaf field: require field def + visibility ───────────────────────
  if (!field) return null;
  if (!isFieldVisible(fieldConfig, formData)) return null;

  const value = formData[name];
  const hasError = errors[name];
  const errorMsg = t(messages.error || "FIELD_REQUIRED");
  const labelKey = resolveFieldLabelKey(fieldConfig, formData);
  /** Duration compute fields may display as years when months > 12. */
  const isDurationField = field.computeFn === "calculateDuration";
  const durationMonths = isDurationField ? Number(value) : NaN;
  const showDurationAsYears = isDurationField && Number.isFinite(durationMonths) && durationMonths > 12;
  const textDisplayValue = isDurationField ? formatDurationDisplay(value) : value;
  const textUnit = showDurationAsYears ? undefined : unit;
  /** Lookup UI when MDMS marks the field with searchCard or searchButton. */
  const useSearchCard = Boolean(field.searchCard || field.searchButton);

  // ── dropdown ─────────────────────────────────────────────────────────
  if (type === "dropdown") {
    const isFieldDisabled = isDisabled || fieldConfig.key === "EST_CITY";
    /**
     * Safe translator for Digit Dropdown (never returns empty for a key).
     * @param {string} key
     * @returns {string}
     */
    const tSafe = (key) => (key ? t(key) || key : "");
    // Prefer the option object from the current list (stable reference by code).
    // String codes / stale objects otherwise leave Dropdown selectedVal blank.
    const selectedCode = optionCode(value);
    const selected =
      (selectedCode && options.find((o) => optionCode(o) === selectedCode)) ||
      (value && typeof value === "object" ? value : null);

    return (
      <>
        <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} />
        <div className="field" data-field-error={hasError ? "true" : undefined}>
          <Dropdown
            placeholder={tSafe(placeholder || "")}
            selected={selected}
            option={options}
            optionKey="i18nKey"
            select={(val) => onChange(name, enrichDropdownSelection(val, options))}
            t={tSafe}
            disable={isFieldDisabled}
            optionCardStyles={field.optionCardStyles}
          />
          <FieldError show={hasError} message={errorMsg} />
        </div>
      </>
    );
  }

  // ── radio ────────────────────────────────────────────────────────────
  if (type === "radio") {
    const radioDisabled = isDisabled || validation.disabled;
    return (
      <>
        <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} />
        <div
          className="field dynamic-form-field__radio-group"
          data-field-error={hasError ? "true" : undefined}
        >
          {options.map((opt) => (
            <label
              key={opt.code}
              className={`dynamic-form-field__radio-label${
                radioDisabled ? ` dynamic-form-field__radio-label--disabled` : ""
              }`}
            >
              <input
                type="radio"
                name={name}
                value={opt.code}
                checked={value === opt.code}
                disabled={radioDisabled}
                onChange={() => onChange(name, opt.code)}
                className="dynamic-form-field__radio-input"
              />
              {t(opt.i18nKey || opt.label || opt.name || opt.code)}
            </label>
          ))}
        </div>
        <FieldError show={hasError} message={errorMsg} />
      </>
    );
  }

  // ── date ─────────────────────────────────────────────────────────────
  if (type === "date") {
    return (
      <>
        <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} />
        <div className="field" data-field-error={hasError ? "true" : undefined}>
          <DatePicker
            date={toInputDate(value)}
            disable={isDisabled || validation.disabled}
            onChange={(d) => onChange(name, d)}
          />
          <FieldError show={hasError} message={errorMsg} />
        </div>
      </>
    );
  }

  // ── file upload ──────────────────────────────────────────────────────
  if (type === "file") {
    const fileRef =
      typeof value === "string"
        ? value
        : value?.filestoreId || value?.fileStoreId || value?.documentuuid;
    const hasUploaded = Boolean(fileRef);
    const uploadedLabel =
      (typeof value === "object" && value?.fileName) ||
      fileRef ||
      t("CS_ACTION_FILEUPLOADED");

    return (
      <>
        <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} />
        <div className="field" data-field-error={hasError ? "true" : undefined}>
          <UploadFile
            id={name}
            accept={field.accept || ".png,.jpg,.jpeg,.pdf"}
            message={hasUploaded ? t("CS_ACTION_FILEUPLOADED") : t("CS_ACTION_NO_FILEUPLOADED")}
            file={hasUploaded ? { name: uploadedLabel } : undefined}
            onUpload={(e) => onFileUpload && onFileUpload(name, e.target.files[0])}
            onDelete={() => onChange(name, null)}
          />
          <FieldError show={hasError} message={errorMsg} />
        </div>
      </>
    );
  }

  // ── lookup / search card (text + search affordances) ─────────────────
  if (useSearchCard) {
    const panelForField = searchPanel?.fieldName === name ? searchPanel : null;
    return (
      <>
        <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} unit={textUnit} />
        <div className="field" data-field-error={hasError ? "true" : undefined}>
          <div className="dynamic-form-field__lookup">
            <TextInput
              placeholder={t(placeholder || "")}
              value={value || ""}
              onChange={(e) =>
                applyTextInputChange(e, { name, sanitizeRegex, validation, onChange })
              }
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  e.preventDefault();
                  onFieldSearch?.(name);
                }
              }}
              disabled={isDisabled || validation.disabled}
              readOnly={validation.readOnly}
              errorStyle={hasError}
            />
            <button
              type="button"
              className="dynamic-form-field__lookup-icon"
              disabled={isDisabled || validation.disabled || isFieldSearching || !String(value || "").trim()}
              onClick={() => onFieldSearch?.(name)}
              aria-label={t("ES_COMMON_SEARCH")}
            >
              <SearchIcon />
            </button>
          </div>
          <FieldError show={hasError} message={errorMsg} />

          {panelForField?.status === "matches" && Array.isArray(panelForField.matches) && (
            <div className="dynamic-form-field__suggest-box">
              {panelForField.matches.map((match) => (
                <button
                  key={match.estateNo}
                  type="button"
                  className="dynamic-form-field__suggest-item"
                  onClick={() => onSelectSearchResult?.(name, match)}
                >
                  <span className="dynamic-form-field__suggest-no">
                    {match.label || match.estateNo}
                  </span>
                  {match.subtitle ? (
                    <span className="dynamic-form-field__suggest-sub">
                      {match.subtitle}
                    </span>
                  ) : null}
                </button>
              ))}
            </div>
          )}

          {panelForField?.status === "found" && (
            <div className="dynamic-form-field__result-card">
              <div className="dynamic-form-field__result-row">
                <span>{t(field.resultLabel || labelKey || "CS_COMMON_ASSET_NUMBER")}</span>
                <span>{panelForField.estateNo}</span>
              </div>
              <button
                type="button"
                className="dynamic-form-field__select-button"
                onClick={() => onSelectSearchResult?.(name)}
              >
                {t(field.selectLabel || "CS_COMMON_SELECT")}
              </button>
            </div>
          )}

          {panelForField?.status === "notFound" && (
            <div className="dynamic-form-field__not-found">
              <p className="dynamic-form-field__not-found-text">
                {t(field.notFoundLabel || "CS_COMMON_NOT_FOUND")}
              </p>
              <button
                type="button"
                className="dynamic-form-field__create-button"
                onClick={() => onCreateNewFromSearch?.(name)}
              >
                {t(field.createNewLabel || "CS_COMMON_CREATE_NEW")}
              </button>
            </div>
          )}
        </div>
      </>
    );
  }

  // ── default: plain text input ────────────────────────────────────────
  return (
    <>
      <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} unit={textUnit} />
      <div className="field" data-field-error={hasError ? "true" : undefined}>
        <TextInput
          placeholder={t(placeholder || "")}
          value={textDisplayValue || ""}
          onChange={(e) =>
            applyTextInputChange(e, { name, sanitizeRegex, validation, onChange })
          }
          disabled={isDisabled || validation.disabled}
          readOnly={validation.readOnly}
          errorStyle={hasError}
        />
        <FieldError show={hasError} message={errorMsg} />
      </div>
    </>
  );
};

/**
 * Field names this component must watch for memo compares
 * (own value, visibleWhen deps, computeFrom deps, etc.).
 *
 * @param {object} fc - fieldConfig
 * @returns {string[]}
 */
const collectNames = (fc) => getFieldWatchNames(fc);

/**
 * Deep-ish equality for a single watched form value.
 * Primitives use ===; option objects compare code + rent/multiplier metadata
 * so enriched dropdown selections don't spuriously re-render.
 *
 * @param {*} a
 * @param {*} b
 * @returns {boolean}
 */
const watchValueEqual = (a, b) => {
  if (a === b) return true;
  if (a && b && typeof a === "object" && typeof b === "object") {
    if (optionCode(a) !== optionCode(b)) return false;
    const metaKeys = ["multiplier", "rentMultiplier", "cycleMultiplier", "rentLabelKey"];
    return metaKeys.every((key) => (a[key] ?? null) === (b[key] ?? null));
  }
  return false;
};

/**
 * React.memo comparator for DynamicFormField.
 * Re-renders when stable props identity changes, or when any watched formData
 * / errors entry for this fieldConfig differs (via collectNames + watchValueEqual).
 * Ignores unrelated formData keys so sibling field updates don't cascade.
 *
 * @param {object} prev - Previous props.
 * @param {object} next - Next props.
 * @returns {boolean} True when props are equal (skip re-render).
 */
const areEqual = (prev, next) => {
  if (
    prev.fieldConfig !== next.fieldConfig ||
    prev.dropdownData !== next.dropdownData ||
    prev.t !== next.t ||
    prev.isDisabled !== next.isDisabled ||
    prev.onChange !== next.onChange ||
    prev.onFileUpload !== next.onFileUpload ||
    prev.onFieldSearch !== next.onFieldSearch ||
    prev.isFieldSearching !== next.isFieldSearching ||
    prev.searchPanel !== next.searchPanel ||
    prev.onSelectSearchResult !== next.onSelectSearchResult ||
    prev.onCreateNewFromSearch !== next.onCreateNewFromSearch
  ) {
    return false;
  }
  const names = collectNames(next.fieldConfig);
  return names.every(
    (n) =>
      watchValueEqual(prev.formData[n], next.formData[n]) &&
      prev.errors[n] === next.errors[n]
  );
};

export default React.memo(DynamicFormField, areEqual);
