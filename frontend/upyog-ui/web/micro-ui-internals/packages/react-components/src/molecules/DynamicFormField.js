import React, { useMemo } from "react";
import {
  CardLabel,
  Dropdown,
  TextInput,
  DatePicker,
  UploadFile,
} from "@nudmcdgnpm/digit-ui-react-components";
import { toInputDate, resolveFieldLabelKey, getFieldWatchNames, optionCode, enrichDropdownSelection, isFieldVisible } from "../utilities/formUtils";
import { formatDurationDisplay } from "../utilities/validators";
import styles from "../styles/DynamicFormField.module.scss";

/* ── shared sub-renderers ─────────────────────────────────────────────── */

const FieldLabel = ({ text, required, hasError, unit }) => (
  <CardLabel className={hasError ? styles["dynamic-form-field__label--error"] : undefined}>
    {text}
    {unit && <span className={styles["dynamic-form-field__unit"]}> {unit}</span>}
    {required && (
      <span className={`astericColor ${styles["dynamic-form-field__required"] || ""}`}> *</span>
    )}
  </CardLabel>
);

const FieldError = ({ show, message }) =>
  show ? (
    <p className={styles["dynamic-form-field__error"]}>{message}</p>
  ) : null;

const SearchIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
    <circle cx="11" cy="11" r="7" stroke="currentColor" strokeWidth="2" />
    <path d="M20 20l-3.5-3.5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
  </svg>
);

