import React, { useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const SelectName = ({ config, onSelect, t, isDisabled }) => {
  const navigate = useNavigate();
  const location = useLocation();
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

  const handleBack = () => {
    navigate("/upyog-ui/citizen/login", { state: location.state });
  };

  return (
    <div className="login-mobile-step">
      <button type="button" className="login-back-btn" onClick={handleBack}>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
          <line x1="19" y1="12" x2="5" y2="12" />
          <polyline points="12 19 5 12 12 5" />
        </svg>
        <span>{t("CS_COMMON_BACK")}</span>
      </button>
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
          <img src={"/upyog-ui/images/secure.svg"} alt="secure" />
        </span>
        {"Your information is safe and secure with us."}
      </p>
    </div>
  );
};

export default SelectName;
