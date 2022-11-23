import React, { useState,useEffect } from "react";
import { CardLabel, TypeSelectCard,Dropdown,TextInput, TextArea } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectLicensee = ({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  const onSkip = () => onSelect();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const { data: place = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "PlaceOfActivity");
  const { data: boundaryList = {}, isLoading } = Digit.Hooks.tl.useTradeLicenseMDMS("kl.cochin", "cochin/egov-location", "boundary-data");
  const [value2, setValue2] = useState();
  const [LicenseeType, setLicenseeType] = useState(formData?.TradeDetails?.LicenseeType);
  const [LicensingUnitType, setLicensingUnitType] = useState(formData?.TradeDetails?.LicensingUnitType);
  const [Zonal, setZonal] = useState();
  const [WardNo, setWardNo] = useState();
  const [wards, setFilterWard] = useState(0);
  const [isInitialRender, setIsInitialRender] = useState(true);
  const [BlockNo, setBlockno] = useState(formData.TradeDetails?.BlockNo);
  const [SurveyNo, setSurveyNo] = useState(formData.TradeDetails?.SurveyNo);
  const [SubDivNo, setSubDivNo] = useState(formData.TradeDetails?.SubDivNo);
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");

  const menu = [
    { i18nKey: "TL_COMMON_INDIVIDUAL", code: "INDIVIDUAL" },
    { i18nKey: "TL_COMMON_INSTITUTION", code: "INSTITUTION" },
  ];
  let cmbPlace = [];
  place &&
  place["TradeLicense"] &&
  place["TradeLicense"].PlaceOfActivity.map((ob) => {
        cmbPlace.push(ob);
    });
  let cmbZonal = [];
  boundaryList &&
  boundaryList["egov-location"] &&
  boundaryList["egov-location"].TenantBoundary.map((ob) => {    
    cmbZonal.push(ob.boundary.children);
  });
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
  function selectLicensingUnitType(value) {
    setLicensingUnitType(value);
    
  }  
  function setSelectZonalOffice(e) {
    setIsInitialRender(true);
    setZonal(e);
    setWardNo(null);
    setFilterWard(null);
  }
  function setSelectWard(e) {
    setWardNo(e);
  }
  React.useEffect(() => {
    
    if (isInitialRender) {
      if(Zonal){
        setIsInitialRender(false);
        setFilterWard(Zonal.children)
      }
    }
  }, [wards,isInitialRender]);
 console.log(value2);
  function goNext() {
    sessionStorage.setItem("ZonalDet", Zonal.name);
    sessionStorage.setItem("WardDet", WardNo.name);
    sessionStorage.setItem("BlockNo", BlockNo);
    sessionStorage.setItem("SurveyNo", SurveyNo);
    sessionStorage.setItem("SubDivNo", SubDivNo);   
    onSelect(config.key, { Zonal,WardNo,BlockNo,SurveyNo,SubDivNo });   
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!BlockNo} >
    
          <CardLabel>{`${t("TL_LICENSEE_MSG")}`}</CardLabel>
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
      {value2 === "INDIVIDUAL" && (
      <div>
        <div className="row">    
          <div className="col-md-12" > 
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_LICENSEE_INDIVIDUAL_HEADER")}*`}</span>
              </h1>
          </div>        
        </div>
        <div className="row">
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LICENSING_UNIT_TYPE")}`}</CardLabel>
            <Dropdown
              t={t}
              optionKey="code"
              isMandatory={config.isMandatory}
              option={cmbPlace}
              selected={LicensingUnitType}
              select={selectLicensingUnitType}
              disabled={isEdit}
            />
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LICENSING_UNIT_ID")}`}</CardLabel>
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
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LICENSING_UNIT_NAME")}`}</CardLabel>
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
          </div>
        </div>
        <div className="row">
          <div className="col-md-4" > 
            <CardLabel>{`${t("TL_LOCALIZATION_ZONAL_OFFICE")}`}</CardLabel>
            <Dropdown
              t={t}
              optionKey="name"
              isMandatory={config.isMandatory}
              option={cmbZonal[0]}
              selected={setZonal}
              select={setSelectZonalOffice}
              disabled={isEdit}
              {...(validation = { isRequired: true, title: t("TL_INVALID_ZONAL_NAME") })}
            />
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LOCALIZATION_WARD_NO")}`}</CardLabel>
            <Dropdown
              t={t}
              optionKey="name"
              isMandatory={config.isMandatory}
              option={wards}
              selected={setWardNo}
              select={setSelectWard}
              disabled={isEdit}
              {...(validation = { isRequired: true, title: t("TL_INVALID_WARD_NO") })}
            />
          </div>
          <div className="col-md-4" >           
            <CardLabel>{`${t("TL_STREET_NAME")}`}</CardLabel>
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
          </div>
        </div>
        <div className="row">
          <div className="col-md-12" > 
          <CardLabel>{`${t("TL_LOCALIZATION_LAND_MARK")}`}</CardLabel>
          <TextArea
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
          </div>
        </div>
        <div className="row">
          <div className="col-md-6" >
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
          <div className="col-md-6" >
            <CardLabel>{`${t("TL_LOCALIZATION_EMAIL_ID")}`}</CardLabel>
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
        </div> )}

        {value2 === "INSTITUTION" && (
      <div>
        <div className="row">    
          <div className="col-md-12" > 
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_LICENSING_INSTITUTION_HEADER")}*`}</span>
              </h1>
          </div>        
        </div>
        <div className="row">
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LICENSING_INSTITUTION_TYPE")}`}</CardLabel>
            <Dropdown
              t={t}
              optionKey="code"
              isMandatory={config.isMandatory}
              option={cmbPlace}
              selected={LicensingUnitType}
              select={selectLicensingUnitType}
              disabled={isEdit}
            />
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LICENSING_INSTITUTION_ID")}`}</CardLabel>
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
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LICENSING_INSTITUTION_NAME")}`}</CardLabel>
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
          </div>
        </div>
        <div className="row">
          <div className="col-md-12" > 
          <CardLabel>{`${t("TL_LICENSING_INSTITUTION_ADDRESS")}`}</CardLabel>
          <TextArea
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
          </div>
        </div>
        <div className="row">
          <div className="col-md-6" >
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
          <div className="col-md-6" >
            <CardLabel>{`${t("TL_LOCALIZATION_EMAIL_ID")}`}</CardLabel>
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
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_LICENSING_UNIT_HEADER")}*`}</span>
              </h1>
          </div>        
        </div>
        <div className="row">
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LICENSING_UNIT_TYPE")}`}</CardLabel>
            <Dropdown
              t={t}
              optionKey="code"
              isMandatory={config.isMandatory}
              option={cmbPlace}
              selected={LicensingUnitType}
              select={selectLicensingUnitType}
              disabled={isEdit}
            />
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LICENSING_UNIT_ID")}`}</CardLabel>
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
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LICENSING_UNIT_NAME")}`}</CardLabel>
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
          </div>
        </div>
        <div className="row">
          <div className="col-md-4" > 
            <CardLabel>{`${t("TL_LOCALIZATION_ZONAL_OFFICE")}`}</CardLabel>
            <Dropdown
              t={t}
              optionKey="name"
              isMandatory={config.isMandatory}
              option={cmbZonal[0]}
              selected={setZonal}
              select={setSelectZonalOffice}
              disabled={isEdit}
              {...(validation = { isRequired: true, title: t("TL_INVALID_ZONAL_NAME") })}
            />
          </div>
          <div className="col-md-4" >
            <CardLabel>{`${t("TL_LOCALIZATION_WARD_NO")}`}</CardLabel>
            <Dropdown
              t={t}
              optionKey="name"
              isMandatory={config.isMandatory}
              option={wards}
              selected={setWardNo}
              select={setSelectWard}
              disabled={isEdit}
              {...(validation = { isRequired: true, title: t("TL_INVALID_WARD_NO") })}
            />
          </div>
          <div className="col-md-4" >           
            <CardLabel>{`${t("TL_STREET_NAME")}`}</CardLabel>
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
          </div>
        </div>
        <div className="row">
          <div className="col-md-12" > 
          <CardLabel>{`${t("TL_LOCALIZATION_LAND_MARK")}`}</CardLabel>
          <TextArea
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
          </div>
        </div>
        <div className="row">
          <div className="col-md-6" >
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
          <div className="col-md-6" >
            <CardLabel>{`${t("TL_LOCALIZATION_EMAIL_ID")}`}</CardLabel>
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
        </div> )}
    </FormStep>
    </React.Fragment>
  );
};
export default SelectLicensee;
