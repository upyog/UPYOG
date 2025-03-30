import { BackButton, Dropdown, FormComposer, Loader, Toast } from "@upyog/digit-ui-react-components";
import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import Background from "../../../components/Background";
import Header from "../../../components/Header";
import CryptoJS from "crypto-js";

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

const Login = ({ config: propsConfig, t, isDisabled }) => {
  const { data: cities, isLoading } = Digit.Hooks.useTenants();
  const { data: storeData, isLoading: isStoreLoading } = Digit.Hooks.useStore.getInitData();
  const { stateInfo } = storeData || {};
  const [user, setUser] = useState(null);
  const [showToast, setShowToast] = useState(null);
  const [disable, setDisable] = useState(false);
  const [captcha, setCaptcha] = useState([]);
  const [secretKey, setSecretKey] = useState(process.env.REACT_APP_SECRET_KEY);
  // const [config, setConfig] = useState([]);

  const history = useHistory();
  // const getUserType = () => "EMPLOYEE" || Digit.UserService.getType();
  let   sourceUrl = "https://s3.ap-south-1.amazonaws.com/egov-qa-assets";
  const pdfUrl = "https://pg-egov-assets.s3.ap-south-1.amazonaws.com/Upyog+Code+and+Copyright+License_v1.pdf";
  const [userId, password, city, captchaTxt,captchaField] = propsConfig.inputs;

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
        // res?.captcha?.captcha+process.env.REACT_APP_SECRET_KEY.subString(6)
          setSecretKey((prev)=>res?.captcha?.captcha+prev.slice(6))
        // }
        // loadForm()

      }
      // return [res, null];
    } catch (err) {
      return [null, err];
    }
  }
  useEffect(()=>{
    // console.log("captcha----",captcha)
  },[captcha])
  useEffect(async () => {
    if (!user) {
      return;
    }
    Digit.SessionStorage.set("citizen.userRequestObject", user);
    const filteredRoles = user?.info?.roles?.filter((role) => role.tenantId === Digit.SessionStorage.get("Employee.tenantId"));
    if (user?.info?.roles?.length > 0) user.info.roles = filteredRoles;
    Digit.UserService.setUser(user);
    // Digit.UserService.generateCaptcha({})
    
    setEmployeeDetail(user?.info, user?.access_token);
    let redirectPath = "/digit-ui/employee";

    /* logic to redirect back to same screen where we left off  */
    if (window?.location?.href?.includes("from=")) {
      redirectPath = decodeURIComponent(window?.location?.href?.split("from=")?.[1]) || "/digit-ui/employee";
    }

    /*  RAIN-6489 Logic to navigate to National DSS home incase user has only one role [NATADMIN]*/
    if (user?.info?.roles && user?.info?.roles?.length > 0 &&  user?.info?.roles?.every((e) => e.code === "NATADMIN")) {
      redirectPath = "/digit-ui/employee/dss/landing/NURT_DASHBOARD";
    }
    /*  RAIN-6489 Logic to navigate to National DSS home incase user has only one role [NATADMIN]*/
    if (user?.info?.roles && user?.info?.roles?.length > 0 && user?.info?.roles?.every((e) => e.code === "STADMIN")) {
      redirectPath = "/digit-ui/employee/dss/landing/home";
    }

    history.replace(redirectPath);
  }, [user]);
//   const secretKey = '1234567890123456'
//   //  process.env.REACT_APP_SECRET_KEY; // Store in environment variables

// const encryptPassword = (password) => {
//   console.log("secretKey==",secretKey)
//   return CryptoJS.AES.encrypt(password, secretKey).toString();
// };

// const secretKey = "1234567890123456"
// // CryptoJS.enc.Utf8.parse("1234567890123456"); // 16-byte key

// const encryptPassword = (password) => {
//   const iv = CryptoJS.enc.Base64.parse('1234567890abcdef'); 
//   const encrypted = CryptoJS.AES.encrypt(password, secretKey,  { iv: iv });
//   console.log("encrypted===",encrypted)
//   return encrypted.toString(); // Base64 encode before sending
// };
// console.log("captcha[0].captcha+process.env.REACT_APP_SECRET_KEY.subString(6)=",captcha[0]?.captcha+process.env.REACT_APP_SECRET_KEY.subString(6))
// const secretKey = CryptoJS.enc.Utf8.parse(captcha[0].captcha+process.env.REACT_APP_SECRET_KEY.subString(6)); // Ensure 16 bytes key
const generateIV = () => CryptoJS.lib.WordArray.random(16);

const encryptPassword = (plainText) => {
  // console.log("secretKey==",secretKey)
  // let secretKeyN = secretKey.subString(6);
  // secretKeyN = captcha[0].captcha+secretKeyN;
  // console.log("secretKeyN==",secretKeyN)
  let ss = CryptoJS.enc.Utf8.parse(secretKey); // Ensure 16 bytes key

  const iv = generateIV();
  const encrypted = CryptoJS.AES.encrypt(plainText, ss, {
    mode: CryptoJS.mode.CBC, // Matches Java ECB Mode
    padding: CryptoJS.pad.Pkcs7, // Matches Java PKCS5Padding
    iv: iv, 
  });
  return iv.toString(CryptoJS.enc.Base64) + ":" + encrypted.toString();

  // return encrypted.toString(); // Returns Base64 encoded encrypted text
};
// const decryptPassword = (cipherText) => {
//   const bytes = CryptoJS.AES.decrypt(cipherText, secretKey);
//   console.log("bytes==",bytes.toString())
//   return bytes.toString(CryptoJS.enc.Utf8);
// };

