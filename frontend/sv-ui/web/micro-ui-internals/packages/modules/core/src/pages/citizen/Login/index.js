import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { AppContainer, BackButton, Toast } from "@nudmcdgnpm/upyog-ui-react-components-lts";
// Updated: Imported Route, Routes, useLocation from react-router-dom v6.
// Updated: <Switch> replaced with <Routes>, component/render props replaced with element prop.

import { Route, Routes, useLocation } from "react-router-dom";
import { loginSteps } from "./config";
import SelectMobileNumber from "./SelectMobileNumber";
import SelectOtp from "./SelectOtp";
import SelectName from "./SelectName";
// Updated: date-fns v3 — named imports remain same, no breaking change for subYears and format.

import { subYears, format } from "date-fns";
const TYPE_REGISTER = { type: "register" };
const TYPE_LOGIN = { type: "login" };
const DEFAULT_USER = "digit-user";
const DEFAULT_REDIRECT_URL = "/sv-ui/citizen/sv-home";

/* set citizen details to enable backward compatiable */
const setCitizenDetail = (userObject, token, tenantId) => {
  let locale = JSON.parse(sessionStorage.getItem("Digit.initData"))?.value?.selectedLanguage;
  localStorage.setItem("Citizen.tenant-id", tenantId);
  localStorage.setItem("tenant-id", tenantId);
  localStorage.setItem("citizen.userRequestObject", JSON.stringify(userObject));
  localStorage.setItem("locale", locale);
  localStorage.setItem("Citizen.locale", locale);
  localStorage.setItem("token", token);
  localStorage.setItem("Citizen.token", token);
  localStorage.setItem("user-info", JSON.stringify(userObject));
  localStorage.setItem("Citizen.user-info", JSON.stringify(userObject));
};

const getFromLocation = (state, searchParams) => {
  return state?.from || searchParams?.from || DEFAULT_REDIRECT_URL;
};

