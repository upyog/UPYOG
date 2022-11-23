import React, { useState,useEffect } from "react";
import { CardLabel, DatePicker,Dropdown,TextInput, TextArea } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectLicenseePlaceActivity = ({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  const onSkip = () => onSelect();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const [BlockNo, setBlockno] = useState(formData.TradeDetails?.BlockNo);
  const [SurveyNo, setSurveyNo] = useState(formData.TradeDetails?.SurveyNo);
  const [SubDivNo, setSubDivNo] = useState(formData.TradeDetails?.SubDivNo);
  const [value2, setValue2] = useState();
  const [LicenseeType, setLicenseeType] = useState(formData?.TradeDetails?.LicenseeType);
  const [CommencementDate, setCommencementDate] = useState(formData?.TradeDetails?.CommencementDate);
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  const menu = [
    { i18nKey: "TL_COMMON_YES", code: "YES" },
    { i18nKey: "TL_COMMON_NO", code: "NO" },
  ];
  function setSelectBlockNo(e) {
    setBlockno(e.target.value);
  }
  function setSelectSurveyNo(e) {
    setSurveyNo(e.target.value);
  }
  function setSelectSubDivNo(e) {
    setSubDivNo(e.target.value);
  }
  function selectLicenseeType(value) {
    setLicenseeType(value);
    setValue2(value.code);
  }
  function selectCommencementDate(value) {
    setCommencementDate(value);
  }
  function goNext() {
    sessionStorage.setItem("BlockNo", BlockNo);
    sessionStorage.setItem("SurveyNo", SurveyNo);
    sessionStorage.setItem("SubDivNo", SubDivNo);   
    onSelect(config.key, { BlockNo,SurveyNo,SubDivNo });   
    // onSelect(config.key, { BlockNo });       
    // onSelect(config.key, { SurveyNo });    
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!BlockNo} >
    
          <CardLabel>{`${t("TL_PLACE_MSG")}`}</CardLabel>
            <RadioButtons
              t={t}
              optionsKey="i18nKey"
              isMandatory={config.isMandatory}
              options={menu}
              selectedOption={LicenseeType}
              onSelect={selectLicenseeType}
              // checked={value2 === "si"}
              // onChange={LicenseeType}
              disabled={isEdit}
            /> 
      {value2 === "NO" && (
      <div>
        <div className="row">    
          <div className="col-md-12" > 
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_OWNER_DETAILS_HEADER")}*`}</span>
              </h1>
          </div>        
        </div>
        <div className="row">
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_OWNER_AADHAR_NO")}`}</CardLabel>
            <TextInput
                t={t}
                isMandatory={false}
                type={"text"}
                optionKey="i18nKey"
                name="SubDivNo"
                value={SubDivNo}
                onChange={setSelectSubDivNo}
                disable={isEdit}
                {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })}
            />
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_OWNER_NAME")}`}</CardLabel>
            <TextInput
                t={t}
                isMandatory={false}
                type={"text"}
                optionKey="i18nKey"
                name="SubDivNo"
                value={SubDivNo}
                onChange={setSelectSubDivNo}
                disable={isEdit}
                {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })}
            />
          </div>
          <div className="col-md-4" >
              <CardLabel>{`${t("TL_LOCALIZATION_MOBILE_NO")}`}</CardLabel>
              <TextInput
                  t={t}
                  isMandatory={false}
                  type={"text"}
                  optionKey="i18nKey"
                  name="SubDivNo"
                  value={SubDivNo}
                  onChange={setSelectSubDivNo}
                  disable={isEdit}
                  {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })}
              />
          </div>        
        </div>
        <div className="row">
          <div className="col-md-12" >
            <CardLabel>{`${t("TL_OWNER_ADDRESS")}`}</CardLabel>
            <TextArea
                t={t}
                isMandatory={false}
                type={"text"}
                optionKey="i18nKey"
                name="SubDivNo"
                value={SubDivNo}
                onChange={setSelectSubDivNo}
                disable={isEdit}
                {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })}
            />
          </div>
        </div>
        <div className="row">
          <div className="col-md-6" >
              <CardLabel>{`${t("TL_OWNER_CONSENT_PLACE")}`}</CardLabel>
              <TextInput
                  t={t}
                  isMandatory={false}
                  type={"text"}
                  optionKey="i18nKey"
                  name="SubDivNo"
                  value={SubDivNo}
                  onChange={setSelectSubDivNo}
                  disable={isEdit}
                  {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })}
              />
          </div>
        </div>
        <div className="row">
          <div className="col-md-6" >
            <CardLabel>{t("TL_OWNER_CONSENT_DATE_START")}</CardLabel>
            <DatePicker date={CommencementDate} name="CommencementDate" onChange={selectCommencementDate} disabled={isEdit} />
          </div>
          <div className="col-md-6" >
            <CardLabel>{t("TL_OWNER_CONSENT_DATE_END")}</CardLabel>
            <DatePicker date={CommencementDate} name="CommencementDate" onChange={selectCommencementDate} disabled={isEdit} />
          </div>
        </div>
      </div>)}
    </FormStep>
    </React.Fragment>
  );
};
export default SelectLicenseePlaceActivity;