const decryptPassword = (encryptedText) => {
  const decrypted = CryptoJS.AES.decrypt(encryptedText, secretKey, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  });

  return decrypted.toString(CryptoJS.enc.Utf8);
};

  const onLogin = async (data) => {
    let encriptedPass = encryptPassword(data?.password)
    data.password = encriptedPass;
    let decriptedPass = decryptPassword(encriptedPass)

    if (!data.city) {
      alert("Please Select City!");
      return;
    }
    if (!data.captcha) {
      alert("Please write captcha");
      return;
    }
    setDisable(true);

    const requestData = {
      ...data,
      userType: "EMPLOYEE",
    };
    requestData.tenantId = data.city.code;
    delete requestData.city;
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
    sessionStorage.getItem("User") && sessionStorage.removeItem("User")
    history.push("/digit-ui/employee/user/forgot-password");
  };
  // const loadForm = () => {
  //   console.log("loadForm==",captcha)
    const config = [
      {
        body: [
          {
            label: t(userId.label),
            type: userId.type,
            populators: {
              name: userId.name,
            },
            isMandatory: true,
          },
          {
            label: t(password.label),
            type: password.type,
            populators: {
              name: password.name,
            },
            isMandatory: true,
          },
          {
            label: t(city.label),
            type: city.type,
            populators: {
              name: city.name,
              customProps: {},
              component: (props, customProps) => (
                <Dropdown
                  option={cities}
                  className="login-city-dd"
                  optionKey="i18nKey"
                  select={(d) => {
                    props.onChange(d);
                  }}
                  t={t}
                  {...customProps}
                />
              ),
            },
            isMandatory: true,
          },
          {
            label: t(captchaTxt.label),
            type: captchaTxt.type,
            populators: {
              name: captchaTxt.name,
            },
            captchaTxt: captcha,
            isMandatory: true,
          },
          {
            label: t(captchaField?.label),
            type: captchaField.type,
            populators: {
              name: captchaField.name,
            },
            isMandatory: true,
          },
          
        ],
      },
    ];
  //   setConfig(config)
  // }

  const onCaptchaRefresh = ()=> {
    fetchCaptcha()
  }
  

  return isLoading || isStoreLoading ? (
    <Loader />
  ) : (
    <Background backgroundClass={'loginBackgroundCls'}>
      <div className="employeeBackbuttonAlign">
        <BackButton variant="white" style={{ borderBottom: "none" }} />
      </div>

      <FormComposer
        onSubmit={onLogin}
        isDisabled={isDisabled || disable}
        noBoxShadow
        inline
        submitInForm
        config={[...config]}
        captchaDetails={[...captcha]}
        onCaptchaRefresh={onCaptchaRefresh}
        label={propsConfig.texts.submitButtonLabel}
        secondaryActionLabel={propsConfig.texts.secondaryButtonLabel}
        onSecondayActionClick={onForgotPassword}
        heading={propsConfig.texts.header}
        headingStyle={{ display: "flex", justifyContent: 'center' }}
        headingLogo={'true'}
        cardStyle={{ margin: "auto", minWidth: "408px" }}
        className="loginFormStyleEmployee"
        formStyle={{position: "absolute",right: "0", top: "-10px"}}
        buttonStyle={{ maxWidth: "100%", width: "100%" ,backgroundColor:"#5a1166"}}
      >
        {/* <Header /> */}
      </FormComposer>
      {showToast && <Toast error={true} label={t(showToast)} onClose={closeToast} />}
      <div style={{ width: '100%', position: 'fixed', bottom: 0,backgroundColor:"white",textAlign:"center" }}>
        <div style={{ display: 'flex', justifyContent: 'center', color:"black" }}>
          {/* <span style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"12px", fontWeight: "400"}} onClick={() => { window.open('https://www.digit.org/', '_blank').focus();}} >Powered by DIGIT</span>
          <span style={{ margin: "0 10px" ,fontSize: window.Digit.Utils.browser.isMobile()?"12px":"12px"}}>|</span>
          <a style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"12px", fontWeight: "400"}} href="#" target='_blank'>UPYOG License</a>

          <span  className="upyog-copyright-footer" style={{ margin: "0 10px",fontSize:"12px" }} >|</span> */}
          <span  className="upyog-copyright-footer" style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"12px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2024 Manipur Municipality Property Tax Board</span>
          
          {/* <a style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} href="#" target='_blank'>UPYOG License</a> */}

        </div>
        <div className="upyog-copyright-footer-web">
          <span className="" style={{ cursor: "pointer", fontSize:  window.Digit.Utils.browser.isMobile()?"14px":"16px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2024 Manipur Municipality Property Tax Board</span>
          </div>
      </div>
    </Background>
  );
};

Login.propTypes = {
  loginParams: PropTypes.any,
};

Login.defaultProps = {
  loginParams: null,
};

export default Login;
