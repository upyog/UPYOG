import React, { useState, useEffect } from "react";
import { CardText,Card, CardHeader,FormStep, CitizenConsentForm, Loader, CheckBox,ButtonSelector,TextInput ,OTPInput} from "@upyog/digit-ui-react-components";
import {useLocation,useHistory } from "react-router-dom";
const TYPE_LOGIN = { type: "login" };
const DEFAULT_REDIRECT_URL = "/digit-ui/citizen";
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
const SelectMobileNumber = ({ t, onSelect, showRegisterLink, mobileNumber, onMobileChange, config, canSubmit }) => {
  const location = useLocation();
  const history = useHistory();
  const [isCheckBox, setIsCheckBox] = useState(false);
  const [isCCFEnabled, setisCCFEnabled] = useState(false);
  const [mdmsConfig, setMdmsConfig] = useState("");
  const [error, setError]=useState("");
  const [isOtpLogin, setIsOtpLogin] = useState(false);
  const [isOtpSent, setIsOtpSent] = useState(false);
  const [formValues, setFormValues] = useState({
    username: '',
    password: '',
    mobile: '',
    otp: ''
  });
  const { isLoading, data } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "common-masters", [{ name: "CitizenConsentForm" }]);

  function setTermsAndPolicyDetails(e) {
    setIsCheckBox(e.target.checked)
  }

  const checkDisbaled = () => {
    if (isCCFEnabled?.isCitizenConsentFormEnabled) {
      return !(mobileNumber.length === 10 && canSubmit && isCheckBox)
    } else {
      return !(mobileNumber.length === 10 && canSubmit)
    }
  }

  useEffect(()=> {
    if (data?.["common-masters"]?.CitizenConsentForm?.[0]?.isCitizenConsentFormEnabled) {
      setisCCFEnabled(data?.["common-masters"]?.CitizenConsentForm?.[0])
    }
  }, [data]);

  const onLinkClick = (e) => {
    setMdmsConfig(e.target.id)
}

  const checkLabels = () => {
    return (
    <span>
      {isCCFEnabled?.checkBoxLabels?.map((data, index) => {
        return <span>
          {/* {index == 0 && "CCF"} */}
          {data?.linkPrefix && <span>{t(`${data?.linkPrefix}_`)}</span>}
          {data?.link && <span id={data?.linkId} onClick={(e) => { onLinkClick(e) }} style={{ color: "#a82227", cursor: "pointer" }}>{t(`${data?.link}_`)}</span>}
          {data?.linkPostfix && <span>{t(`${data?.linkPostfix}_`)}</span>}
          {(index == isCCFEnabled?.checkBoxLabels?.length - 1) && t("LABEL")}
        </span>
      })}
    </span>
    );
  };
  const validateMobileNumber=()=>{
      if(/^\d{0,10}$/.test(mobileNumber)){
        setError("")
      }
  };
  const handleMobileChange=(e)=>{
      const value=e.target.value;
      if(/^\d{0,10}$/.test(value)|| value===""){
        onMobileChange(e);
        validateMobileNumber();
      }
      else{
        setError(t("CORE_COMMON_PROFILE_MOBILE_NUMBER_INVALID"));
      }
  };
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues({
      ...formValues,
      [name]: value,
    });
  };
  const handleChangeOTP = (otp) => {
    console.log("hell1234")
    setFormValues({
      ...formValues,
      otp,
    });
  };

  const toggleLoginMethod = () => {
    console.log("hell122")
    setIsOtpLogin(!isOtpLogin);
    setIsOtpSent(false);
  };
 
  const register = async (e) => {
    const data = await Digit.DigiLockerService.register({ module: "REGISTER" });
    e.preventDefault()
    const redirectUrl = data.redirectURL
    console.log("data", data)
    sessionStorage.setItem("code_verfier_register", data?.codeverifier)
    window.location.href = redirectUrl
  }
  const sendOtp = async (data) => {
    try {
      const res = await Digit.UserService.sendOtp(data, "pg");
      return [res, null];
    } catch (err) {
      return [null, err];
    }
  };
  const handleSendOtp = async (e) => {
    e.preventDefault()
    console.log('OTP sent to:', formValues.mobile);
    const data = {
      mobileNumber: formValues.mobile,
      tenantId: "pg",
      userType: "Citizen",
    };
    const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_LOGIN } });
    setIsOtpSent(true);
  };
  const handleSubmit = async (e) => {
    console.log("hell115")
    e.preventDefault();
    const requestData = isOtpLogin
      ? {
          username: formValues?.mobile,
          password: formValues?.otp,
          tenantId: "pg",
          userType: "CITIZEN",
          otp: "Y"
        }
      : {
          username: formValues?.username,
          password: formValues?.password,
          tenantId: "pg",
          userType: "CITIZEN",
          otp: "N"
        };
    try {
      console.log("hell116")
      const { ResponseInfo, UserRequest: info, ...tokens } = await Digit.UserService.authenticate(requestData);
      if (!ResponseInfo) throw new Error("No response info");
      console.log("hell117")
      if (location.state?.role) {
        const roleInfo = info.roles.find((userRole) => userRole.code === location.state.role);
        if (!roleInfo || !roleInfo.code) {
          setError(t("ES_ERROR_USER_NOT_PERMITTED"));
          setTimeout(() => history.push("/digit-ui/citizen"), 5000);
          return;
        }
      }
  
      if (window?.globalConfigs?.getConfig("ENABLE_SINGLEINSTANCE")) {
        info.tenantId = Digit.ULBService.getStateId();
      }
      const redirectPath = location.state?.from || DEFAULT_REDIRECT_URL;
      const userNew = { info, ...tokens };
      Digit.SessionStorage.set("citizen.userRequestObject", userNew);
      Digit.UserService.setUser(userNew);
      setCitizenDetail(userNew?.info, userNew?.access_token, "pg");
      console.log("hell118")
      if (!Digit.ULBService.getCitizenCurrentTenant(true)) {
        history.replace("/digit-ui/citizen/select-location", {
          redirectBackTo: redirectPath,
        });
      } else {
        history.replace(redirectPath);
      }
    } catch (error) {
      console.error('Error during authentication:', error);
      setError(t("ES_ERROR_LOGIN_FAILED"));
    }
  };
  return (
    // <FormStep
    //   isDisabled={checkDisbaled()}
    //   onSelect={onSelect}
    //   config={config}
    //   t={t}
    //   componentInFront="+91"
    //   onChange={handleMobileChange}
    //   value={mobileNumber}
    // >
    //   {error && <p style={{color:"red"}}>{error}</p>}
    //   {isCCFEnabled?.isCitizenConsentFormEnabled && (
    //   <div>
    //     <CheckBox
    //       className="form-field"
    //       label={checkLabels()}
    //       value={isCheckBox}
    //       checked={isCheckBox}
    //       style={{ marginTop: "5px", marginLeft: "55px" }}
    //       styles={{marginBottom: "30px"}}
    //       onChange={setTermsAndPolicyDetails}
    //     />

    //     <CitizenConsentForm
    //       styles={{}}
    //       t={t}
    //       isCheckBoxChecked={setTermsAndPolicyDetails}
    //       labels={isCCFEnabled?.checkBoxLabels}
    //       mdmsConfig={mdmsConfig}
    //       setMdmsConfig={setMdmsConfig}
    //     />
    //   </div>)}
    //   <div className="col col-md-4  text-md-center p-0" style={{width:"40%", marginTop:"5px"}}>
    //          <button className="digilocker-btn"type="submit" onClick={(e)=> register(e)}><img src="https://meripehchaan.gov.in/assets/img/icon/digi.png" class="mr-2" style={{"width":"12%"}}></img>Register with DigiLocker</button>
    //             </div>
    // </FormStep>
    <React.Fragment>
      <Card>

        <CardHeader variant="body2">
          {isOtpLogin ? t("Login with OTP") : t("Login with Username & Password")}
        </CardHeader>
        <div>
          <div onClick={toggleLoginMethod} style={{ cursor: 'pointer', fontSize: '24px' }}>
            <i className={isOtpLogin ? 'fa-solid fa-toggle-on' : 'fa-solid fa-toggle-on fa-flip-horizontal'}></i>
          </div>
          <span style={{ marginLeft: "10px" }}>{isOtpLogin ? 'Click for Login with Username & Password' : 'Click for Login with OTP'}</span>
        </div>
          {isOtpLogin ? (
            <div>
              <label>{t("Mobile_No")}</label>
              <TextInput
                required
                id="mobile"
                name="mobile"
                label={t("Mobile Number")}
                variant="outlined"
                value={formValues.mobile}
                onChange={handleChange}
                fullWidth
                margin="normal"
              />
              {!isOtpSent ? (
                <ButtonSelector type="button" onSubmit={handleSendOtp} style={{ marginLeft: "10px", marginBottom: "10px" }} label=  {t("Send OTP")} />

              ) : (
                <div>
                  <label>{t("OTP")}</label>
                  <OTPInput length={6} onChange={handleChangeOTP} value={formValues.otp} />
                </div>
              )}
            </div>
          ) : (
            <div>
              <label>{t("User_Name")}</label>
              <TextInput
                required
                id="username"
                name="username"
                label={t("Username")}
                variant="outlined"
                value={formValues.username}
                onChange={handleChange}
                fullWidth
                margin="normal"
              />
              <label>{t("Password")}</label>
              <TextInput
                required
                id="password"
                name="password"
                label={t("Password")}
                type="password"
                variant="outlined"
                value={formValues.password}
                onChange={handleChange}
                fullWidth
                margin="normal"
              />
            </div>
          )}
          <ButtonSelector type="button" onSubmit={(e) =>handleSubmit(e) }style={{marginLeft: "0px" }} label= {isOtpLogin ? 'Login with OTP' : 'Login with Username & Password'} />
           
          <ButtonSelector  type="button" size="small" onSubmit={() => window.location.href= window.location.href.split("login")[0] + "register/name"} label="Register" />

      </Card>
      </React.Fragment>
  );
};

export default SelectMobileNumber;