const Login = ({ stateCode, isUserRegistered = true }) => {
  const { t } = useTranslation();
  const location = useLocation();
  // Updated: useCustomNavigate replaces useHistory from react-router-dom v5.
  // useHistory() removed in v6 — navigate() used instead of history.push().

  const navigate = Digit.Hooks.useCustomNavigate();
  const [user, setUser] = useState(null);
  const [error, setError] = useState(null);
  const [isOtpValid, setIsOtpValid] = useState(true);
  const [tokens, setTokens] = useState(null);
  const [params, setParmas] = useState(isUserRegistered ? {} : location?.state?.data);
  const [errorTO, setErrorTO] = useState(null);
  const searchParams = Digit.Hooks.useQueryParams();
  const [canSubmitName, setCanSubmitName] = useState(false);
  const [canSubmitOtp, setCanSubmitOtp] = useState(true);
  const [canSubmitNo, setCanSubmitNo] = useState(true);

  useEffect(() => {
    let errorTimeout;
    if (error) {
      if (errorTO) {
        clearTimeout(errorTO);
        setErrorTO(null);
      }
      errorTimeout = setTimeout(() => {
        setError("");
      }, 5000);
      setErrorTO(errorTimeout);
    }
    return () => {
      errorTimeout && clearTimeout(errorTimeout);
    };
  }, [error]);

  useEffect(() => {
    if (!user) {
      return;
    }
    Digit.SessionStorage.set("citizen.userRequestObject", user);
    Digit.UserService.setUser(user);
    setCitizenDetail(user?.info, user?.access_token, stateCode);
    const redirectPath = location.state?.from || DEFAULT_REDIRECT_URL;
  // Updated: history.push() replaced with navigate() — react-router-dom v6.
  // Updated: replace option passed as second argument object instead of separate call.

    if (!Digit.ULBService.getCitizenCurrentTenant(true)) {
      navigate("/sv-ui/citizen/select-location", {
        replace: true,
        state: { redirectBackTo: redirectPath }
      });
    } else {
      // Updated: history.push() replaced with navigate() — react-router-dom v6.
      navigate(redirectPath, { replace: true });
    }
  }, [user]);

  const stepItems = useMemo(() =>
    loginSteps.map(
      (step) => {
        const texts = {};
        for (const key in step.texts) {
          texts[key] = t(step.texts[key]);
        }
        return { ...step, texts };
      },
      [loginSteps]
    )
  );

  const getUserType = () => "citizen";

  const handleOtpChange = (otp) => {
    setParmas({ ...params, otp });
  };

  const handleMobileChange = (event) => {
    const { value } = event.target;
    setParmas({ ...params, mobileNumber: value });
  };

  const selectMobileNumber = async (mobileNumber) => {
    setCanSubmitNo(false);
    setParmas({ ...params, ...mobileNumber });
    const data = {
      ...mobileNumber,
      tenantId: stateCode,
      userType: "citizen",
    };
    if (isUserRegistered) {
      const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_LOGIN } });
      if (!err) {
        setCanSubmitNo(true);
        // Updated: history.push() replaced with navigate() — react-router-dom v6.
        // Updated: state passed as second argument object — same as v5 but cleaner syntax.

        navigate("otp", { 
          replace: true, 
          state: { from: getFromLocation(location.state, searchParams), role: location.state?.role } 
        });
        return;
      } else {
        setCanSubmitNo(true);
        if (!(location.state && location.state.role === ("FSM_DSO" || "WT_VENDOR"))) {
          // Updated: history.push() replaced with navigate() — react-router-dom v6.
          navigate("/sv-ui/citizen/register/name", { 
            state: { from: getFromLocation(location.state, searchParams), data: data } 
          });
        }
      }
      if (location.state?.role) {
        setCanSubmitNo(true);
        setError(location.state?.role === "FSM_DSO" ? t("ES_ERROR_DSO_LOGIN") : "User not registered.");
      }
      if (location.state?.role) {
        setCanSubmitNo(true);
        setError(location.state?.role === "WT_VENDOR" ? t("ES_ERROR_WT_VENDOR_LOGIN") : "User not registered.");
      }
    } else {
      const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_REGISTER } });
      if (!err) {
        setCanSubmitNo(true);
        // Updated: history.push() replaced with navigate() — react-router-dom v6.

        navigate("otp", { 
          replace: true, 
          state: { from: getFromLocation(location.state, searchParams) } 
        });

        return;
      }
      setCanSubmitNo(true);
    }
  };
  function selectCommencementDate(value) {
    const appDate= new Date();
    // Updated: date-fns v3 — subYears and format named imports work same as v2.
    const proposedDate= format(subYears(appDate, 18), 'yyyy-MM-dd').toString();

    if( convertDateToEpoch(proposedDate)  <= convertDateToEpoch(value)){
      return true     
    }
    else {
      return false;     
    }    
  }
  const selectName = async (name) => {
    const data = {
      ...params,
      tenantId: stateCode,
      userType: getUserType(),
      ...name,
    };
    if (selectCommencementDate(name.dob))
    {
      setError("Minimum age should be 18 years");
      setTimeout(() => {
        setError(false);
      }, 3000);
    }
    else {
      setParmas({ ...params, ...name });
      setCanSubmitName(true);
      const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_REGISTER } });
      if (res) {
        setCanSubmitName(false);
        // Updated: history.push() replaced with navigate() — react-router-dom v6.
        navigate("otp", { 
          replace: true, 
          state: { from: getFromLocation(location.state, searchParams) } 
        });

      } else {
        setCanSubmitName(false);
      }
    }
    
  
  };

  const selectOtp = async () => {
    try {
      setIsOtpValid(true);
      setCanSubmitOtp(false);
      const { mobileNumber, otp, name } = params;
      if (isUserRegistered) {
        const requestData = {
          username: mobileNumber ? mobileNumber:sessionStorage.getItem("userName"),
          password: otp,
          tenantId: stateCode,
          userType: getUserType(),
        };
        const { ResponseInfo, UserRequest: info, ...tokens } = await Digit.UserService.authenticate(requestData);

        if (location.state?.role) {
          const roleInfo = info.roles.find((userRole) => userRole.code === location.state.role);
          if (!roleInfo || !roleInfo.code) {
            setError(t("ES_ERROR_USER_NOT_PERMITTED"));
            setTimeout(() => navigate(DEFAULT_REDIRECT_URL, { replace: true }), 5000);
            return;
          }
        }
        if (window?.globalConfigs?.getConfig("ENABLE_SINGLEINSTANCE")) {
          info.tenantId = Digit.ULBService.getStateId();
        }

        setUser({ info, ...tokens });
      } else if (!isUserRegistered) {
        const requestData = {
          name,
          username: mobileNumber,
          otpReference: otp,
          tenantId: stateCode,
        };

        const { ResponseInfo, UserRequest: info, ...tokens } = await Digit.UserService.registerUser(requestData, stateCode);

        if (window?.globalConfigs?.getConfig("ENABLE_SINGLEINSTANCE")) {
          info.tenantId = Digit.ULBService.getStateId();
        }

        setUser({ info, ...tokens });
      }
    } catch (err) {
      setCanSubmitOtp(true);
      setIsOtpValid(false);
    }
  };

  const resendOtp = async () => {
    const { mobileNumber } = params;
    const data = {
      mobileNumber,
      tenantId: stateCode,
      userType: getUserType(),
    };
    if (!isUserRegistered) {
      const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_REGISTER } });
    } else if (isUserRegistered) {
      const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_LOGIN } });
    }
  };

  const sendOtp = async (data) => {
    try {
      const res = await Digit.UserService.sendOtp(data, stateCode);
      return [res, null];
    } catch (err) {
      return [null, err];
    }
  };

  return (
    <div className="citizen-form-wrapper">
      <AppContainer>
        <BackButton />
        {/* Updated: <Switch> replaced with <Routes> — react-router-dom v6. */}
        {/* Updated: component/render props replaced with element prop in all Route components. */}
        {/* Updated: AppContainer moved outside <Routes> — custom components cannot be direct children of <Routes> in v6. */}

        <Routes>
            <Route 
              path="/" 
              element={
                <SelectMobileNumber
                  onSelect={selectMobileNumber}
                  config={stepItems[0]}
                  mobileNumber={params.mobileNumber || ""}
                  onMobileChange={handleMobileChange}
                  canSubmit={canSubmitNo}
                  showRegisterLink={isUserRegistered && !location.state?.role}
                  t={t}
                />
              } 
            />
          {/* Updated: component={SelectOtp} replaced with element={<SelectOtp />} — react-router-dom v6. */}

            <Route 
              path="otp" 
              element={
                <SelectOtp
                  config={{ ...stepItems[1], texts: { ...stepItems[1].texts, cardText: `${stepItems[1].texts.cardText} ${params.mobileNumber || ""}` } }}
                  onOtpChange={handleOtpChange}
                  onResend={resendOtp}
                  onSelect={selectOtp}
                  otp={params.otp}
                  error={isOtpValid}
                  canSubmit={canSubmitOtp}
                  t={t}
                />
              } 
            />
          <Route 
            path="name" 
            element={
              <SelectName config={stepItems[2]} onSelect={selectName} t={t} isDisabled={canSubmitName} />
            } 
          />
        </Routes>
            {error && <Toast error={true} label={error} onClose={() => setError(null)} />}
        </AppContainer>
    </div>
  );
};

export default Login;
export const convertDateToEpoch = (dateString, dayStartOrEnd = "dayend") => {
  //example input format : "2018-10-02"
  try {
    const parts = dateString.match(/(\d{4})-(\d{1,2})-(\d{1,2})/);
    const DateObj = new Date(Date.UTC(parts[1], parts[2] - 1, parts[3]));
    DateObj.setMinutes(DateObj.getMinutes() + DateObj.getTimezoneOffset());
    if (dayStartOrEnd === "dayend") {
      DateObj.setHours(DateObj.getHours() + 24);
      DateObj.setSeconds(DateObj.getSeconds() - 1);
    }
    return DateObj.getTime();
  } catch (e) {
    return dateString;
  }
};