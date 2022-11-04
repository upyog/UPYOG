import React, { useState,useEffect } from "react";
import { CardLabel, TypeSelectCard,Dropdown,TextInput } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectLand = ({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  const onSkip = () => onSelect();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const [BlockNo, setBlockno] = useState(formData.TradeDetails?.BlockNo);
  const [SurveyNo, setSurveyNo] = useState(formData.TradeDetails?.SurveyNo);
  const [SubDivNo, setSubDivNo] = useState(formData.TradeDetails?.SubDivNo);
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");

  function setSelectBlockNo(e) {
    setBlockno(e.target.value);
  }
  function setSelectSurveyNo(e) {
    setSurveyNo(e.target.value);
  }
  function setSelectSubDivNo(e) {
    setSubDivNo(e.target.value);
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
            
      <CardLabel>{`${t("TL_LOCALIZATION_BLOCK_NO")}`}</CardLabel>
       <TextInput
          t={t}
          isMandatory={false}
          type={"text"}
          optionKey="i18nKey"
          name="BlockNo"
          value={BlockNo}
          onChange={setSelectBlockNo}
          disable={isEdit}
          {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_BLOCK_NO") })}
        />
        <CardLabel>{`${t("TL_LOCALIZATION_SURVEY_NO")}`}</CardLabel>
        <TextInput
            t={t}
            isMandatory={false}
            type={"text"}
            optionKey="i18nKey"
            name="SurveyNo"
            value={SurveyNo}
            onChange={setSelectSurveyNo}
            disable={isEdit}
            {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SURVEY_NO") })}
            />
        <CardLabel>{`${t("TL_LOCALIZATION_SUBDIVISION_NO")}`}</CardLabel>
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
    </FormStep>
    </React.Fragment>
  );
};
export default SelectLand;
