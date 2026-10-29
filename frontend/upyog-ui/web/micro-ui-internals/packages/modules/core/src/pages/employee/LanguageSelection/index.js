import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";

const LanguageSelection = () => {
  const { data: storeData, isLoading } = Digit.Hooks.useStore.getInitData();
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { languages, stateInfo } = storeData || {};
  const selectedLanguage = Digit.StoreData.getCurrentLanguage();
  const [selected, setSelected] = useState(selectedLanguage);

  const handleChangeLanguage = (language) => {
    setSelected(language.value);
    Digit.LocalizationService.changeLanguage(language.value, stateInfo?.code);
  };

  const handleSubmit = (event) => {
    event?.preventDefault?.();
    navigate("/upyog-ui/employee/user/login");
  };

  if (isLoading) return <Loader />;

  return (
    <div className="login-mobile-step">
      <div className="login-form-header">
        <h2>{t("CORE_COMMON_CHOOSE_LANGUAGE") || "Choose Language"}</h2>
        <p>{t("CORE_CHOOSE_LANGUAGE_SUBTITLE") || "Select your preferred language to continue."}</p>
      </div>

      <div className="login-form-body">
        <div className="login-field-group">
          <label className="login-label">{t("CORE_SELECT_LANGUAGE") || "Select Language"}</label>
          <div className="login-language">
            <ul className="login-language-list">
              {languages?.map((language) => (
                <li
                  key={language.label}
                  className={selected === language.value ? "is-selected" : ""}
                  onClick={() => handleChangeLanguage(language)}
                >
                  {selected === language.value ? (
                    <img src={"/upyog-ui/images/check.svg"} alt="check icon" />
                  ) : null}&nbsp;{language.label}
                </li>
              ))}
              <li className="employee-login-language-search">
                <img src={"/upyog-ui/images/search.svg"} alt="search icon" />
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div className="login-actions login-actions--mt-32">
        <button
          type="button"
          className="login-primary-button"
          onClick={handleSubmit}
        >
          {t("CORE_COMMON_CONTINUE") || "Continue"}
        </button>
      </div>

      <p className="login-security login-security--mt-24">
        <span className="login-security-icon" aria-hidden="true">
          <img src={"/upyog-ui/images/secure.svg"} alt="secure" />
        </span>
        {t("CORE_LOGIN_SECURITY_MSG") || "Your information is safe and secure with us."}
      </p>
    </div>
  );
};

export default LanguageSelection;
