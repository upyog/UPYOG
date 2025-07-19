import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { AppContainer, BackButton, Toast } from "@demodigit/digit-ui-react-components";
import { Route, Switch, useHistory, useRouteMatch, useLocation } from "react-router-dom";
import { loginSteps } from "./config";
import SelectMobileNumber from "./SelectMobileNumber";
import SelectOtp from "./SelectOtp";
import SelectName from "./SelectName";
import { subYears, format } from "date-fns";
const TYPE_REGISTER = { type: "register" };
const TYPE_LOGIN = { type: "login" };
const DEFAULT_USER = "digit-user";
const DEFAULT_REDIRECT_URL = "/digit-ui/citizen/pgr-home";

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
  let selectedCity = {
    "i18nKey": "TENANT_TENANTS_PG_CITYA",
    "code": "pg.citya",
    "name": "India",
    "description": "India",
    "pincode": [
        143001,
        143002,
        143003,
        143004,
        143005
    ],
    "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
    "imageId": null,
    "domainUrl": "https://www.upyog.niua.org",
    "type": "CITY",
    "twitterUrl": null,
    "facebookUrl": null,
    "emailId": "citya@gmail.com",
    "OfficeTimings": {
        "Mon - Fri": "9.00 AM - 6.00 PM"
    },
    "city": {
        "name": "India",
        "localName": null,
        "districtCode": "CITYA",
        "districtName": null,
        "districtTenantCode": "pg.citya",
        "regionName": null,
        "ulbGrade": "Municipal Corporation",
        "longitude": 75.5761829,
        "latitude": 31.3260152,
        "shapeFileLocation": null,
        "captcha": null,
        "code": "IN",
        "ddrName": "DDR A"
    },
    "address": "India Municipal Corporation",
    "contactNumber": "001-2345876"
}
  Digit.SessionStorage.set("CITIZEN.COMMON.HOME.CITY", selectedCity);
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
  function selectCommencementDate(value) {
    const appDate= new Date();
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
    console.log("name",name)
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
        history.replace(`${path}/otp`, { from: getFromLocation(location.state, searchParams) });
      } else {
        setCanSubmitName(false);
      }
    }
    
  
  };

  const selectOtp = async () => {
    try {
      setIsOtpValid(true);
      setCanSubmitOtp(false);
      const { mobileNumber, otp, name,username, password,email,passportNumber,gender,address,dob} = params;
      console.log("paramsparams",isUserRegistered,params)
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
            setTimeout(() => history.replace(DEFAULT_REDIRECT_URL), 5000);
            return;
          }
        }
        if (window?.globalConfigs?.getConfig("ENABLE_SINGLEINSTANCE")) {
          info.tenantId = Digit.ULBService.getStateId();
        }

        setUser({ info, ...tokens });
      } else if (!isUserRegistered) {
        let dobNew=dob.split("-")

        const requestData = {
        otpReference: otp,
        username: username,
        password: password,
        mobileNumber: mobileNumber,
        name: name,
        emailId: email,
        passportNo: passportNumber,
        tenantId: "pg",
        permanentCity: "pg",
        permanentAddress:address,
        gender:"Male",
        dob:dobNew[2]+"/"+dobNew[1]+"/"+dobNew[0]
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
    <div className="citizen-form-wrapper" style={{width:"100%",marginTop:"0px",paddingLeft:"0px",paddingRight:"0px",height:"inherit"}}>
      <Switch>
        <AppContainer style={{height: "calc(100% + 25px)"}}>
          <BackButton /> 
          <Route path={`${path}`} exact>
            <SelectMobileNumber
              onSelect={selectMobileNumber}
              config={stepItems[0]}
              mobileNumber={params?.mobileNumber || ""}
              onMobileChange={handleMobileChange}
              canSubmit={canSubmitNo}
              showRegisterLink={isUserRegistered && !location.state?.role}
              t={t}
            />
          </Route>
          <Route path={`${path}/otp`}>
            <SelectOtp
              config={{ ...stepItems[1], texts: { ...stepItems[1].texts, cardText: `${stepItems[1].texts.cardText} ${params?.mobileNumber || ""}` } }}
              onOtpChange={handleOtpChange}
              onResend={resendOtp}
              onSelect={selectOtp}
              otp={params?.otp}
              error={isOtpValid}
              canSubmit={canSubmitOtp}
              t={t}
            />
          </Route>
          <Route path={`${path}/name`}>
            <SelectName config={stepItems[2]} onSelect={selectName} t={t} isDisabled={canSubmitName} />
          </Route>
          {error && <Toast error={true} label={error} onClose={() => setError(null)} />}
        </AppContainer>
      </Switch>
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