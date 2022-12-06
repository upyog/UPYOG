import React, { useState,useEffect } from "react";
import { CardLabel, DatePicker,Dropdown,TextInput, TextArea,LabelFieldPair  } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectLicenseePlaceActivity = ({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  const onSkip = () => onSelect();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const [OwnerAadharNo, setOwnerAadharNo] = useState(formData.address?.OwnerAadharNo);
  const [OwnerName, setOwnerName] = useState(formData.address?.OwnerName);
  const [OwnerMobileNo, setOwnerMobileNo] = useState(formData.address?.OwnerMobileNo);
  const [OwnerAddress, setOwnerAddress] = useState(formData.address?.OwnerAddress);
  const [OwnerConsentPlace, setOwnerConsentPlace] = useState(formData.address?.OwnerConsentPlace);
  const [OwnerConsentDateStart, setOwnerConsentDateStart] = useState(formData?.address?.OwnerConsentDateStart);
  const [OwnerConsentDateEnd, setOwnerConsentDateEnd] = useState(formData?.address?.OwnerConsentDateEnd);
  const [isInitialRenderRadio, setIsInitialRenderRadio] = useState(true);
  const [value2, setValue2] = useState();
  const [OwnProperty, setOwnProperty] = useState(formData?.address?.OwnProperty);
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  const menu = [
    { i18nKey: "TL_COMMON_YES", code: "YES" },
    { i18nKey: "TL_COMMON_NO", code: "NO" },
  ];
  function setSelectOwnProperty(value) {
    setOwnProperty(value);
    setValue2(value.code);
    setIsInitialRenderRadio(true);
  }
  function setSelectOwnerAadharNo(e) {
    setOwnerAadharNo(e.target.value);
  }
  function setSelectOwnerName(e) {
    setOwnerName(e.target.value);
  }
  function setSelectOwnerMobileNo(e) {
    setOwnerMobileNo(e.target.value);
  }
  function setSelectOwnerAddress(e) {
    setOwnerAddress(e.target.value);
  }
  function setSelectOwnerConsentPlace(e) {
    setOwnerConsentPlace(e.target.value);
  }
  function selectOwnerConsentDateStart(value) {
    setOwnerConsentDateStart(value);
  }
  function selectOwnerConsentDateEnd(value) {
    setOwnerConsentDateEnd(value);
  }
  useEffect(() => {
    
    if (isInitialRenderRadio) {
      if(OwnProperty){
        setIsInitialRenderRadio(false);
        setValue2(OwnProperty.code);
      }
    }
  }, [isInitialRenderRadio]);
  function goNext() {
    sessionStorage.setItem("OwnProperty", OwnProperty.code);
    sessionStorage.setItem("OwnerAadharNo", OwnerAadharNo);
    sessionStorage.setItem("OwnerName", OwnerName);
    sessionStorage.setItem("OwnerMobileNo", OwnerMobileNo);   
    sessionStorage.setItem("OwnerAddress", OwnerAddress);
    sessionStorage.setItem("OwnerConsentPlace", OwnerConsentPlace);
    sessionStorage.setItem("OwnerConsentDateStart", OwnerConsentDateStart);   
    sessionStorage.setItem("OwnerConsentDateEnd", OwnerConsentDateEnd);   
    onSelect(config.key, { OwnProperty,OwnerAadharNo,OwnerName,OwnerMobileNo,OwnerAddress,OwnerConsentPlace,OwnerConsentDateStart,OwnerConsentDateEnd });   
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline currentStep={2} /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!OwnProperty} >
    <LabelFieldPair style={{ display: "flex" }}><CardLabel>{`${t("TL_PLACE_MSG")}`}</CardLabel>
      <RadioButtons t={t} optionsKey="i18nKey" isMandatory={config.isMandatory} options={menu} selectedOption={OwnProperty} onSelect={setSelectOwnProperty} style={{ marginTop:"-8px",paddingLeft:"5px" ,height:"25px"}} /> 
    </LabelFieldPair>
      {value2 === "NO" && (
      <div>
        <div className="row">    
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_OWNER_DETAILS_HEADER")}*`}</span> </h1>
          </div>        
        </div>
        <div className="row">
          <div className="col-md-4" ><CardLabel>{`${t("TL_OWNER_AADHAR_NO")}`}</CardLabel>
            <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="OwnerAadharNo" value={OwnerAadharNo} onChange={setSelectOwnerAadharNo}   disable={isEdit}   {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true,maxLength:12,minLength:12, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
          </div>
          <div className="col-md-4" ><CardLabel>{`${t("TL_OWNER_NAME")}`}</CardLabel>
            <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="OwnerName" value={OwnerName} onChange={setSelectOwnerName}   disable={isEdit}   {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
          </div>
          <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_MOBILE_NO")}`}</CardLabel> 
          <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="OwnerMobileNo" value={OwnerMobileNo} onChange={setSelectOwnerMobileNo} disable={isEdit} placeholder={`${t("TL_LOCALIZATION_MOBILE_NO")}`} {...(validation = { pattern: "[6-9]{1}[0-9]{9}",type: "tel", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
          </div>        
        </div>
        <div className="row">
          <div className="col-md-12" ><CardLabel>{`${t("TL_OWNER_ADDRESS")}`}</CardLabel>
            <TextArea t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="OwnerAddress" value={OwnerAddress} onChange={setSelectOwnerAddress} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
          </div>
        </div>
        <div className="row">
          <div className="col-md-4" ><CardLabel>{`${t("TL_OWNER_CONSENT_PLACE")}`}</CardLabel> 
          <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="OwnerConsentPlace" value={OwnerConsentPlace} onChange={setSelectOwnerConsentPlace} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
          </div>
          <div className="col-md-4" ><CardLabel>{t("TL_OWNER_CONSENT_DATE_START")}</CardLabel>
            <DatePicker date={OwnerConsentDateStart} name="OwnerConsentDateStart" onChange={selectOwnerConsentDateStart} disabled={isEdit} />
          </div>
          <div className="col-md-4" ><CardLabel>{t("TL_OWNER_CONSENT_DATE_END")}</CardLabel>
            <DatePicker date={OwnerConsentDateEnd} name="OwnerConsentDateEnd" onChange={selectOwnerConsentDateEnd} disabled={isEdit} />
          </div>
        </div>
      </div>)}
    </FormStep>
    </React.Fragment>
  );
};
export default SelectLicenseePlaceActivity;