const EMPTY_OPTIONS = [];

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

  const sanitizeRegex = useMemo(
    () =>
      validation.regex
        ? new RegExp(validation.regex.pattern, validation.regex.flags || "")
        : null,
    [validation.regex]
  );

  if (fieldConfig.type === "sectionHeader") {
    return (
      <h2 className={styles["dynamic-form-field__section-header"]}>
        {t(fieldConfig.label?.code || fieldConfig.key)}
      </h2>
    );
  }

  if (fieldConfig.type === "group") {
    if (!isFieldVisible(fieldConfig, formData)) return null;
    return (
      <div className={styles["dynamic-form-field__group"]}>
        <FieldLabel
          text={t(fieldConfig.label?.code || fieldConfig.key)}
          unit={fieldConfig.label?.unit}
        />
        <div className={styles["dynamic-form-field__group-row"]}>
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

  if (!field) return null;
  if (!isFieldVisible(fieldConfig, formData)) return null;

  const value = formData[name];
  const hasError = errors[name];
  const errorMsg = t(messages.error || "FIELD_REQUIRED");
  const labelKey = resolveFieldLabelKey(fieldConfig, formData);
  const isDurationField = field.computeFn === "calculateDuration";
  const durationMonths = isDurationField ? Number(value) : NaN;
  const showDurationAsYears = isDurationField && Number.isFinite(durationMonths) && durationMonths > 12;
  const textDisplayValue = isDurationField ? formatDurationDisplay(value) : value;
  const textUnit = showDurationAsYears ? undefined : unit;
  const useSearchCard = Boolean(field.searchCard || field.searchButton);

  if (type === "dropdown") {
    const isFieldDisabled = isDisabled || fieldConfig.key === "EST_CITY";
    const tSafe = (key) => (key ? t(key) || key : "");

    return (
      <>
        <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} />
        <div className="field" data-field-error={hasError ? "true" : undefined}>
          <Dropdown
            placeholder={tSafe(placeholder || "")}
            selected={value || null}
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

  if (type === "radio") {
    const radioDisabled = isDisabled || validation.disabled;
    return (
      <>
        <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} />
        <div
          className={`field ${styles["dynamic-form-field__radio-group"]}`}
          data-field-error={hasError ? "true" : undefined}
        >
          {options.map((opt) => (
            <label
              key={opt.code}
              className={`${styles["dynamic-form-field__radio-label"]}${
                radioDisabled ? ` ${styles["dynamic-form-field__radio-label--disabled"]}` : ""
              }`}
            >
              <input
                type="radio"
                name={name}
                value={opt.code}
                checked={value === opt.code}
                disabled={radioDisabled}
                onChange={() => onChange(name, opt.code)}
                className={styles["dynamic-form-field__radio-input"]}
              />
              {t(opt.i18nKey || opt.label || opt.name || opt.code)}
            </label>
          ))}
        </div>
        <FieldError show={hasError} message={errorMsg} />
      </>
    );
  }

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

  if (useSearchCard) {
    const panelForField = searchPanel?.fieldName === name ? searchPanel : null;
    return (
      <>
        <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} unit={textUnit} />
        <div className="field" data-field-error={hasError ? "true" : undefined}>
          <div className={styles["dynamic-form-field__lookup"]}>
            <TextInput
              placeholder={t(placeholder || "")}
              value={value || ""}
              onChange={(e) => {
                let val = e.target.value;
                if (sanitizeRegex) val = val.replace(sanitizeRegex, "");
                if (validation.maxLength) val = val.slice(0, validation.maxLength);
                onChange(name, val);
              }}
              disabled={isDisabled || validation.disabled}
              readOnly={validation.readOnly}
              errorStyle={hasError}
            />
            <button
              type="button"
              className={styles["dynamic-form-field__lookup-icon"]}
              disabled={isDisabled || validation.disabled || isFieldSearching || !String(value || "").trim()}
              onClick={() => onFieldSearch?.(name)}
              aria-label={t("ES_COMMON_SEARCH")}
            >
              <SearchIcon />
            </button>
          </div>
          <FieldError show={hasError} message={errorMsg} />

          {panelForField?.status === "matches" && Array.isArray(panelForField.matches) && (
            <div className={styles["dynamic-form-field__suggest-box"]}>
              {panelForField.matches.map((match) => (
                <button
                  key={match.estateNo}
                  type="button"
                  className={styles["dynamic-form-field__suggest-item"]}
                  onClick={() => onSelectSearchResult?.(name, match)}
                >
                  <span className={styles["dynamic-form-field__suggest-no"]}>
                    {match.label || match.estateNo}
                  </span>
                  {match.subtitle ? (
                    <span className={styles["dynamic-form-field__suggest-sub"]}>
                      {match.subtitle}
                    </span>
                  ) : null}
                </button>
              ))}
            </div>
          )}

          {panelForField?.status === "found" && (
            <div className={styles["dynamic-form-field__result-card"]}>
              <div className={styles["dynamic-form-field__result-row"]}>
                <span>{t(field.resultLabel || labelKey || "EST_ASSET_NUMBER")}</span>
                <span>{panelForField.estateNo}</span>
              </div>
              <button
                type="button"
                className={styles["dynamic-form-field__select-button"]}
                onClick={() => onSelectSearchResult?.(name)}
              >
                {t(field.selectLabel || "CS_COMMON_SELECT")}
              </button>
            </div>
          )}

          {panelForField?.status === "notFound" && (
            <div className={styles["dynamic-form-field__not-found"]}>
              <p className={styles["dynamic-form-field__not-found-text"]}>
                {t(field.notFoundLabel || "EST_ASSET_NOT_FOUND")}
              </p>
              <button
                type="button"
                className={styles["dynamic-form-field__create-button"]}
                onClick={() => onCreateNewFromSearch?.(name)}
              >
                {t(field.createNewLabel || "EST_CREATE_NEW_REGISTRATION")}
              </button>
            </div>
          )}
        </div>
      </>
    );
  }

  return (
    <>
      <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} unit={textUnit} />
      <div className="field" data-field-error={hasError ? "true" : undefined}>
        <TextInput
          placeholder={t(placeholder || "")}
          value={textDisplayValue || ""}
          onChange={(e) => {
            let val = e.target.value;
            if (sanitizeRegex) val = val.replace(sanitizeRegex, "");
            if (validation.maxLength) val = val.slice(0, validation.maxLength);
            onChange(name, val);
          }}
          disabled={isDisabled || validation.disabled}
          readOnly={validation.readOnly}
          errorStyle={hasError}
        />
        <FieldError show={hasError} message={errorMsg} />
      </div>
    </>
  );
};

const collectNames = (fc) => getFieldWatchNames(fc);

const watchValueEqual = (a, b) => {
  if (a === b) return true;
  if (a && b && typeof a === "object" && typeof b === "object") {
    if (optionCode(a) !== optionCode(b)) return false;
    const metaKeys = ["multiplier", "rentMultiplier", "cycleMultiplier", "rentLabelKey"];
    return metaKeys.every((key) => (a[key] ?? null) === (b[key] ?? null));
  }
  return false;
};

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
