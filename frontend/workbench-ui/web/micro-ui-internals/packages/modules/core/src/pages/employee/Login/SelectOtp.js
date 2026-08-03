import { CardLabelError, CardText, OTPInput } from "@upyog/workbench-ui-react-components";
import React, { Fragment, useState } from "react";
import useInterval from "../../../hooks/useInterval";

const SelectOtp = ({ otp, onOtpChange, onResend, t, error }) => {
  const [timeLeft, setTimeLeft] = useState(30);

  useInterval(() => { setTimeLeft(timeLeft - 1); }, timeLeft > 0 ? 1000 : null);

  const handleResendOtp = () => {
    onResend();
    setTimeLeft(2);
  };

  return (
    <Fragment>
      <OTPInput length={6} onChange={onOtpChange} value={otp} />
      {timeLeft > 0 ? (
        <CardText>{`${t("CS_RESEND_ANOTHER_OTP")} ${timeLeft} ${t("CS_RESEND_SECONDS")}`}</CardText>
      ) : (
        <p className="card-text-button resend-otp" onClick={handleResendOtp}>{t("CS_RESEND_OTP")}</p>
      )}
      {!error && <CardLabelError>{t("CS_INVALID_OTP")}</CardLabelError>}
    </Fragment>
  );
};

export default SelectOtp;
