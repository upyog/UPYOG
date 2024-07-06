import { CardLabel, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useState } from "react";
import { useHistory } from "react-router-dom";
import Timeline from "../components/bmcTimeline";
import aadhaarData from "./aadhaarData.json";

const AadhaarVerification = ({ t, setError: setFormError, clearErrors: clearFormErrors, onBlur }) => {
  const [aadhaar, setAadhaar] = useState(Array(12).fill(""));
  const [otp, setOtp] = useState(Array(6).fill(""));
  const [error, setError] = useState("");
  const [isAadhaarValid, setIsAadhaarValid] = useState(false);
  const [isOtpEnabled, setIsOtpEnabled] = useState(false);
  const [message, setMessage] = useState("");
  const [buttonText, setButtonText] = useState("Submit");
  const history = useHistory();

  const handleAadhaarChange = (e, index) => {
    const value = e.target.value;
    if (/^\d{0,1}$/.test(value)) {
      const newAadhaar = [...aadhaar];
      newAadhaar[index] = value;
      setAadhaar(newAadhaar);
      if (newAadhaar.every((digit) => digit !== "")) {
        setError("");
      }
      setIsAadhaarValid(false);
      setIsOtpEnabled(false);
      setMessage("");
      setButtonText("Submit");

      if (e.target.value && value && index < aadhaar.length - 1) {
        document.getElementById(`aadhaar-${index + 1}`).focus();
      }
    } else {
      setError("Aadhaar number should contain only 12 digits");
    }
  };

  const handleOtpChange = (e, index) => {
    const value = e.target.value;
    if (/^\d{0,1}$/.test(value)) {
      const newOtp = [...otp];
      newOtp[index] = value;
      setOtp(newOtp);
      if (newOtp.every((digit) => digit !== "")) {
        setError("");
      }

      if (e.target.value && value && index < otp.length - 1) {
        document.getElementById(`otp-${index + 1}`).focus();
      }
    } else {
      setError("OTP should contain only 6 digits");
    }
  };

  const handleKeyDownAadhaar = (e, index) => {
    if (e.key === "Backspace" && !aadhaar[index] && index > 0) {
      document.getElementById(`aadhaar-${index - 1}`).focus();
    }
  };

  const handleKeyDownOtp = (e, index) => {
    if (e.key === "Backspace" && !otp[index] && index > 0) {
      document.getElementById(`otp-${index - 1}`).focus();
    }
  };

  const validateAadhaar = () => {
    if (aadhaar.every((digit) => digit !== "")) {
      setError("");
      setIsAadhaarValid(true);
      setIsOtpEnabled(true);
      setButtonText("Verify");
    } else {
      setError("Aadhaar number should contain only 12 digits");
      setIsAadhaarValid(false);
      setIsOtpEnabled(false);
      setMessage("");
      setButtonText("Submit");
    }
  };

  const handleSubmit = () => {
    const aadhaarString = aadhaar.join("");
    const otpString = otp.join("");
    if (buttonText === "Submit") {
      validateAadhaar();
    } else if (buttonText === "Verify" && isAadhaarValid && otpString.length === 6) {
      if (aadhaarString === aadhaarData.aadhaarNumber && otpString === aadhaarData.otp) {
        history.push({
          pathname: "/digit-ui/citizen/bmc/aadhaarForm",
          state: { aadhaarInfo: aadhaarData.aadhaarInfo },
        });
      } else {
        setError("Invalid Aadhaar number or OTP");
        setIsOtpEnabled(false);
      }
    }
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}
        <div className="bmc-row-card-header" style={{ padding: "0" }}>
          <div className="bmc-card-row" style={{ height: "100%" }}>
            <div className="bmc-col2-card" style={{ height: "55vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
              <div className="bmc-aadhaarText" style={{ width: "60%" }}>
                <div className="bmc-title" style={{ textAlign: "center" }}>
                  Aadhaar Verification
                </div>
               
                <LabelFieldPair>
                  <CardLabel className="aadhaar-label">{"BMC_AADHAAR_LABEL"}</CardLabel>
                  <div className="aadhaar-container">
                    {aadhaar.map((digit, index) => (
                      <TextInput
                        key={index}
                        id={`aadhaar-${index}`}
                        t={t}
                        type="number"
                        isMandatory={false}
                        optionKey="i18nKey"
                        name={`aadhaar-${index}`}
                        onBlur={onBlur}
                        value={digit}
                        onChange={(e) => handleAadhaarChange(e, index)}
                        onKeyDown={(e) => handleKeyDownAadhaar(e, index)}
                        className="aadhaar-input"
                        maxLength={1}
                        validation={{
                          required: true,
                          minLength: 12,
                          maxLength: 12,
                        }}
                      />
                    ))}
                  </div>
                </LabelFieldPair>
                <LabelFieldPair>
                  <CardLabel className="aadhaar-label">{"BMC_OTP_LABEL"}</CardLabel>
                  <div className="otp-container" style={{ width: "60%" }}>
                    {otp.map((digit, index) => (
                      <TextInput
                        key={index}
                        id={`otp-${index}`}
                        t={t}
                        type="number"
                        isMandatory={false}
                        optionKey="i18nKey"
                        name={`otp-${index}`}
                        value={digit}
                        onChange={(e) => handleOtpChange(e, index)}
                        onKeyDown={(e) => handleKeyDownOtp(e, index)}
                        className="otp-input"
                        maxLength={1}
                        disabled={!isOtpEnabled}
                        validation={{
                          required: true,
                          minLength: 6,
                          maxLength: 6,
                        }}
                      />
                    ))}
                  </div>
                </LabelFieldPair>
                {message && (
                  <div style={{ textAlign: "center", color: aadhaar.join("") === aadhaarData.aadhaarNumber ? "green" : "red" }}>{message}</div>
                )}
                {error && <div style={{ textAlign: "center", color: "red" }}>{error}</div>}
                <div style={{ textAlign: "center" }}>
                  <button className="bmc-card-button" onClick={handleSubmit} style={{ borderBottom: "3px solid black", textAlign: "center" }}>
                    {buttonText}
                  </button>
                </div>
              </div>
            </div>
            <div className="bmc-col2-card" style={{ padding: "0" }}>
              <div className="bmc-card-aadharimage"></div>
            </div>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default AadhaarVerification;
