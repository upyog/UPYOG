import { Dropdown, Loader, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";

const ForgotPassword = ({ layout, config: propsConfig, t, emp = {}, setEmp = () => {} }) => {
  const { data: cities, isLoading } = Digit.Hooks.useTenants();
  const [user, setUser] = useState(null);
  const navigate = Digit.Hooks.useCustomNavigate();
  const [showToast, setShowToast] = useState(null);
  const [mobileNumber, setMobileNumber] = useState(emp?.mobileNumber || "");
  const [selectedCity, setSelectedCity] = useState(emp?.city || null);

  useEffect(() => {
    if (!user) {
      Digit.UserService.setType("employee");
      return;
    }
    Digit.UserService.setUser(user);
    const redirectPath = location.state?.from || "/upyog-ui/employee";
    navigate(redirectPath, { replace: true });
  }, [user]);

  const closeToast = () => {
    setShowToast(null);
  };

  const isMobileValid = /^[6-9]\d{9}$/.test(mobileNumber);

  const onForgotPassword = async (e) => {
    e?.preventDefault?.();
    if (!selectedCity) {
      setShowToast(t("ERR_HRMS_INVALID_CITY") || "Please Select City!");
      setTimeout(closeToast, 5000);
      return;
    }
    if (!isMobileValid) {
      setShowToast(t("ERR_HRMS_INVALID_MOBILE_NUMBER") || "Please enter a valid mobile number!");
      setTimeout(closeToast, 5000);
      return;
    }
    navigate(
      `/upyog-ui/employee/user/change-password?mobile_number=${mobileNumber}&tenantId=${selectedCity?.city?.code || selectedCity?.code}`
    );
  };

  const navigateToLogin = () => {
    navigate("/upyog-ui/employee/user/login", { replace: true });
  };

  const getLocalized = (key, fallback) => {
    if (!key) return fallback;
    const val = t(key);
    return !val || val === key ? fallback : val;
  };

  if (isLoading) {
    return <Loader />;
  }

  const footer = showToast && <Toast error={true} label={showToast} onClose={closeToast} />;

  return (
    <div className="login-mobile-step">
      <div className="login-form-header">
        <h2>{getLocalized(propsConfig?.texts?.header, "Forgot Password")}</h2>
        <p>{getLocalized("ES_FORGOT_PASSWORD_DESC", "Enter your registered mobile number and city to reset your password.")}</p>
      </div>

      <form onSubmit={onForgotPassword} className="login-form-body">
        {/* City Selection */}
        <div className="login-field-group">
          <label className="login-label">{getLocalized("CORE_COMMON_CITY", "City")}</label>
          <div className="city-selection">
            <span className="login-input-icon" aria-hidden="true">
              <img src={"/upyog-ui/images/search.svg"} alt="search icon" />
            </span>
            <Dropdown
              className="city-selection-dropdown"
              selected={selectedCity}
              select={(city) => {
                setSelectedCity(city);
                setEmp({ ...emp, city });
              }}
              option={cities}
              optionKey="i18nKey"
              t={t}
              placeholder={getLocalized("SEARCH_YOUR_CITY", "Search your city...")}
            />
          </div>
        </div>

        {/* Mobile Number */}
        <div className="login-field-group">
          <label className="login-label">{getLocalized("CORE_COMMON_MOBILE_NUMBER", "Mobile Number")}</label>
          <div className="login-input-wrap">
            <input
              type="tel"
              name="mobileNumber"
              autoComplete="tel"
              inputMode="numeric"
              maxLength={10}
              value={mobileNumber}
              placeholder={getLocalized("ENTER_MOBILE_NUMBER", "Enter Mobile Number")}
              onChange={(e) => {
                const value = e.target.value.replace(/\D/g, "").slice(0, 10);
                setMobileNumber(value);
                setEmp({ ...emp, mobileNumber: value });
              }}
            />
          </div>
        </div>

        {/* Action Buttons */}
        <div className="login-actions">
          <button type="submit" className="login-primary-button" disabled={!selectedCity || !isMobileValid}>
            {getLocalized(propsConfig?.texts?.submitButtonLabel, "Continue")}
          </button>

          <div className="login-divider">OR</div>

          <button type="button" className="login-secondary-button" onClick={navigateToLogin}>
            <span className="login-secondary-button__content">
              {getLocalized("CORE_COMMON_BACK_TO_LOGIN", "Back to Login")}
            </span>
          </button>
        </div>

        <p className="login-security">
          <span className="login-security-icon" aria-hidden="true">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            </svg>
          </span>
          <span>{getLocalized("CORE_LOGIN_SECURITY_MSG", "Secure and encrypted official login portal")}</span>
        </p>
      </form>
      {footer}
    </div>
  );
};

ForgotPassword.propTypes = {
  loginParams: PropTypes.any,
};

ForgotPassword.defaultProps = {
  loginParams: null,
};

export default ForgotPassword;