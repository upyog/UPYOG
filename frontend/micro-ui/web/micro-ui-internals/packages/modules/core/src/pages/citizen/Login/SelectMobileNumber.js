import React, { useState, useRef } from "react";
import { img } from "./img";
import { useHistory } from "react-router-dom";
// import CityPicker if available
// import CityPicker from "egov-ui-kit/common/common/CityPicker";
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
const SelectMobileNumber = ({ submitFormComplete }) => {
  const [mobile, setMobile] = useState("");
  const [name, setName] = useState("");
  const [city, setCity] = useState("");
  const [otp, setOtp] = useState(Array(6).fill(""));
  const [otpSent, setOtpSent] = useState(false);
  const [registerMode, setRegisterMode] = useState(false);
  const [otpError, setOtpError] = useState(false);
  const [verifying, setVerifying] = useState(false);
  const [success, setSuccess] = useState(false);
  const history = useHistory();
  const otpInputsRef = useRef([...Array(6)].map(() => React.createRef()));

  const handleSendOtpNew = async () => {
    if (!/^[6-9]\d{9}$/.test(mobile)) {
      alert("Please enter a valid 10-digit mobile number.");
      return;
    }
    if (registerMode && (!name || !city)) {
      alert("Please enter Name and select a City.");
      return;
    }
  
    setOtpSent(false);
    setOtpError(false);
  
    const data = { mobileNumber: mobile, tenantId: "pg", userType: "Citizen" };
    const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_LOGIN } });
  
    if (err) {
      console.error(err);
      setOtpError(true);
      alert("Failed to send OTP. Please try again.");
      return;
    }
  
    setOtpSent(true);
    setOtp(Array(6).fill(""));
  };
  const sendOtp = async (data) => {
    try {
      const res = await Digit.UserService.sendOtp(data, "pg");
      return [res, null];
    } catch (err) {
      return [null, err];
    }
  };
  
  // Helper to verify OTP
  const verifyOtp = async ({ mobile, otp }) => {
    try {
      const requestData = {
        username: mobile,
        password: otp,
        tenantId: "pg",
        userType: "CITIZEN",
        otp: "Y",
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
      return [response, null];
    } catch (err) {
      return [null, err];
    }
  };

  const handleOtpChange = (index, value) => {
    if (!/^\d?$/.test(value)) return;
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);
    if (value && index < 5) {
      otpInputsRef.current[index + 1].current.focus();
    }
  };

  const handleOtpKeyDown = (index, e) => {
    if (e.key === "Backspace" && !otp[index] && index > 0) {
      otpInputsRef.current[index - 1].current.focus();
    }
  };

  const handleVerifyOtp = async () => {
    setVerifying(true);
    setOtpError(false);
  
    const otpValue = otp.join("");
    const [res, err] = await verifyOtp({ mobile, otp: otpValue });
  
    if (err || !res) {
      setOtpError(true);
      setVerifying(false);
      return;
    }
  
    // On success: show success screen and call parent callback
    setVerifying(false);
    setSuccess(true);
    //history.replace("/digit-ui/citizen/home");
  
   
  };
  

  const handleBack = () => {
    setOtpSent(false);
    setOtp(Array(6).fill(""));
    setSuccess(false);
    setOtpError(false);
  };

  return (
    <main style={{ display: "flex", flexDirection: window.innerWidth < 992 ? "column" : "row" }}>
      {/* Banner */}
      <style>
        {
          `
          body {
            margin: 0;
            font-family: 'Inter', sans-serif;
            color: #0e2a3b;
            background: #f9fafc;
        }
        
        header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 28px;
            background: #fff;
            box-shadow: 0 4px 8px rgba(0, 60, 130, 0.2);
        }
        
        .brand {
            display: flex;
            align-items: left;
            gap: 12px;
        }
        
        .brand img {
            height: 60px;
            background: #fff;
            padding: 4px;
            border-radius: 50%;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.12);
        }
        
        .brand-text h1 {
            margin: 0;
            font-size: 24px;
            font-weight: 700;
            color: #1f4f7b;
        }
        
        .brand-text small {
            font-size: 13px;
            color: #6b7b8a;
        }
        
        .lang span {
            margin: 0 4px;
            padding: 4px 10px;
            border: 1px solid #1f4f7b;
            border-radius: 12px;
            cursor: pointer;
            font-size: 13px;
            transition: background 0.2s, color 0.2s;
        }
        
        .lang span:hover {
            background: #1f4f7b;
            color: #fff;
        }
        
        main {
          display: grid;
          grid-template-columns: 1fr 0.6fr;
          gap: 24px;
          padding: 30px;
          margin: auto;
          align-items: stretch;  /* ensures children stretch equally */
        }
        
        .banner {
            background: #fff;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            display: flex;
            flex-direction: column;
            justify-content: center;
            height:100% !important;
        }
        .banner, .login-box {
          display: flex;
          flex-direction: column;
          justify-content: center;
          background: #fff;
          border-radius: 12px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
          height: 100%;       /* take full equal height */
        }
        .banner img {
            width: 100%;
            height: auto;       /* keeps aspect ratio */
            max-height: 300px;  /* optional: prevent banner from being too tall on mobile */
            object-fit: cover;  /* crops instead of squishing */
            border-radius: 10px;
          }
          
        
        .banner h2 {
            margin: 20px auto 8px;
            font-size: 32px;
            font-weight: 900;
            text-align: center;
            line-height: 1.3;
            letter-spacing: 0.5px;
            position: relative;
            max-width: 95%;
            background: linear-gradient(90deg, #1f4f7b, #1dd1a1);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            animation: fadeUp 1.2s ease-in-out;
        }
        
        @keyframes fadeUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
        
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .banner h2::after {
            content: "";
            display: block;
            width: 0;
            height: 4px;
            margin: 12px auto 0;
            background: linear-gradient(90deg, #1f4f7b, #1dd1a1);
            border-radius: 2px;
            animation: grow 1.2s ease forwards;
        }
        
        @keyframes grow {
            from {
                width: 0;
            }
        
            to {
                width: 100px;
            }
        }
        
        .banner p {
            text-align: center;
            font-size: 15px;
            color: #6c7a89;
            margin: 0 0 16px;
        }
        
        .login-box {
            background: #fff;
            border-radius: 14px;
            padding: 32px 28px;
            box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
            text-align: center;
            border-top: 5px solid #1f4f7b;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            display: flex;
            flex-direction: column;
            justify-content: center;
            width: 100%;
            max-width: 100%;
            min-height: 520px;
            box-sizing: border-box;
        }
        
        .login-box h2 {
            font-size: 20px;
            font-weight: 700;
            color: #1f4f7b;
            margin-bottom: 6px;
            position: relative;
        }
        
        .login-box h2::after {
            content: "";
            display: block;
            width: 40px;
            height: 3px;
            margin: 10px auto 12px;
            background: #1f4f7b;
            border-radius: 2px;
        }
        
        .login-box small {
            color: #6c7a89;
            display: block;
            margin-bottom: 18px;
        }
        
        .form-group {
            margin-bottom: 24px;
            width: 100%;
            max-width: 280px;
            margin-left: auto;
            margin-right: auto;
            text-align: left;
            position: relative;
        }
        
        label {
            font-size: 13px;
            display: block;
            margin-bottom: 5px;
        }
        
        input {
            width: 100%;
            padding: 15px;
            border: 1px solid #c9d3e0;
            border-radius: 6px;
            font-size: 16px;
            transition: all 0.2s;
            display: block;
            box-sizing: border-box;
        }
        
        input:focus {
            border-color: #1f4f7b;
            outline: none;
            box-shadow: 0 0 0 3px rgba(31, 79, 123, 0.15);
            background: #f0f6fb;
        }
        
        .otp-container {
            display: flex;
            justify-content: space-between;
            gap: 8px;
            max-width: 280px;
            margin: auto;
        }
        
        .otp-input {
            width: 40px;
            text-align: center;
            font-size: 18px;
            padding: 10px;
            border: 1px solid #c9d3e0;
            border-radius: 6px;
            transition: all 0.2s;
        }
        
        .otp-input:focus {
            border-color: #1f4f7b;
            outline: none;
            box-shadow: 0 0 0 3px rgba(31, 79, 123, 0.15);
            background: #f0f6fb;
        }
        
        .btn-primary {
            width: 100%;
            padding: 15px;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            font-weight: 600;
            background: #1f4f7b;
            color: #fff;
            cursor: pointer;
            transition: all 0.3s ease;
            display: block;
            box-sizing: border-box;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
        }
        
        .btn-primary:active {
            transform: scale(0.97);
        }
        
        .btn-outline {
            width: 100%;
            padding: 15px;
            border: 2px solid #1f4f7b;
            border-radius: 6px;
            font-size: 16px;
            font-weight: 600;
            background: #fff;
            color: #1f4f7b;
            cursor: pointer;
            transition: all 0.3s ease;
            display: block;
            box-sizing: border-box;
        }
        
        .btn-outline:hover {
            background: #1f4f7b;
            color: #fff;
        }
        
        .links {
            display: flex;
            flex-direction: row-reverse;
            justify-content: space-between;
            width: 100%;
            max-width: 280px;
            margin: 0 auto;
            font-size: 13px;
            margin-top: 16px;
            box-sizing: border-box;
            cursor: pointer;
        }
        
        .links a {
            color: #1f4f7b !important;
            text-decoration: none;
            transition: color 0.2s;
        }
        
        .links a:hover {
            color: #1dd1a1;
        }
        
        footer {
            background: #102b3f;
            color: #d4deea;
            padding: 16px 24px;
            font-size: 12px;
            border-top: 4px solid #1f4f7b;
            text-align: center;
            margin-top: 40px;
        }
        
        footer .nav-links {
            margin-bottom: 8px;
        }
        
        footer a {
            color: #d4deea;
            text-decoration: none;
            margin: 0 6px;
        }
        
        /* Error and Success Styling */
        .error-glow {
            border: 2px solid #e63946;
            box-shadow: 0 0 6px rgba(230, 57, 70, 0.8);
        }
        
        .success-glow {
            border: 2px solid #2ecc71 !important;
            animation: successPulse 0.6s ease forwards;
        }
        
        @keyframes successPulse {
            0% {
                transform: scale(1);
                box-shadow: 0 0 6px rgba(46, 204, 113, 0.6);
            }
        
            50% {
                transform: scale(1.05);
                box-shadow: 0 0 14px rgba(46, 204, 113, 0.9);
            }
        
            100% {
                transform: scale(1);
                box-shadow: 0 0 6px rgba(46, 204, 113, 0.6);
            }
        }
        
        .shake {
            animation: shake 0.6s;
        }
        
        @keyframes shake {
        
            0%,
            100% {
                transform: translateX(0);
            }
        
            25% {
                transform: translateX(-5px);
            }
        
            50% {
                transform: translateX(5px);
            }
        
            75% {
                transform: translateX(-5px);
            }
        }
        
        .error-message {
            color: #e63946;
            font-size: 13px;
            margin-top: 6px;
            text-align: center;
            font-weight: 500;
        }
        
        /* Spinner */
        .spinner {
            width: 28px;
            height: 28px;
            border: 3px solid #cce8dd;
            border-top: 3px solid #2ecc71;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin: auto;
        }
        
        @keyframes spin {
            0% {
                transform: rotate(0deg);
            }
        
            100% {
                transform: rotate(360deg);
            }
        }
        
        /* Success checkmark */
        .success-checkmark {
            width: 80px;
            height: 115px;
            margin: 0 auto;
        }
        
        .success-checkmark .check-icon {
            width: 80px;
            height: 80px;
            position: relative;
            border-radius: 50%;
            box-sizing: content-box;
            border: 4px solid #2ecc71;
        }
        
        .success-checkmark .icon-line {
            height: 5px;
            background-color: #2ecc71;
            display: block;
            border-radius: 2px;
            position: absolute;
            z-index: 10;
        }
        
        .success-checkmark .line-tip {
            top: 46px;
            left: 14px;
            width: 25px;
            transform: rotate(45deg);
            animation: lineTip 0.4s ease forwards;
        }
        
        .success-checkmark .line-long {
            top: 38px;
            right: 8px;
            width: 47px;
            transform: rotate(-45deg);
            animation: lineLong 0.4s ease forwards;
        }
        
        @keyframes lineTip {
            0% {
                width: 0;
            }
        
            100% {
                width: 25px;
            }
        }
        
        @keyframes lineLong {
            0% {
                width: 0;
            }
        
            100% {
                width: 47px;
            }
        }
        
        /* Skip button */
        #skipRedirect {
            margin-top: 12px;
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            background: #2ecc71;
            color: #fff;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        #skipRedirect:hover {
            background: #27ae60;
            transform: translateY(-2px);
        }
        
        #skipRedirect:active {
            transform: scale(0.97);
        }
        .city-picker-wrapper input#person-city {
            width: 100% !important;
            padding: 15px !important;
            border: 1px solid #c9d3e0 !important;
            border-radius: 6px !important;
            font-size: 16px !important;
            transition: all 0.2s !important;
            display: block !important;
            box-sizing: border-box !important;
            height: auto !important;
            margin-top: 0px !important;
          }
          
          /* Optional: hover/focus states */
          .city-picker-wrapper input#person-city:focus {
            border-color: #1f4f7b !important;
            outline: none !important;
          }
        @media (max-width:992px) {
            main {
                grid-template-columns: 1fr;
                padding: 20px;
            }
        
            .banner {
                order: 2;
            }
        
            .login-box {
                order: 1;
            }
        }
        
        @media (max-width:480px) {
            .otp-input {
                width: 36px;
                padding: 10px;
                font-size: 18px;
            }
        
            button,
            input {
                padding: 18px;
                font-size: 17px;
            }
        }
        main {
          display: grid;
          grid-template-columns: 1fr 0.6fr;
          gap: 24px;
          padding: 30px;
          margin: auto;
          height: calc(100% - 40px);
          align-items: stretch; /* ensures children stretch equally */
        }
        
        .banner,
        .login-box {
          display: flex;
          flex-direction: column;
          justify-content: center;
          background: #fff;
          border-radius: 12px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
          height: 100%; /* equal height in grid layout */
        }
        
        /* On mobile, let them auto-size naturally */
        @media (max-width: 992px) {
          main {
            grid-template-columns: 1fr;
            align-items: start; /* don't force equal heights */
          }
          .banner,
          .login-box {
            height: auto; /* natural flow */
          }
        }
        
        @media (max-width: 480px) {
            .banner img {
              max-height: 200px; /* smaller on very small devices */
            }
          }
          
          `
        }
      </style>
      <div className="banner" style={{ width: window.innerWidth < 992 ? "100%" : "180%" }}>
        <img src={img} alt="Gujrat City Banner" />
        <h2>
          {otpSent
            ? "One-Click Login with OTP"
            : registerMode
            ? "Register New User"
            : "One Unified Platform for Urban Governance & Citizen Services"}
        </h2>
        <p>
          {otpSent
            ? "Efficiency Meets Security"
            : "Delivering transparent, efficient and citizen-friendly urban services across Gujrat."}
        </p>
      </div>

      {/* Login/Register Box */}
      <div className="login-box">
        {!success ? (
          <React.Fragment>
            {/* Register Mode Fields */}
            <h2>
                                    {otpSent
                                        ? "Enter OTP to Continue"
                                        : registerMode
                                            ? "REGISTER TO SWAGAT"
                                            : "LOGIN TO SWAGAT"}
                                </h2>
                                <small>
                                    {otpSent
                                        ? "An OTP has been sent to your mobile number"
                                        : registerMode
                                            ? "Fill in details to create a new account"
                                            : "Access citizen services securely"}
                                </small>
            {!otpSent && registerMode && (
              <React.Fragment>
                <div className="form-group">
                  <label htmlFor="mobile">Mobile Number</label>
                  <input
                    id="mobile"
                    type="tel"
                    value={mobile}
                    onChange={(e) => setMobile(e.target.value)}
                    placeholder="Enter Mobile Number"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="name">Full Name</label>
                  <input
                    id="name"
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Enter Full Name"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="city">Select City</label>
                  {/* Replace with <CityPicker /> if needed */}
                  <input
                    id="city"
                    type="text"
                    value={city}
                    onChange={(e) => setCity(e.target.value)}
                    placeholder="Enter City"
                  />
                </div>

                <div className="form-group">
                  <button type="button" className="btn-outline" onClick={handleSendOtpNew}>
                    Send OTP & Register
                  </button>
                </div>
                </React.Fragment>
            )}

            {/* Login Mode Mobile Field */}
            {!otpSent && !registerMode && (
              <div className="form-group">
                <label htmlFor="mobile">Mobile Number</label>
                <input
                  id="mobile"
                  type="tel"
                  value={mobile}
                  onChange={(e) => setMobile(e.target.value)}
                  placeholder="Enter Mobile Number"
                  style={{ marginBottom: "20px" }}
                />
                <button type="button" className="btn-outline" onClick={handleSendOtpNew}>
                  Get OTP
                </button>
              </div>
            )}

            {/* OTP Section */}
            {otpSent && (
              <React.Fragment>
                <div className="form-group">
                  <label>Enter OTP to continue</label>
                  <div className="otp-container">
                    {otp.map((digit, index) => (
                      <input
                        key={index}
                        type="text"
                        value={digit}
                        maxLength="1"
                        className={`otp-input ${otpError ? "error-glow" : ""}`}
                        onChange={(e) => handleOtpChange(index, e.target.value)}
                        onKeyDown={(e) => handleOtpKeyDown(index, e)}
                        ref={otpInputsRef.current[index]}
                      />
                    ))}
                  </div>
                  {otpError && <div className="error-message">Incorrect OTP. Please try again.</div>}
                </div>

                {verifying && (
                  <div id="verifying" style={{ marginTop: "15px" }}>
                    <div className="spinner"></div>
                    <div>Verifying...</div>
                  </div>
                )}

                <div className="form-group">
                  <button type="button" className="btn-primary" onClick={handleVerifyOtp} disabled={verifying}>
                    {registerMode ? "Register & Login" : "Login"}
                  </button>
                </div>

                <div className="form-group" style={{ marginTop: "12px" }}>
                  <button className="btn-outline" onClick={handleBack} style={{ width: "100%" }}>
                    Back
                  </button>
                </div>
                </React.Fragment>
            )}

            {/* Links */}
            {!otpSent && (
              <div className="links">
                {!registerMode ? (
                  <a onClick={() => setRegisterMode(true)}>Register New User</a>
                ) : (
                  <button className="btn-outline" onClick={() => setRegisterMode(false)}>
                    Back to Login
                  </button>
                )}
              </div>
            )}
          </React.Fragment>
        ) : (
          <div id="successScreen">
            <div className="success-checkmark">
              <div className="check-icon">
                <span className="icon-line line-tip"></span>
                <span className="icon-line line-long"></span>
              </div>
            </div>
            <h3 style={{ color: "#2ecc71" }}>Login Successful</h3>
            <button id="skipRedirect">Go to Dashboard Now</button>
          </div>
        )}
      </div>
    </main>
  );
};

export default SelectMobileNumber;
