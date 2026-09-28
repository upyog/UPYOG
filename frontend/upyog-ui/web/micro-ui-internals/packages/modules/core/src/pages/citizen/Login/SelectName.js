import React, { useMemo, useState } from "react";

const SelectName = ({ config, onSelect, t, isDisabled }) => {
  const fields = config?.inputs || [];
  const initialState = useMemo(
    () =>
      fields.reduce((acc, field) => {
        acc[field.name] = "";
        return acc;
      }, {}),
    [fields]
  );
  const [formData, setFormData] = useState(initialState);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = () => {
    if (!formData.name || !formData.dob) return;
    onSelect(formData);
  };

  const canSubmit = !isDisabled && !!formData.name && !!formData.dob;

  return (
    <div className="login-mobile-step">
      <div className="login-form-header">
        <h2>{t(config?.texts?.header || "CS_LOGIN_PROVIDE_NAME_DOB")}</h2>
        <p>{config?.texts?.cardText || "Provide your name and date of birth to continue."}</p>
      </div>

      <div className="login-form-body">
        {fields.map((field) => (
          <div className="login-field-group" key={field.name}>
            <label className="login-label">{t(field.label)}</label>
            <div className="login-input-wrap">
              <input
                type={field.type}
                name={field.name}
                value={formData[field.name] || ""}
                onChange={handleChange}
                max={field.type === "date" ? "9999-12-31" : undefined}
              />
            </div>
          </div>
        ))}
      </div>

      <div className="login-actions">
        <button
          type="button"
          className="login-primary-button"
          disabled={!canSubmit}
          onClick={handleSubmit}
        >
          {t(config?.texts?.submitBarLabel || "CS_COMMONS_NEXT")}
        </button>
      </div>

      <p className="login-security">
        <span className="login-security-icon" aria-hidden="true">
          <img src={"/images/secure.svg"} alt="secure" />
        </span>
        {"Your information is safe and secure with us."}
      </p>
    </div>
  );
};

export default SelectName;
