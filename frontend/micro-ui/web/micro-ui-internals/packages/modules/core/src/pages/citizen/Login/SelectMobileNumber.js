import React, { useState, useEffect } from "react";
import { CardText, FormStep, CitizenConsentForm, Loader, CheckBox } from "@egovernments/digit-ui-react-components";
import { Link } from "react-router-dom";

const SelectMobileNumber = ({ t, onSelect, showRegisterLink, mobileNumber, onMobileChange, config, canSubmit }) => {

  const [isCheckBox, setIsCheckBox] = useState(false);
  const [isCCFEnabled, setisCCFEnabled] = useState(false);
  const [mdmsConfig, setMdmsConfig] = useState("");
  const [error, setError]=useState("");
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
  if (isLoading) return <Loader />
  const register = async (e) => {
    const data = await Digit.DigiLockerService.register({ module: "REGISTER" });
    e.preventDefault()
    const redirectUrl = data.redirectURL
    console.log("data", data)
    sessionStorage.setItem("code_verfier_register", data?.codeverifier)
    window.location.href = redirectUrl
  }
  return (
    <FormStep
      isDisabled={checkDisbaled()}
      onSelect={onSelect}
      config={config}
      t={t}
      componentInFront="+91"
      onChange={handleMobileChange}
      value={mobileNumber}
    >
      {error && <p style={{color:"red"}}>{error}</p>}
      {isCCFEnabled?.isCitizenConsentFormEnabled && (
      <div>
        <CheckBox
          className="form-field"
          label={checkLabels()}
          value={isCheckBox}
          checked={isCheckBox}
          style={{ marginTop: "5px", marginLeft: "55px" }}
          styles={{marginBottom: "30px"}}
          onChange={setTermsAndPolicyDetails}
        />

        <CitizenConsentForm
          styles={{}}
          t={t}
          isCheckBoxChecked={setTermsAndPolicyDetails}
          labels={isCCFEnabled?.checkBoxLabels}
          mdmsConfig={mdmsConfig}
          setMdmsConfig={setMdmsConfig}
        />
      </div>)}
      <div className="col col-md-4  text-md-center p-0" style={{width:"40%", marginTop:"5px"}}>
             <button className="digilocker-btn"type="submit" onClick={(e)=> register(e)}><img src="https://meripehchaan.gov.in/assets/img/icon/digi.png" class="mr-2" style={{"width":"12%"}}></img>Register with DigiLocker</button>
                </div>
    </FormStep>
  );
};

export default SelectMobileNumber;
