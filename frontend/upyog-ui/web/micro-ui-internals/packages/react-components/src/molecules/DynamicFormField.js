import React, { useMemo } from "react";
import {
  CardLabel,
  Dropdown,
  TextInput,
  DatePicker,
  UploadFile,
} from "@nudmcdgnpm/digit-ui-react-components";
import { toInputDate, resolveFieldLabelKey, getFieldWatchNames } from "../utilities/formUtils";

/* ── shared sub-renderers (were copy-pasted 5x before) ─────────────────── */

const FieldLabel = ({ text, required, hasError, unit }) => (
  <CardLabel style={{ color: hasError ? "red" : undefined }}>
    {text}
    {unit && <span className="field-unit"> {unit}</span>}
    {required && <span style={{ color: "red" }}> *</span>}
  </CardLabel>
);

const FieldError = ({ show, message }) =>
  show ? (
    <p className="field-error" style={{ color: "red", fontSize: "12px", marginTop: "4px" }}>
      {message}
    </p>
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
  // (The locality list can be 1000+ items; mapping + t() on each render of
  // each field was the main typing-lag source.)
  // useDynamicMDMS keys dropdownData by field.name ("assetType"), with
  // fieldConfig.key ("EST_ASSET_TYPE") kept as a legacy fallback.
  const options = useMemo(() => {
    if (type !== "dropdown" && type !== "radio") return EMPTY_OPTIONS;

    const fromMdms = dropdownData[name] || dropdownData[fieldConfig.key];
    if (Array.isArray(fromMdms) && fromMdms.length > 0) {
      // If an option's i18nKey has no translation yet (t returns the raw
      // key), fall back to its plain name: "Land", not "EST_ASSET_TYPE_LAND".
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

  // Compile the sanitize regex once, not on every keystroke.
  const sanitizeRegex = useMemo(
    () =>
      validation.regex
        ? new RegExp(validation.regex.pattern, validation.regex.flags || "")
        : null,
    [validation.regex]
  );

  // ── SECTION HEADER: plain heading, no field/value ─────────────────────
  if (fieldConfig.type === "sectionHeader") {
    return (
      <h2 className="dynamic-form-section-header" style={{ marginTop: "20px", marginBottom: "16px", fontSize: "20px", fontWeight: "bold", color: "#333" }}>
        {t(fieldConfig.label?.code || fieldConfig.key)}
      </h2>
    );
  }

  // ── GROUP: render children side by side ──────────────────────────────
  if (fieldConfig.type === "group") {
    return (
      <div className="dynamic-form-group">
        <FieldLabel
          text={t(fieldConfig.label?.code || fieldConfig.key)}
          unit={fieldConfig.label?.unit}
        />
        <div style={{ display: "flex", gap: "16px", flexWrap: "wrap" }}>
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

  // ── DROPDOWN ─────────────────────────────────────────────────────────
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
            select={(val) => onChange(name, val)}
            t={tSafe}
            disable={isFieldDisabled}
          />
          <FieldError show={hasError} message={errorMsg} />
        </div>
      </>
    );
  }

  // ── RADIO ────────────────────────────────────────────────────────────
  if (type === "radio") {
    const radioDisabled = isDisabled || validation.disabled;
    return (
      <>
        <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} />
        <div className="field" data-field-error={hasError ? "true" : undefined} style={{ display: "flex", gap: "20px", marginBottom: "16px" }}>
          {options.map((opt) => (
            <label key={opt.code} style={{ display: "flex", alignItems: "center", cursor: radioDisabled ? "default" : "pointer" }}>
              <input
                type="radio"
                name={name}
                value={opt.code}
                checked={value === opt.code}
                disabled={radioDisabled}
                onChange={() => onChange(name, opt.code)}
                style={{ marginRight: "8px" }}
              />
              {t(opt.i18nKey || opt.label || opt.name || opt.code)}
            </label>
          ))}
        </div>
        <FieldError show={hasError} message={errorMsg} />
      </>
    );
  }

  // ── DATE ─────────────────────────────────────────────────────────────
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

  // ── FILE ─────────────────────────────────────────────────────────────
  if (type === "file") {
    return (
      <>
        <FieldLabel text={t(labelKey)} required={validation.required} hasError={hasError} />
        <div className="field" data-field-error={hasError ? "true" : undefined}>
          <UploadFile
            id={name}
            accept={field.accept || ".png,.jpg,.jpeg,.pdf"}
            message={value ? t("CS_ACTION_FILEUPLOADED") : t("CS_ACTION_NO_FILEUPLOADED")}
            onUpload={(e) => onFileUpload && onFileUpload(name, e.target.files[0])}
            onDelete={() => onChange(name, null)}
          />
          <FieldError show={hasError} message={errorMsg} />
        </div>
      </>
    );
  }

  // ── TEXT INPUT (default) — also covers read-only display fields ───────
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
          style={{ borderColor: hasError ? "red" : undefined }}
        />
        <FieldError show={hasError} message={errorMsg} />
      </div>
    </>
  );
};

/* ── memoization ────────────────────────────────────────────────────────
   formData changes identity on EVERY keystroke, which used to re-render
   every field in the form (including 1000+ option locality dropdowns).
   This comparator re-renders a field only when ITS OWN value/error (or its
   group children's) changed, or when shared inputs actually changed.
   Requires onChange/onFileUpload to be stable (useCallback in DynamicForm —
   they are). */

const collectNames = (fc) => getFieldWatchNames(fc);

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
  return collectNames(next.fieldConfig).every(
    (n) => prev.formData[n] === next.formData[n] && prev.errors[n] === next.errors[n]
  );
};

export default React.memo(DynamicFormField, areEqual);
