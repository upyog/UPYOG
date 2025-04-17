import React, { useState, Fragment, useEffect } from "react";
import { ButtonSelector, CardText, FormStep, LinkButton, OTPInput, CardLabelError } from "@nudmcdgnpm/digit-ui-react-components";
import useInterval from "../../../hooks/useInterval";

const SelectOtp = ({ config, otp, onOtpChange, onResend, onSelect, t, error, userType = "citizen", canSubmit }) => {
  const [timeLeft, setTimeLeft] = useState(30);
  const TYPE_REGISTER = { type: "register" };
  const TYPE_LOGIN = { type: "login" };
  const [errorRegister, setErrorRegister]= useState(false)
  const getUserType = () => Digit.UserService.getType();
  let newData={}
  useInterval(
    () => {
      setTimeLeft(timeLeft - 1);
    },
    timeLeft > 0 ? 1000 : null
  );
  useEffect(async ()=>{
    //sessionStorage.setItem("DigiLocker.token1","cf87055822e4aa49b0ba74778518dc400a0277e5")
  if(window.location.href.includes("code"))
  {
    let code =window.location.href.split("=")[1].split("&")[0]
    let TokenReq = {
      code_verifier: sessionStorage.getItem("code_verfier_register"),
      code: code, module: "REGISTER",
      redirect_uri: "https://upyog.niua.org/cnd-ui/citizen/login/otp",
    }
    console.log("token",code,TokenReq,sessionStorage.getItem("code_verfier_register"))
    const data = await Digit.DigiLockerService.token({TokenReq })
    registerUser(data)
    
  }
  else if (window.location.href.includes("error="))
  {
    window.location.href = window.location.href.split("/otp")[0]
  }
  },[])
  const registerUser = async (response) => {
    const data = {
      dob: response?.TokenRes?.dob.substring(0, 2) +"/"+response?.TokenRes?.dob.substring(2,4)+"/"+response?.TokenRes?.dob.substring(4, 8),
      mobileNumber: response?.TokenRes?.mobile,
      name: response?.TokenRes?.name,
      tenantId: "pg",
      userType: getUserType(),
    };
      sessionStorage.setItem("userName",response?.TokenRes?.mobile)
      const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_REGISTER } });
      if (!err) {
        history.replace(`${path}/otp`, { from: getFromLocation(location.state, searchParams) });
        return;
      }
      else {
        const [res, err] = await sendOtp({ otp: { ...data, ...TYPE_LOGIN } })
        sessionStorage.setItem("userName",response?.TokenRes?.mobile)
      }
  }; 
  const handleResendOtp = () => {
    onResend();
    setTimeLeft(2);
  };
  const sendOtp = async (data) => {
    try {
      const res = await Digit.UserService.sendOtp(data, "pg");
      return [res, null];
    } catch (err) {
      return [null, err];
    }
  };
  if (userType === "employee") {
    return (
      <Fragment>
        <OTPInput length={6} onChange={onOtpChange} value={otp} />
        {timeLeft > 0 ? (
          <CardText>{`${t("CS_RESEND_ANOTHER_OTP")} ${timeLeft} ${t("CS_RESEND_SECONDS")}`}</CardText>
        ) : (
          <p className="card-text-button" onClick={handleResendOtp}>
            {t("CS_RESEND_OTP")}
          </p>
        )}
        {!error && <CardLabelError>{t("CS_INVALID_OTP")}</CardLabelError>}
      </Fragment>
    );
  }

  return (
    <FormStep onSelect={onSelect} config={config} t={t} isDisabled={!(otp?.length === 6 && canSubmit)}>
      <OTPInput length={6} onChange={onOtpChange} value={otp} />
      {timeLeft > 0 ? (
        <CardText>{`${t("CS_RESEND_ANOTHER_OTP")} ${timeLeft} ${t("CS_RESEND_SECONDS")}`}</CardText>
      ) : (
        <p className="card-text-button" onClick={handleResendOtp}>
          {t("CS_RESEND_OTP")}
        </p>
      )}
      {!error && <CardLabelError>{t("CS_INVALID_OTP")}</CardLabelError>}
      {errorRegister && <CardLabelError>{t("CS_ALREADY_REGISTERED")}</CardLabelError>}
    </FormStep>
  );
};

export default SelectOtp;
