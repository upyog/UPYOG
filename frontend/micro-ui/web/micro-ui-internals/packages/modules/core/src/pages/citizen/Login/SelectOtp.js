import React, { useState, Fragment } from "react";
import { ButtonSelector, CardText, FormStep, LinkButton, OTPInput, CardLabelError } from "@upyog/digit-ui-react-components";
import useInterval from "../../../hooks/useInterval";

const SelectOtp = ({ config, otp, onOtpChange, onResend, onSelect, t, error, userType = "citizen", canSubmit,captchaDetails,onCaptchaRefresh, onCaptchaChange }) => {
  const [timeLeft, setTimeLeft] = useState(60);
  const [captchaField, setCaptchaField] = useState('');

  useInterval(
    () => {
      setTimeLeft(timeLeft - 1);
    },
    timeLeft > 0 ? 1000 : null
  );

  const handleResendOtp = () => {
    onResend();
    setTimeLeft(60);
  };

  if (userType === "employee") {
    return (
      <Fragment>
        <OTPInput length={6} onChange={onOtpChange} value={otp} />
        {timeLeft > 0 ? (
          <CardText>{`${t("CS_RESEND_ANOTHER_OTP")} ${timeLeft} ${t("CS_RESEND_SECONDS")}`}</CardText>
        ) : (
          <p className="card-text-button" onClick={handleResendOtp} style={{cursor:'pointer'}}>
            {t("CS_RESEND_OTP")}
          </p>
        )}
        {!error && <CardLabelError>{t("CS_INVALID_OTP")}</CardLabelError>}
      </Fragment>
    );
  }
  
  const onChangeCaptcha = (e)=>{
    onCaptchaChange(e.target.value)
    setCaptchaField(e.target.value)
  }

  return (
    <FormStep onSelect={onSelect} config={config} t={t} isDisabled={!(otp?.length === 6 && captchaField)}>
      <OTPInput length={6} onChange={onOtpChange} value={otp} />
      {timeLeft > 0 ? (
        <CardText>{`${t("CS_RESEND_ANOTHER_OTP")} ${timeLeft} ${t("CS_RESEND_SECONDS")}`}</CardText>
      ) : (
        <p className="card-text-button" style={{cursor:'pointer'}} onClick={handleResendOtp}>
          {t("CS_RESEND_OTP")}
        </p>
      )}
      {!error && <CardLabelError>{t("CS_INVALID_OTP")}</CardLabelError>}
      <div>
        <label>
          Captcha
        </label>
        <div style={{background: 'white', fontStyle: 'italic', padding: '5px', display: 'flex',justifyContent:'space-between', borderRadius: '4px', marginBottom: '15px'}}><span style={{fontSize: '20px',background: '#529ec029'}}>{captchaDetails[0]?.captcha}</span> <span style={{padding: '3px', background: 'gray', float: 'right', color: 'white', borderRadius: '4px', cursor: 'pointer'}} onClick={onCaptchaRefresh}>Refresh</span> </div>
        <div>
          <input type="text" className="employee-card-input" placeholder="Enter the above captcha" onChange={onChangeCaptcha} required/>
        </div>
      </div>
      
    </FormStep>
  );
};

export default SelectOtp;
