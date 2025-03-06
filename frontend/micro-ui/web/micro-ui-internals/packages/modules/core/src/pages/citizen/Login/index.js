import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { AppContainer, BackButton, Toast } from "@upyog/digit-ui-react-components";
import { Route, Switch, useHistory, useRouteMatch, useLocation } from "react-router-dom";
import { loginSteps } from "./config";
import SelectMobileNumber from "./SelectMobileNumber";
import SelectOtp from "./SelectOtp";
import SelectName from "./SelectName";

const TYPE_REGISTER = { type: "register" };
const TYPE_LOGIN = { type: "login" };
const DEFAULT_USER = "digit-user";
const DEFAULT_REDIRECT_URL = "/digit-ui/citizen";

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
  const { path, url } = useRouteMatch();
  const history = useHistory();
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
  const [captcha, setCaptcha] = useState([]);
  const [showToast, setShowToast] = useState(null);
  const [disable, setDisable] = useState(false);

  useEffect(async ()=>{
    let isMounted = true;
    
    fetchCaptcha();
    
    return()=>{
      isMounted = false;
    }
  },[])
  const fetchCaptcha = async()=>{
    try {
      const res = await Digit.UserService.generateCaptcha({});
      if(res && res?.captcha?.captcha && res?.captcha?.captchaUuid) {
        let tt = [];
        tt.push(res?.captcha)
        // if(isMounted) {
          setCaptcha(tt)
        // }
        // loadForm()

      }
      // return [res, null];
    } catch (err) {
      return [null, err];
    }
  }
  useEffect(()=>{
    console.log("captcha----",captcha)
  },[captcha])

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
    if (!Digit.ULBService.getCitizenCurrentTenant(true)) {
      history.replace("/digit-ui/citizen/select-location", {
        redirectBackTo: redirectPath,
      });
    } else {
      history.replace(redirectPath);
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

  const getUserType = () => Digit.UserService.getType();

  const handleOtpChange = (otp) => {
    setParmas({ ...params, otp });
  };
  const onCaptchaChange = (captchaText) => {
    setParmas({ ...params, captcha: captchaText,  captchaUuid: captcha[0]?.captchaUuid});
  }

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
      userType: getUserType(),
    };
    if (isUserRegistered) {
      const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_LOGIN } });
      if (!err) {
        setCanSubmitNo(true);
        history.replace(`${path}/otp`, { from: getFromLocation(location.state, searchParams), role: location.state?.role });
        return;
      } else {
        setCanSubmitNo(true);
        if (!(location.state && location.state.role === "FSM_DSO")) {
          history.push(`/digit-ui/citizen/register/name`, { from: getFromLocation(location.state, searchParams), data: data });
        }
      }
      if (location.state?.role) {
        setCanSubmitNo(true);
        setError(location.state?.role === "FSM_DSO" ? t("ES_ERROR_DSO_LOGIN") : "User not registered.");
      }
    } else {
      const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_REGISTER } });
      if (!err) {
        setCanSubmitNo(true);
        history.replace(`${path}/otp`, { from: getFromLocation(location.state, searchParams) });
        return;
      }
      setCanSubmitNo(true);
    }
  };

  const selectName = async (name) => {
    const data = {
      ...params,
      tenantId: stateCode,
      userType: getUserType(),
      ...name,
    };
    setParmas({ ...params, ...name });
    setCanSubmitName(true);
    const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_REGISTER } });
    if (res) {
      setCanSubmitName(false);
      history.replace(`${path}/otp`, { from: getFromLocation(location.state, searchParams) });
    } else {
      setCanSubmitName(false);
    }
  };

  const selectOtp = async () => {
        try {
      setIsOtpValid(true);
      setCanSubmitOtp(false);
      const { mobileNumber, otp, name, captcha, captchaUuid } = params;
      if (isUserRegistered) {
        const requestData = {
          username: mobileNumber,
          captcha: captcha,
          captchaUuid: captchaUuid,
          password: otp,
          tenantId: stateCode,
          userType: getUserType(),
        };
        const { ResponseInfo, UserRequest: info, ...tokens } = await Digit.UserService.authenticate(requestData);
        
        if (location.state?.role) {
          const roleInfo = info.roles.find((userRole) => userRole.code === location.state.role);
          if (!roleInfo || !roleInfo.code) {
            setError(t("ES_ERROR_USER_NOT_PERMITTED"));
            setTimeout(() => history.replace(DEFAULT_REDIRECT_URL), 5000);
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
          captcha: captcha,
          captchaUuid: captchaUuid,
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
      setShowToast(err?.response?.data?.error_description || "Invalid OTP or Captcha");
      setTimeout(closeToast, 5000);
      setCanSubmitOtp(false);
      setIsOtpValid(true);
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
  const onCaptchaRefresh = () =>{
    fetchCaptcha()
  }
  const closeToast = () => {
    setShowToast(null);
  };

  return (
    <div className="citizen-form-wrapper">
      <div>
        <div style={{marginBottom: "20%", fontStyle: 'italic', fontSize: '22px', WebkitTextFillColor: 'transparent', background: 'linear-gradient(to right, rgb(247 247 251), rgb(226 249 5), rgb(233 233 233), rgb(13 13 13)) text', WebkitBackgroundClip: 'text', fontWeight: "600"}}>
          <div> Simplify, Pay, Thrive: </div>
          <div> Let Your Property Tax Portal Lead the Way! </div>
        </div>
        
        <div>
          {/* <img src="https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/Khongjom_War_Memorial.png" style={{width: '150px', height: '200px', display: 'inline', boxShadow: '0px 0px 0px 5px white'}}/> */}
          <img src="https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/Kangla-Museum.png" style={{width: '340px', height: '200px', display: 'inline', boxShadow: '0px 0px 0px 5px white', position: "relative", left: "40px", top: "-120px"}}/>
          <img src="https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/Lainingthou_Sanamahi_Temple.png" style={{width: '340px', height: '200px', display: 'inline', boxShadow: '0px 0px 0px 5px white', marginLeft: '20px', position: "relative", left: "-80px"}}/>

        </div>
        
      </div>
      
      <Switch>
        <AppContainer style={{ right: "-10%", width: '410px', marginTop: "48px", marginLeft: "100px"}}>
          {/* <BackButton /> */}
          <Route path={`${path}`} exact>
            <SelectMobileNumber
              onSelect={selectMobileNumber}
              config={stepItems[0]}
              mobileNumber={params.mobileNumber || ""}
              onMobileChange={handleMobileChange}
              canSubmit={canSubmitNo}
              showRegisterLink={isUserRegistered && !location.state?.role}
              t={t}
            />
          </Route>
          <Route path={`${path}/otp`}>
            <SelectOtp
              config={{ ...stepItems[1], texts: { ...stepItems[1].texts, cardText: `${stepItems[1].texts.cardText} ${params.mobileNumber || ""}` } }}
              onOtpChange={handleOtpChange}
              onResend={resendOtp}
              onSelect={selectOtp}
              otp={params.otp}
              error={isOtpValid}
              canSubmit={canSubmitOtp}
              captchaDetails={[...captcha]}
              onCaptchaRefresh={onCaptchaRefresh}
              onCaptchaChange={onCaptchaChange}
              t={t}
            />
          </Route>
          <Route path={`${path}/name`}>
            <SelectName config={stepItems[2]} onSelect={selectName} t={t} isDisabled={canSubmitName} />
          </Route>
          {error && <Toast error={true} label={error} onClose={() => setError(null)} />}
          {showToast && <Toast error={true} label={t(showToast)} onClose={closeToast} />}

        </AppContainer>
      </Switch>
    </div>
  );
};

export default Login;
