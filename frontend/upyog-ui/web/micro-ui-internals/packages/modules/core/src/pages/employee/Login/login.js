import { Dropdown, FormComposer, Loader, Toast } from "@nudmcdgnpm/digit-ui-react-components";
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

const Login = ({ layout, config: propsConfig, t, isDisabled, emp, setEmp }) => {
  const { data: cities, isLoading } = Digit.Hooks.useTenants();
  const { data: storeData, isLoading: isStoreLoading } = Digit.Hooks.useStore.getInitData();
  const { stateInfo, languages } = storeData || {};
  const [user, setUser] = useState(null);
  const [showToast, setShowToast] = useState(null);
  const [disable, setDisable] = useState(false);
  const [selectedLanguage, setSelectedLanguage] = useState(Digit.StoreData.getCurrentLanguage() || "en_IN");
  const { isEmployeeV2 = false } = layout?.loginUI

  const navigate = Digit.Hooks.useCustomNavigate();
  // const getUserType = () => "EMPLOYEE" || Digit.UserService.getType();

  const handleLanguageSelection = (language) => {
    Digit.LocalizationService.changeLanguage(language.value, stateInfo?.code);
    setSelectedLanguage(language.value);
    setEmp({ ...emp, language: language })
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

  const onLogin = async (data) => {
    data = emp
    navigate("/upyog-ui/employee/dashboard");

    // if (!data.city) {
    //   alert("Please Select City!");
    //   return;
    // }
    // setDisable(true);
    // const requestData = {
    //   ...data,
    //   userType: "EMPLOYEE",
    // };
    // requestData.tenantId = data.city.code;
    // requestData.language = data?.language?.value || languages[0]?.value;
    // delete requestData.city;
    // try {
    //   const { UserRequest: info, ...tokens } = await Digit.UserService.authenticate(requestData);
    //   Digit.SessionStorage.set("Employee.tenantId", info?.tenantId);
    //   setUser({ info, ...tokens });
    // } catch (err) {
    //   setShowToast(err?.response?.data?.error_description || "Invalid login credentials!");
    //   setTimeout(closeToast, 5000);
    // }
    // setDisable(false);
  };

  const closeToast = () => {
    setShowToast(null);
  };

  const onForgotPassword = () => {
    sessionStorage.getItem("User") && sessionStorage.removeItem("User")
    navigate("/upyog-ui/employee/user/forgot-password");
  };

  const [userId, password, city] = propsConfig.inputs;
  let config = [
    {
      body: [
        {
          label: t(city.label),
          type: city.type,
          populators: {
            name: city.name,
            component: ({ onChange, value, setValue }) => (
              <div className="employee-city-selection">
                <span className="employee-city-selection-icon" aria-hidden="true">
                  <img src={"/images/search.svg"} alt="search icon" />
                </span>
                <Dropdown
                  option={cities}
                  className="login-city-dd"
                  optionKey="i18nKey"
                  select={(d) => {
                    setEmp({ ...emp, city: d })
                    if (typeof onChange === "function") {
                      onChange(d);
                      return;
                    }
                    setValue?.(city.name, d);
                  }}
                  selected={value}
                  t={t}
                  placeholder={"Search your city"}
                />
              </div>
            ),
          },
          isMandatory: true,
        },
      ],
    },
  ];
  if (!isEmployeeV2) {
    config = config.map(item => ({
      ...item, body: [...item.body,

      {
        label: "Select Language",
        type: "custom",
        populators: {
          name: "language",
          component: () => (
            <div className="employee-login-language">
              <ul className="employee-login-language-list">
                {languages?.map((language) => (
                  <li
                    key={language.label}
                    className={selectedLanguage === language.value ? "is-selected" : ""}
                    onClick={() => handleLanguageSelection(language)}
                  >
                    {selectedLanguage === language.value ? <img src={"/images/check.svg"} alt="check icon" /> : null}
                    <span>{language.label}</span>
                  </li>
                ))}
                <li className="employee-login-language-search">
                  <img src={"/images/search.svg"} alt="search icon" />
                </li>
              </ul>
            </div>
          ),
        },
      }
      ]
    }));
  }
  config = config.map(item => ({
    ...item, body: [...item.body,
    {
      label: t(userId.label),
      type: userId.type,
      populators: {
        name: userId.name,
        onChange: (e) => setEmp({ ...emp, [userId.name]: e.target.value })
      },
      isMandatory: true,
    },
    {
      label: t(password.label),
      type: password.type,
      populators: {
        name: password.name,
        onChange: (e) => setEmp({ ...emp, [password.name]: e.target.value })
      },
      isMandatory: true,
    }]
  }))
  const footer = showToast && <Toast error={true} label={showToast} onClose={closeToast} />;

  return isLoading || isStoreLoading ? (
    <Loader />
  ) : (
    <>
      {footer}
      {isEmployeeV2 && <div className="dropDown-v2">
        <div className="innder-dropdowwn">
          <Dropdown
            option={languages}
            className="login-city-dd-v2"
            optionKey="label"
            select={(d) => {
              setEmp({ ...emp, language: d })
            }}
            // selected={value}
            t={t}
            placeholder={"language"}
            selected={languages[0]}
            icon={"🌎"}
          />
          🌎<button>Help</button>
        </div>
      </div>}
      <FormComposer
        onSubmit={onLogin}
        isDisabled={isDisabled || disable}
        noBoxShadow
        inline
        submitInForm
        config={config}
        label={propsConfig.texts.submitButtonLabel}
        secondaryActionLabel={propsConfig.texts.secondaryButtonLabel}
        onSecondayActionClick={onForgotPassword}
        heading={propsConfig.texts.header}
        headingStyle={{ textAlign: "center" }}
        cardStyle={{ margin: "0 auto", width: "100%" }}
        cardClassName="loginFormStyleEmployee"
        buttonStyle={{ maxWidth: "100%", width: "100%", backgroundColor: "#5a1166" }}
        enp={emp}
        setEmp={setEmp}
      />
    </>
  );
};

Login.propTypes = {
  loginParams: PropTypes.any,
};

Login.defaultProps = {
  loginParams: null,
};

export default Login;