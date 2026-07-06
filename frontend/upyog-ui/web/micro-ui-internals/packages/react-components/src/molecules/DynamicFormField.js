import React from "react";
import {
  CardLabel,
  Dropdown,
  TextInput,
  DatePicker,
  UploadFile,
} from "@nudmcdgnpm/digit-ui-react-components";

const DynamicFormField = ({
  fieldConfig,
  formData,
  onChange,
  errors,
  dropdownData = {},
  t,
  isDisabled = false,
  onFileUpload, // (fieldName, file) => void — provided by DynamicForm, handles the actual filestore upload
}) => {
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
        <CardLabel>
          {t(fieldConfig.label?.code || fieldConfig.key)}
          {fieldConfig.label?.unit && (
            <span className="field-unit"> {fieldConfig.label.unit}</span>
          )}
        </CardLabel>
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

  const { field, validation = {}, messages = {} } = fieldConfig;
  if (!field) return null;

  const { name, type, placeholder, unit } = field;
  const value = formData[name];
  const hasError = errors[name];

  // ── DROPDOWN ─────────────────────────────────────────────────────────
  if (type === "dropdown") {
    const options =
      dropdownData[fieldConfig.key] ||
      (fieldConfig.options || []).map((o) => ({
        code: o.code || o.value,
        name: o.value || o.code,
        value: o.i18nKey || o.localname || o.value || o.code,
        i18nKey: o.i18nKey || o.localname || o.code,
      }));

    const isFieldDisabled = isDisabled || fieldConfig.key === "EST_CITY";

    const tSafe = (key) => {
      if (!key) return "";
      const translated = t(key);
      return translated || key;
    };

    return (
      <>
        <CardLabel style={{ color: hasError ? "red" : undefined }}>
          {t(fieldConfig.key)}
          {validation.required && <span style={{ color: "red" }}> *</span>}
        </CardLabel>
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
          {hasError && (
            <p className="field-error" style={{ color: "red", fontSize: "12px", marginTop: "4px" }}>
              {t(messages.error || "FIELD_REQUIRED")}
            </p>
          )}
        </div>
      </>
    );
  }

  // ── RADIO ────────────────────────────────────────────────────────────
  // Options resolved the same way as dropdown: MDMS-backed dropdownData first,
  // falling back to static options declared inline in the field config.
  if (type === "radio") {
    const options =
      dropdownData[fieldConfig.key] ||
      (fieldConfig.options || []).map((o) => ({
        code: o.code,
        label: o.label || o.i18nKey || o.code,
        i18nKey: o.i18nKey || o.label || o.code,
      }));

    return (
      <>
        <CardLabel style={{ color: hasError ? "red" : undefined }}>
          {t(fieldConfig.key)}
          {validation.required && <span style={{ color: "red" }}> *</span>}
        </CardLabel>
        <div className="field" data-field-error={hasError ? "true" : undefined} style={{ display: "flex", gap: "20px", marginBottom: "16px" }}>
          {options.map((opt) => (
            <label key={opt.code} style={{ display: "flex", alignItems: "center", cursor: isDisabled || validation.disabled ? "default" : "pointer" }}>
              <input
                type="radio"
                name={name}
                value={opt.code}
                checked={value === opt.code}
                disabled={isDisabled || validation.disabled}
                onChange={() => onChange(name, opt.code)}
                style={{ marginRight: "8px" }}
              />
              {t(opt.i18nKey)}
            </label>
          ))}
        </div>
        {hasError && (
          <p className="field-error" style={{ color: "red", fontSize: "12px", marginTop: "4px" }}>
            {t(messages.error || "FIELD_REQUIRED")}
          </p>
        )}
      </>
    );
  }

  // ── DATE ─────────────────────────────────────────────────────────────
  if (type === "date") {
    // Native <input type="date"> (inside DIGIT's DatePicker) requires a
    // "yyyy-MM-dd" STRING — passing a Date object stringifies to
    // "Thu Jul 09 2026 05:30:00 GMT+0530..." and the browser rejects it.
    // formData may hold: a yyyy-MM-dd string (live edits), an epoch number
    // (prefill from a saved record), or a Date. Normalize all three.
    const toInputDate = (v) => {
      if (!v) return "";
      if (typeof v === "string" && /^\d{4}-\d{2}-\d{2}$/.test(v)) return v;
      const d = v instanceof Date ? v : new Date(v);
      if (isNaN(d.getTime())) return "";
      // Build from LOCAL date parts — toISOString() shifts IST-midnight
      // epochs back a day (UTC), giving off-by-one dates.
      const pad = (n) => String(n).padStart(2, "0");
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
    };

    const dateValue = toInputDate(value);

    return (
      <>
        <CardLabel style={{ color: hasError ? "red" : undefined }}>
          {t(fieldConfig.key)}
          {validation.required && <span style={{ color: "red" }}> *</span>}
        </CardLabel>
        <div className="field" data-field-error={hasError ? "true" : undefined}>
          <DatePicker
            date={dateValue}
            disable={isDisabled || validation.disabled}
            onChange={(d) => onChange(name, d)}
          />
          {hasError && (
            <p className="field-error" style={{ color: "red", fontSize: "12px", marginTop: "4px" }}>
              {t(messages.error || "FIELD_REQUIRED")}
            </p>
          )}
        </div>
      </>
    );
  }

  // ── FILE ─────────────────────────────────────────────────────────────
  // Upload itself (filestore call, size validation, toast on error) is owned by
  // DynamicForm via onFileUpload, since that's where tenantId/error-toast state live.
  // This component only reports the raw File object up and renders upload state.
  if (type === "file") {
    return (
      <>
        <CardLabel style={{ color: hasError ? "red" : undefined }}>
          {t(fieldConfig.key)}
          {validation.required && <span style={{ color: "red" }}> *</span>}
        </CardLabel>
        <div className="field" data-field-error={hasError ? "true" : undefined}>
          <UploadFile
            id={name}
            accept={field.accept || ".png,.jpg,.jpeg,.pdf"}
            message={value ? t("CS_ACTION_FILEUPLOADED") : t("CS_ACTION_NO_FILEUPLOADED")}
            onUpload={(e) => onFileUpload && onFileUpload(name, e.target.files[0])}
            onDelete={() => onChange(name, null)}
          />
          {hasError && (
            <p className="field-error" style={{ color: "red", fontSize: "12px", marginTop: "4px" }}>
              {t(messages.error || "FIELD_REQUIRED")}
            </p>
          )}
        </div>
      </>
    );
  }

  // ── TEXT INPUT (default) ───────────────────────────────────────────────
  // Also covers read-only "display" fields (e.g. prefilled asset info) via
  // validation.disabled/readOnly — no separate "label" type needed.
  return (
    <>
      <CardLabel style={{ color: hasError ? "red" : undefined }}>
        {t(fieldConfig.key)}
        {unit && <span className="field-unit"> {unit}</span>}
        {validation.required && <span style={{ color: "red" }}> *</span>}
      </CardLabel>
      <div className="field" data-field-error={hasError ? "true" : undefined}>
        <TextInput
          placeholder={t(placeholder || "")}
          value={value || ""}
          onChange={(e) => {
            let val = e.target.value;
            if (validation.regex) {
              val = val.replace(
                new RegExp(validation.regex.pattern, validation.regex.flags || ""),
                ""
              );
            }
            if (validation.maxLength) {
              val = val.slice(0, validation.maxLength);
            }
            onChange(name, val);
          }}
          disabled={isDisabled || validation.disabled}
          readOnly={validation.readOnly}
          style={{ borderColor: hasError ? "red" : undefined }}
        />
        {hasError && (
          <p className="field-error" style={{ color: "red", fontSize: "12px", marginTop: "4px" }}>
            {t(messages.error || "FIELD_REQUIRED")}
          </p>
        )}
      </div>
    </>
  );
};

export default DynamicFormField;
