import { CardLabel, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useState } from "react";
import { useHistory } from "react-router-dom";
import Timeline from "../components/bmcTimeline";
import aadhaarData from "./aadhaarData.json";

const AadhaarVerification = ({ t, setError: setFormError, clearErrors: clearFormErrors, onBlur }) => {
  const [aadhaar, setAadhaar] = useState(Array(12).fill(""));
  const [error, setError] = useState("");
  const [isAadhaarValid, setIsAadhaarValid] = useState(false);
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
      setMessage("");
      setButtonText("Submit");

      if (e.target.value && value && index < aadhaar.length - 1) {
        document.getElementById(`aadhaar-${index + 1}`).focus();
      }
    } else {
      setError("Aadhaar number should contain only 12 digits");
    }
  };

  const handleKeyDownAadhaar = (e, index) => {
    if (e.key === "Backspace" && !aadhaar[index] && index > 0) {
      document.getElementById(`aadhaar-${index - 1}`).focus();
    }
  };

  const validateAadhaar = () => {
    if (aadhaar.every((digit) => digit !== "")) {
      setError("");
      setIsAadhaarValid(true);
      setButtonText("Submit");
    } else {
      setError("Aadhaar number should contain only 12 digits");
      setIsAadhaarValid(false);
      setMessage("");
    }
  };

  const handleSubmit = () => {
    const aadhaarString = aadhaar.join("");
    if (buttonText === "Submit" && isAadhaarValid && aadhaarString.length === 12) {
      validateAadhaar();
    } else if (buttonText === "Submit") {
      if (aadhaarString === aadhaarData.aadhaarNumber) {
        history.push({
          pathname: "/digit-ui/citizen/bmc/aadhaarForm",
          state: { aadhaarInfo: aadhaarData.aadhaarInfo },
        });
      } else {
        setError("Invalid Aadhaar number");
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
                      <React.Fragment>
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
                        {(index === 3 || index === 7) && <span className="aadhaar-dash">-</span>}
                      </React.Fragment>
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
