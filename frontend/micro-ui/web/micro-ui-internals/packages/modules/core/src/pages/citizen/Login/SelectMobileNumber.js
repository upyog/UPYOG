import React, { useState, useEffect } from "react";
import { CardText,Card, CardHeader,FormStep, CitizenConsentForm, Loader, CheckBox,ButtonSelector,TextInput ,OTPInput} from "@upyog/digit-ui-react-components";
import { Link } from "react-router-dom";
const TYPE_LOGIN = { type: "login" };
const SelectMobileNumber = ({ t, onSelect, showRegisterLink, mobileNumber, onMobileChange, config, canSubmit }) => {

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
          {/* <ButtonSelector type="button" onSubmit={(e) => }style={{marginLeft: "0px" }} label= {isOtpLogin ? 'Login with OTP' : 'Login with Username & Password'} /> */}
           
          <ButtonSelector  type="button" size="small" onSubmit={() => window.location.href= window.location.href.split("login")[0] + "register/name"} label="Register" />

      </Card>
      </React.Fragment>
  );
};

export default SelectMobileNumber;
