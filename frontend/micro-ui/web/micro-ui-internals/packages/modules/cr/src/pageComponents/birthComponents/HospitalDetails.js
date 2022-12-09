import React, { useState } from "react";
import { FormStep, CardLabel, TextInput, Dropdown, DatePicker } from "@egovernments/digit-ui-react-components";
import Timeline from "../../components/CRTimeline";
import { useTranslation } from "react-i18next";

const HospitalDetails = ({ config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const { t } = useTranslation();
  let validation = {};
  const { data: hospital = {}, isLoading } = Digit.Hooks.cr.useCivilRegistrationMDMS(stateId, "birth-death-service", "hospitalList");
  console.log(hospital);
  const [HospitalName, selectHospitalName] = useState(formData?.HospitalDetails?.HospitalName);
  const [SignedOfficerName, selectSignedOfficerName] = useState(formData?.HospitalDetails?.SignedOfficerName);
  const [SignedOfficerDesignation, selectSignedOfficerDesignation] = useState(formData?.HospitalDetails?.SignedOfficerDesignation);
  const [SignedOfficerAadharNo, setSignedOfficerAadharNo] = useState(formData?.HospitalDetails?.SignedOfficerAadharNo);
  const [SignedOfficerMobileNo, setSignedOfficerMobileNo] = useState(formData?.HospitalDetails?.SignedOfficerMobileNo);
  // const isEdit = window.location.href.includes("/edit-application/")||window.location.href.includes("renew-trade");
  let cmbhospital = [];
  hospital &&
  hospital["birth-death-service"] &&
  hospital["birth-death-service"].hospitalList.map((ob) => {    
    cmbhospital.push(ob);
  });

  const onSkip = () => onSelect();

  function setselectHospitalName(value) {
    selectHospitalName(value);
  }
  function setselectSignedOfficerName(value) {
    selectSignedOfficerName(value);
  }
  function setselectSignedOfficerDesignation(value) {
    selectSignedOfficerDesignation(value);
  }
  function setSelectSignedOfficerAadharNo(e) {
    setSignedOfficerAadharNo(e.target.value);
  }
  function setSelectSignedOfficerMobileNo(e) {
    setSignedOfficerMobileNo(e.target.value);
  }
  const goNext = () => {
    sessionStorage.setItem("HospitalName", HospitalName.hospitalName);
    sessionStorage.setItem("SignedOfficerName", SignedOfficerName.hospitalName);
    sessionStorage.setItem("SignedOfficerDesignation", SignedOfficerDesignation.hospitalName);
    sessionStorage.setItem("SignedOfficerAadharNo", SignedOfficerAadharNo);
    sessionStorage.setItem("SignedOfficerMobileNo", SignedOfficerMobileNo);
    onSelect(config.key, { HospitalName, SignedOfficerName, SignedOfficerDesignation, SignedOfficerAadharNo, SignedOfficerMobileNo });
  }
  return (
    <React.Fragment>
      {window.location.href.includes("/employee") ? <Timeline /> : null}
      <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!HospitalName}>
        <div className="row">
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("CR_Hospital_Details")}`}</span> </h1>
          </div>
        </div>
        <div className="row">
          <div className="col-md-4" ><CardLabel>{`${t("CR_HOSPITAL")}`}</CardLabel>
            <Dropdown t={t} optionKey="hospitalName" isMandatory={false} option={cmbhospital} selected={HospitalName} select={setselectHospitalName} placeholder={`${t("CR_HOSPITAL")}`} />
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("CR_SIGNED_OFFICER")}`}</CardLabel>
            <Dropdown t={t} optionKey="hospitalName" isMandatory={false} option={cmbhospital} selected={SignedOfficerName} select={setselectSignedOfficerName} placeholder={`${t("CR_SIGNED_OFFICER")}`} />
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("CR_SIGNED_OFFICER_DESIGNATION")}`}</CardLabel>
            <Dropdown t={t} optionKey="hospitalName" isMandatory={false} option={cmbhospital} selected={SignedOfficerDesignation} select={setselectSignedOfficerDesignation} placeholder={`${t("CR_SIGNED_OFFICER_DESIGNATION")}`} />
          </div>
        </div>
        <div className="row">          
          <div className="col-md-6" > <CardLabel>{`${t("CS_COMMON_AADHAAR")}`}</CardLabel>
            <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="SignedOfficerAadharNo" value={SignedOfficerAadharNo} onChange={setSelectSignedOfficerAadharNo} placeholder={`${t("CS_COMMON_AADHAAR")}`} {...(validation = { pattern: "^([0-9]){12}$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
          </div>
          <div className="col-md-6"> <CardLabel>{`${t("CR_MOBILE_NO")}`}</CardLabel>
            <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="SignedOfficerMobileNo" value={SignedOfficerMobileNo} onChange={setSelectSignedOfficerMobileNo} placeholder={`${t("CR_MOBILE_NO")}`} {...(validation = { pattern: "^[0-9]{10}$",type: "text", isRequired: true, title: t("CR_INVALID_MOBILE_NO") })} />
          </div>
        </div>

      </FormStep>
    </React.Fragment>
  );
};
export default HospitalDetails;