import { Dropdown, Loader, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";

/* set employee details to enable backward compatiable */
const setEmployeeDetail = (userObject, token) => {
  let locale = JSON.parse(sessionStorage.getItem("Digit.locale"))?.value || "en_IN";
  localStorage.setItem("Employee.tenant-id", userObject?.tenantId);
  localStorage.setItem("tenant-id", userObject?.tenantId);
  localStorage.setItem("citizen.userRequestObject", JSON.stringify(userObject));
  localStorage.setItem("locale", locale);
  localStorage.setItem("Employee.locale", locale);
  localStorage.setItem("token", token);
  localStorage.setItem("Employee.token", token);
  localStorage.setItem("user-info", JSON.stringify(userObject));
  localStorage.setItem("Employee.user-info", JSON.stringify(userObject));
};

const Login = ({ layout, config: propsConfig, t, isDisabled, emp = {}, setEmp = () => {} }) => {
  const { data: cities, isLoading } = Digit.Hooks.useTenants();
  const { data: storeData, isLoading: isStoreLoading } = Digit.Hooks.useStore.getInitData();
  const { stateInfo, languages } = storeData || {};
  const [user, setUser] = useState(null);
  const [showToast, setShowToast] = useState(null);
  const [disable, setDisable] = useState(false);
  const [selectedLanguage, setSelectedLanguage] = useState(Digit.StoreData.getCurrentLanguage() || "en_IN");
  const [username, setUsername] = useState(emp?.username || "");
  const [password, setPassword] = useState(emp?.password || "");
  const [selectedCity, setSelectedCity] = useState(emp?.city || null);
  const [showPassword, setShowPassword] = useState(false);

  const navigate = Digit.Hooks.useCustomNavigate();

  const handleLanguageSelection = (language) => {
    Digit.LocalizationService.changeLanguage(language.value, stateInfo?.code);
    setSelectedLanguage(language.value);
    setEmp({ ...emp, language: language });
  };

  const handleCitySelection = (city) => {
    setSelectedCity(city);
    setEmp({ ...emp, city: city });
  };

  useEffect(() => {
    if (!user) {
      return;
    }
    Digit.SessionStorage.set("citizen.userRequestObject", user);
    const filteredRoles = user?.info?.roles?.filter((role) => role.tenantId === Digit.SessionStorage.get("Employee.tenantId"));
    if (user?.info?.roles?.length > 0) user.info.roles = filteredRoles;
    Digit.UserService.setUser(user);
    setEmployeeDetail(user?.info, user?.access_token);
    let redirectPath = "/upyog-ui/employee";

    /* logic to redirect back to same screen where we left off  */
    if (window?.location?.href?.includes("from=")) {
      redirectPath = decodeURIComponent(window?.location?.href?.split("from=")?.[1]) || "/upyog-ui/employee";
    }

    /*  RAIN-6489 Logic to navigate to National DSS home incase user has only one role [NATADMIN]*/
    if (user?.info?.roles && user?.info?.roles?.length > 0 && user?.info?.roles?.every((e) => e.code === "NATADMIN")) {
      redirectPath = "/upyog-ui/employee/dss/landing/NURT_DASHBOARD";
    }
    /*  RAIN-6489 Logic to navigate to National DSS home incase user has only one role [NATADMIN]*/
    if (user?.info?.roles && user?.info?.roles?.length > 0 && user?.info?.roles?.every((e) => e.code === "STADMIN")) {
      redirectPath = "/upyog-ui/employee/dss/landing/home";
    }

    navigate(redirectPath, { replace: true });
  }, [user]);

  const onLogin = async (e) => {
    e?.preventDefault?.();
    if (!selectedCity) {
      setShowToast(t("ERR_HRMS_INVALID_CITY") || "Please Select City!");
      setTimeout(closeToast, 5000);
      return;
    }
    if (!username || !password) {
      setShowToast(t("ERR_HRMS_INVALID_CREDENTIALS") || "Please Enter Username and Password!");
      setTimeout(closeToast, 5000);
      return;
    }
    setDisable(true);
    const requestData = {
      username,
      password,
      userType: "EMPLOYEE",
      tenantId: selectedCity.code,
      language: selectedLanguage || languages?.[0]?.value,
    };
    try {
      const { UserRequest: info, ...tokens } = await Digit.UserService.authenticate(requestData);
      Digit.SessionStorage.set("Employee.tenantId", info?.tenantId);
      setUser({ info, ...tokens });
    } catch (err) {
      setShowToast(err?.response?.data?.error_description || "Invalid login credentials!");
      setTimeout(closeToast, 5000);
    }
    setDisable(false);
  };

  const closeToast = () => {
    setShowToast(null);
  };

  const onForgotPassword = () => {
    sessionStorage.getItem("User") && sessionStorage.removeItem("User");
    navigate("/upyog-ui/employee/user/forgot-password");
  };

  const footer = showToast && <Toast error={true} label={showToast} onClose={closeToast} />;

  const getLocalized = (key, fallback) => {
    if (!key) return fallback;
    const val = t(key);
    return !val || val === key ? fallback : val;
  };

  return isLoading || isStoreLoading ? (
    <Loader />
  ) : (
    <div className="login-mobile-step">
      <div className="login-form-header">
        <h2>{getLocalized(propsConfig?.texts?.header, "Login")}</h2>
        <p>
          {getLocalized("CORE_EMPLOYEE_LOGIN_SUBTITLE", "Enter your credentials to access the employee portal.")}
        </p>
      </div>

      <form onSubmit={onLogin} className="login-form-body">
        {/* Language Selection */}
        <div className="login-field-group">
          <label className="login-label">{getLocalized("CORE_SELECT_LANGUAGE", "Select Language")}</label>
          <div className="login-language">
            <ul className="login-language-list employee-login-language-list">
              {languages?.map((language) => (
                <li
                  key={language.label}
                  className={selectedLanguage === language.value ? "is-selected" : ""}
                  onClick={() => handleLanguageSelection(language)}
                >
                  {selectedLanguage === language.value ? (
                    <img src={"/images/check.svg"} alt="check icon" />
                  ) : null}&nbsp;{language.label}
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* City Selection */}
        <div className="login-field-group">
          <label className="login-label">{getLocalized("CORE_COMMON_CITY", "City")}</label>
          <div className="city-selection">
            <span className="login-input-icon" aria-hidden="true">
              <img src={"/images/search.svg"} alt="search icon" />
            </span>
            <Dropdown
              className="city-selection-dropdown"
              selected={selectedCity}
              select={handleCitySelection}
              option={cities}
              optionKey="i18nKey"
              t={t}
              placeholder={getLocalized("SEARCH_YOUR_CITY", "Search your city...")}
            />
          </div>
        </div>

        {/* User ID / Username */}
        <div className="login-field-group">
          <label className="login-label">{getLocalized("CORE_LOGIN_USERNAME", "User Name")}</label>
          <div className="login-input-wrap">
            <input
              type="text"
              name="username"
              autoComplete="username"
              value={username}
              placeholder={getLocalized("ENTER_USER_ID", "Enter User ID")}
              onChange={(e) => {
                setUsername(e.target.value);
                setEmp({ ...emp, username: e.target.value });
              }}
            />
          </div>
        </div>

        {/* Password */}
        <div className="login-field-group">
          <label className="login-label">{getLocalized("CORE_LOGIN_PASSWORD", "Password")}</label>
          <div className="login-input-wrap">
            <input
              type={showPassword ? "text" : "password"}
              name="password"
              autoComplete="current-password"
              value={password}
              placeholder={getLocalized("ENTER_PASSWORD", "Enter Password")}
              onChange={(e) => {
                setPassword(e.target.value);
                setEmp({ ...emp, password: e.target.value });
              }}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="login-password-toggle"
            >
              {showPassword ? getLocalized("HIDE", "HIDE") : getLocalized("SHOW", "SHOW")}
            </button>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="login-actions">
          <button
            type="submit"
            className="login-primary-button"
            disabled={isDisabled || disable || !username || !password || !selectedCity}
          >
            {getLocalized(propsConfig?.texts?.submitButtonLabel, "Continue")}
          </button>

          <div className="login-divider">OR</div>

          <button
            type="button"
            className="login-secondary-button"
            onClick={onForgotPassword}
          >
            <span className="login-secondary-button__content">
              {getLocalized(propsConfig?.texts?.secondaryButtonLabel, "Forgot Password?")}
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

Login.propTypes = {
  loginParams: PropTypes.any,
};

Login.defaultProps = {
  loginParams: null,
};

export default Login;