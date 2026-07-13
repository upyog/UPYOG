import React, { useMemo } from "react";
import {
  CardLabel,
  Dropdown,
  TextInput,
  DatePicker,
  UploadFile,
} from "@nudmcdgnpm/digit-ui-react-components";
import { toInputDate, resolveFieldLabelKey, getFieldWatchNames, optionCode, enrichDropdownSelection } from "../utilities/formUtils";
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

const EMPTY_OPTIONS = [];

const DynamicFormField = ({
  fieldConfig,
  formData,
  onChange,
  errors,
  dropdownData = {},
  t,
  isDisabled = false,
  onFileUpload, // (fieldName, file) => void — DynamicForm owns the filestore upload
}) => {
  // Hooks must run unconditionally, so they sit ABOVE the type early-returns.
  // For sectionHeader/group nodes `field` is undefined — the memos no-op.
  const { field, validation = {}, messages = {} } = fieldConfig;
  const { name, type, placeholder, unit } = field || {};

  // Options resolved ONCE per dropdownData change — not on every keystroke.
  // useDynamicMDMS keys dropdownData by field.name ("assetType"), with
  // fieldConfig.key ("EST_ASSET_TYPE") kept as a legacy fallback.
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
            />
          ))}
        </div>
      </div>
    );
  }

  if (!field) return null;

  const value = formData[name];
  const hasError = errors[name];
  const errorMsg = t(messages.error || "FIELD_REQUIRED");
  const labelKey = resolveFieldLabelKey(fieldConfig, formData);

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

  return (
    <>
      <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} unit={unit} />
      <div className="field" data-field-error={hasError ? "true" : undefined}>
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
    prev.onFileUpload !== next.onFileUpload
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
