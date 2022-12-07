import React, { useState } from "react";
import { FormStep,CardLabel, TextInput,Dropdown,DatePicker } from "@egovernments/digit-ui-react-components";
import Timeline from "../../components/CRTimeline";
import { useTranslation } from "react-i18next";

const HospitalDetails = ({ config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  let validation = {};
  // const { data: place = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "PlaceOfActivity");
  const {data: Menu} = Digit.Hooks.cr.useCRGenderMDMS(stateId, "common-masters", "HospitalName","SignedOfficerName", "SignedOfficerDesignation");
  // const {data: Menu} = Digit.Hooks.cr.useCRGenderMDMS(stateId, "common-masters", "SignedOfficerName");
  // const {data: Menu} = Digit.Hooks.cr.useCRGenderMDMS(stateId, "common-masters", "SignedOfficerDesignation");
  const [HospitalName, selectHospitalName] = useState(formData?.HospitalDetails?.HospitalName);
  const [SignedOfficerName, selectSignedOfficerName] = useState(formData?.HospitalDetails?.SignedOfficerName);
  const [SignedOfficerDesignation, selectSignedOfficerDesignation] = useState(formData?.HospitalDetails?.SignedOfficerDesignation);
  const [SignedOfficerAadharNo, setSignedOfficerAadharNo] = useState(formData?.HospitalDetails?.SignedOfficerAadharNo);
  const [SignedOfficerMobileNo, setSignedOfficerMobileNo] = useState(formData?.HospitalDetails?.SignedOfficerMobileNo);
  // const [setPlaceofActivity, setSelectedPlaceofActivity] = useState(formData?.TradeDetails?.setPlaceofActivity);
  // const isEdit = window.location.href.includes("/edit-application/")||window.location.href.includes("renew-trade");
  // const [TradeName, setTradeName] = useState(null);
  // const [CommencementDate, setCommencementDate] = useState();
  let menu = [];
    Menu &&
    Menu.map((ge) => {
      menu.push({ i18nKey: `CR_HOSPITAL_${hospitalDetails.code}`, code: `${hospitalDetails.code}`, value: `${hospitalDetails.code}` });
      menu.push({ i18nKey: `CR_SIGNED_OFFICER_${SignedOfficerNameDetails.code}`, code: `${SignedOfficerNameDetails.code}`, value: `${SignedOfficerNameDetails.code}` });
      menu.push({ i18nKey: `CR_SIGNED_OFFICER_DESIGNATION_${SignedOfficerDesignationDetails.code}`, code: `${SignedOfficerDesignationDetails.code}`, value: `${SignedOfficerDesignationDetails.code}` });
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
  // function selectPlaceofactivity(value) {
  //   naturetypecmbvalue=value.code.substring(0, 4);
  //   setSelectedPlaceofActivity(value);    
  // }
  
  // function setSelectTradeName(e) {
  //   setTradeName(e.target.value);
  // }
  // function selectCommencementDate(value) {
  //   setCommencementDate(value);
  // }
  const goNext = () => {
    sessionStorage.setItem("HospitalName","SignedOfficerName" ,"SignedOfficerDesignation","SignedOfficerAadharNo","SignedOfficerMobileNo",setHospitalName.code,setSignedOfficerName.code,setSignedOfficerDesignation.code,setSignedOfficerAadharNo.code,setSignedOfficerMobileNo.code);
    onSelect(config.key, { HospitalName,SignedOfficerName,SignedOfficerDesignation,SignedOfficerAadharNo,SignedOfficerMobileNo });
}
 
  // const goNext = () => {
  //   sessionStorage.setItem("PlaceOfActivity", setPlaceofActivity.code);   
  //   onSelect(config.key, { setPlaceofActivity });
  // }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!CommencementDate}>
    <header className="card-header" style={{fontSize:"35px"}}>Hospital Details</header>    
    <div className="row">    
    <div className="col-md-6" >
        <CardLabel>{`${t("CR_HOSPITAL")}`}</CardLabel>
            <Dropdown
                t={t}
                optionKey="code"
                isMandatory={false}
                option={menu}
                selected={HospitalName}
                select={setselectHospitalName}
                disabled={isEdit}
                />
        </div> 
        
        <div className="col-md-6" >
        <CardLabel>{`${t("CR_SIGNED_OFFICER")}`}</CardLabel>
            <Dropdown
                t={t}
                optionKey="code"
                isMandatory={false}
                option={menu}
                selected={SignedOfficerName}
                select={setselectSignedOfficerName}
                disabled={isEdit}
                />
        </div>        
    </div>
    <div className="row">    
    <div className="col-md-4" >
        <CardLabel>{`${t("CR_SIGNED_OFFICER_DESIGNATION")}`}</CardLabel>
            <Dropdown
                t={t}
                optionKey="code"
                isMandatory={false}
                option={menu}
                selected={SignedOfficerDesignation}
                select={setselectSignedOfficerDesignation}
                disabled={isEdit}
                />
        </div>       
        <div className="col-md-4" >
            <CardLabel>{`${t("CS_COMMON_AADHAAR")}`}</CardLabel>
            <TextInput       
            t={t}
            isMandatory={false}
            type={"text"}
            optionKey="i18nKey"
            name="SignedOfficerAadharNo"
            value={SignedOfficerAadharNo}
            onChange={setSelectSignedOfficerAadharNo}
            disable={isEdit}
            {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />
        </div>

        <div className="col-md-4">
            <CardLabel>{`${t("CR_MOBILE_NO")}`}</CardLabel>
            <TextInput       
            t={t}
            isMandatory={false}
            type={"text"}
            optionKey="i18nKey"
            name="SignedOfficerMobileNo"
            value={SignedOfficerMobileNo}
            onChange={setSelectSignedOfficerMobileNo}
            disable={isEdit}
            {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />  
        </div>
    </div>     
      
    </FormStep>
    </React.Fragment>
  );
};
export default HospitalDetails;