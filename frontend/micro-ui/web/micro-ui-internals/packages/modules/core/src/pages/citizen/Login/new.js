import React, { useState, useEffect } from "react";
import { CardText,Card, CardHeader,FormStep, CitizenConsentForm, Loader, CheckBox,ButtonSelector,TextInput ,OTPInput} from "@demodigit/digit-ui-react-components";
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
  const [isOtpLogin, setIsOtpLogin] = useState(true);
  const [isOtpSent, setIsOtpSent] = useState(false);
  const [formValues, setFormValues] = useState({
    username: '',
    password: '',
    mobile: '',
    otp: ''
  });
  const isMobile = window.Digit.Utils.browser.isMobile();
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
          {data?.link && <span id={data?.linkId} onClick={(e) => { onLinkClick(e) }} style={{ color: "#0a97d5", cursor: "pointer" }}>{t(`${data?.link}_`)}</span>}
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

    <React.Fragment>
      <style>
        {
          `.loginHomePage{
         
          }
          .card .card-header {
            font-size : 28px;
            text-align:center
          }
       
          .employee-card-input {
            width: 100%;
            height:2.5rem !important
}
          `
        }
      </style>
      <div className="loginHomePage" style={{ marginTop:"-40px",height:"100%",backgroundImage:"url(https://chstage.blob.core.windows.net/assets/tmp/Untitled-design.png)",backgroundSize:"cover",backgroundSize:"100% 100%"}}>
        {/* <div style={{textAlign:"center",fontSize:isMobile? "2.2rem":"3rem", fontWeight:"bold",letterSpacing:"1px",color:"white", paddingTop:isMobile?"7%":"5%",margin:isMobile? "5%": ""}}>{t("Welcome to Panchayati Raj & Drinking Water Department ")}</div> */}
        <div style={{display:"flex", justifyContent:isMobile?"":"right", height:isMobile?"":"80%",paddingRight:isMobile?"":"50px", alignItems:isMobile?"center":"center",justifyContent:isMobile?"center":"",flexDirection:isMobile?"":"row-reverse"}}>
      <Card style={{width:isMobile?"":"25%",borderRadius:"5%",minWidth:"380px",padding:"20px 20px",minWidth:"420px"}}>
        <div style={{display:"flex",padding:"15px",justifyContent:"center"}}>
          <div style={{width:"50%"}}>
      <img className="city" src="https://i.postimg.cc/gc4FYkqX/977a9096-3548-4980-aae8-45a6e4d61263-removalai-preview.png" alt="City Logo" />
      </div>
      <div>
    <span style={{ display: "flex", alignItems: "center", justifyContent: "space-between", width: "100%",letterSpacing:"1px" }}>
      <span style={{fontWeight:"bold", display:"flex", flexDirection:"column", marginLeft:"20px", color:"#0a97d5", fontSize:"20px"}} className="logoText">
      Panchayati Raj & Drinking Water Department <span>Government of Gujrat</span>
        {/* <span style={{fontWeight:"normal", color:"black", fontSize:"18px",display:"flex",flexDirection:"column"}} className="logoTextSubline"> MINISTRY OF <span style={{fontWeight:"bold"}}>EXTERNAL AFFAIRS</span></span> */}
      </span>
      </span>
      </div>
      </div>
        <CardHeader variant="body2" style={{fontSize:"28px",justifyContent:"center",textAlign:"center"}}>
          {t("Login")}
        </CardHeader>
        <div style={{display:"flex"}}>
          {/* <div onClick={toggleLoginMethod} style={{ cursor: 'pointer', fontSize: '24px' }}>
            <i className={isOtpLogin ? 'fa-solid fa-toggle-on' : 'fa-solid fa-toggle-on fa-flip-horizontal'}></i>
          </div> */}
          {/* <div style={{ display: "flex",flexWrap:"wrap",justifyContent:"center" }}>
          <span style={{ marginLeft: "10px" }}>{isOtpLogin ? t("Login with Username & Password") : t("Login with OTP")}</span>
          </div> */}
        </div>
          {isOtpLogin ? (
            <div style={{marginBottom:"10px"}}>
              <label style={{color:"#ff9800"}}>{t("Mobile No")}</label>
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
                style={{borderRadius:"10px",marginTop:"5px",backgroundColor: "white",color:"#08519c",height:"2.5rem !important",fontWeight:"bold"}}
              />
              {!isOtpSent ? (
                <span onClick={handleSendOtp} style={{color:"#ff9800",cursor:"pointer"}}>Send OTP</span>
                

              ) : (
                <div>
                  <label>{t("OTP")}</label>
                  <OTPInput length={6} onChange={handleChangeOTP} value={formValues.otp} style={{color:"#08519c",fontWeight:"bold"}}/>
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
                placeholder={t("Enter Username")}
                style={{borderRadius:"10px",marginTop:"5px",backgroundColor: "#0a97d5",color:"white",height:"2.5rem !important"}}
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
                placeholder={t("Enter Password")}
                style={{borderRadius:"10px",marginTop:"5px",backgroundColor: "#0a97d5",color:"white",height:"2.5rem !important"}}
              />
            </div>
          )}
          <div>
          <ButtonSelector type="button" onSubmit={(e) =>handleSubmit(e) }style={{marginLeft: "0px", width:"100%",height:"2.5rem", borderRadius:"10px",backgroundColor:"#ebf1fb !important",color:"white",marginTop:"10px" }} label= {t("Login")}  isDisabled ={isOtpLogin? !isOtpSent ?true : false:false}/>
           <div style={{display:"flex",flexDirection:"column",alignItems:"center",marginTop:"10px",lineHeight:"40px"}}>
          {/* <span  onClick={() => window.location.href= window.location.href.split("login")[0] + "register/name"} style={{marginLeft: "10px",cursor:"pointer", color:"#0a97d5" }}><span>{t("New User ?")}</span> {t("Register Here")}</span>
          <span onClick={() => window.location.href= window.location.href.split("login")[0] + "complaints"}  style={{marginLeft: "10px", cursor:"pointer", color:"#0a97d5" }}>{t("Check Incident Status")}</span>
         */}
         </div>
          </div>
      </Card>
      </div>
      </div>
      </React.Fragment>
  );
};

export default SelectMobileNumber;
