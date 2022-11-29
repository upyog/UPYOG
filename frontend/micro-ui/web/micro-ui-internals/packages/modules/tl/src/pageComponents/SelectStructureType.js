import React, { useState,useEffect } from "react";
import { CardLabel, TextInput,Dropdown } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";
import SelectLand from "./SelectLand";

const SelectStructureType = ({ t, config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();
  const { data: place = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "PlaceOfActivity");
  const { data: dataitem = {}, isLoading } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "TradeStructureSubtype");
  const [setPlaceofActivity, setSelectedPlaceofActivity] = useState(formData?.TradeDetails?.setPlaceofActivity);
  const [StructureType, setStructureType] = useState(formData?.TradeDetails?.StructureType);
  const [activities, setActivity] = useState(0);
  const [isInitialRender, setIsInitialRender] = useState(true);
  const [value2, setValue2] = useState();
  const [value3, setValue3] = useState();
  const [BlockNo, setBlockno] = useState(formData.TradeDetails?.BlockNo);
  const [SurveyNo, setSurveyNo] = useState(formData.TradeDetails?.SurveyNo);
  const [SubDivNo, setSubDivNo] = useState(formData.TradeDetails?.SubDivNo);
  const { data: boundaryList = {} } = Digit.Hooks.tl.useTradeLicenseMDMS("kl.cochin", "cochin/egov-location", "boundary-data");
  const [Zonal, setZonal] = useState(() => formData?.address?.Zonal || {});
  const [WardNo, setWardNo] = useState(() => formData?.address?.WardNo || {});
  const [DoorNoBuild, setDoorNoBuild] = useState(formData.TradeDetails?.DoorNoBuild);
  const [DoorSubBuild, setDoorSubBuild] = useState(formData.TradeDetails?.DoorSubBuild);
  const [wards, setFilterWard] = useState(0);
  const [LicenseeType, setLicenseeType] = useState(formData?.TradeDetails?.LicenseeType);
  const [VechicleNo, setVechicleNo] = useState(formData.TradeDetails?.VechicleNo);
  const isEdit = window.location.href.includes("/edit-application/")||window.location.href.includes("renew-trade");
  let validation = {};
  let naturetype =null;
  let naturetypecmbvalue =null;
  let routeComponent =null;
  let cmbPlace = [];
  let cmbStructure = [];
  place &&
  place["TradeLicense"] &&
  place["TradeLicense"].PlaceOfActivity.map((ob) => {
        cmbPlace.push(ob);
    });
  dataitem &&
  dataitem["TradeLicense"] &&
    dataitem["TradeLicense"].TradeStructureSubtype.map((ob) => {
      cmbStructure.push(ob);
    });
    let cmbZonal = [];
    boundaryList &&
    boundaryList["egov-location"] &&
    boundaryList["egov-location"].TenantBoundary.map((ob) => {    
      cmbZonal.push(ob.boundary.children);
    });
    const menu = [
      { i18nKey: "TL_COMMON_YES", code: "YES" },
      { i18nKey: "TL_COMMON_NO", code: "NO" },
    ];
   
  // const menu = [
  //   { i18nKey: "TL_COMMON_LAND", code: "LAND" },
  //   { i18nKey: "TL_COMMON_BUILDING", code: "BUILDINg" },
  // ];
  function setSelectZonalOffice(e) {
    setIsInitialRender(true);
    setZonal(e);
    setWardNo(null);
    setFilterWard(null);
  }
  function setSelectWard(e) {
    setWardNo(e);
  }
  function setSelectDoorNoBuild(e) {
    setDoorNoBuild(e.target.value);
  }
  function setSelectDoorSubBuild(e) {
    setDoorSubBuild(e.target.value);
  }
  useEffect(() => {
    
    if (isInitialRender) {
      if(Zonal){
        setIsInitialRender(false);
        setFilterWard(Zonal.children)
      }
    }
  }, [wards,isInitialRender]);

  const onSkip = () => onSelect();

  function selectPlaceofactivity(value) {
    setIsInitialRender(true);
    naturetypecmbvalue=value.code.substring(0, 4);
    setValue2(naturetypecmbvalue);
    setSelectedPlaceofActivity(value);
    setStructureType(null);
    setActivity(null);
  }
  function selectStructuretype(value) {
    setStructureType(value);
    
  }
  function selectLicenseeType(value) {
    setLicenseeType(value);
    setValue3(value.code);
  }
  function setSelectBlockNo(e) {
    setBlockno(e.target.value);
  }
  function setSelectSurveyNo(e) {
    setSurveyNo(e.target.value);
  }
  function setSelectSubDivNo(e) {
    setSubDivNo(e.target.value);
  }
  function setSelectVechicleno(e) {
    setVechicleNo(e.target.value);
  }

  React.useEffect(() => {
    if (isInitialRender) {
      if(setPlaceofActivity){
        setIsInitialRender(false);
        naturetype = setPlaceofActivity.code.substring(0, 4);    
        setActivity(cmbStructure.filter( (cmbStructure) => cmbStructure.maincode.includes(naturetype)));
      }
    }
  }, [activities,isInitialRender]);
 
  function goNext() {
    sessionStorage.setItem("PlaceOfActivity", setPlaceofActivity.code);   
    sessionStorage.setItem("StructureType", StructureType.name);
    onSelect(config.key, { StructureType,setPlaceofActivity });
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!StructureType}>
    <div className="row">    
          <div className="col-md-12" > 
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_PLACE_HEADER_MSG")}*`}</span>
              </h1>
          </div>        
        </div>
        <div className="row">
          <div className="col-md-6" >
            <CardLabel>{`${t("TL_LOCALIZATION_PLACE_ACTVITY")}`}</CardLabel>
            <Dropdown
              t={t}
              optionKey="code"
              isMandatory={config.isMandatory}
              option={cmbPlace}
              selected={setPlaceofActivity}
              select={selectPlaceofactivity}
              disabled={isEdit}
            />
          </div>
          <div className="col-md-6" >
          <CardLabel>{`${t("TL_LOCALIZATION_NATURE_STRUCTURE")}`}</CardLabel>
          <Dropdown
            t={t}
            optionKey="name"
            isMandatory={config.isMandatory}
            option={activities}
            selected={StructureType}
            select={selectStructuretype}
            disabled={isEdit}
          />
          </div>
        </div>
        {value2 === "LAND" && (
        <div>                 
          <div className="row">
            <div className="col-md-12" >
              <CardLabel>{`${t("TL_RESURVEY_LAND")}`}</CardLabel>
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
            </div>
          </div>
        
        {value3 === "YES" && (
          <div>
            <div className="row">    
            <div className="col-md-12" > 
                <h1 className="headingh1" >
                    <span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_RESURVEY_LAN_DETAILS")}*`}</span>
                </h1>
            </div>        
          </div>
          <div className="row">
            <div className="col-md-6" >
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
            </div>
            <div className="col-md-6" >
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
            </div>
          </div>
          <div className="row">
            <div className="col-md-6" >
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
            </div>
            <div className="col-md-6" >
            <CardLabel>{`${t("TL_LOCALIZATION_PARTITION_NO")}`}</CardLabel>
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
              
          </div>)}
          {value3 === "NO"  && (
          <div>
            <div className="row">    
            <div className="col-md-12" > 
                <h1 className="headingh1" >
                    <span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_RESURVEY_LAN_DETAILS")}*`}</span>
                </h1>
            </div>        
          </div>
          <div className="row">
            <div className="col-md-4" >
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
            </div>
            <div className="col-md-4" >
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
            </div>
            <div className="col-md-4" >
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
            </div>
          </div>
        </div>)}
      </div>  
      )}
        
      {value2 === "BUIL" && (
        <div>   
        <div className="row">    
          <div className="col-md-12" > 
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_BUILDING_HEADER")}*`}</span>
              </h1>
          </div>        
        </div>              
          <div className="row">
              <div className="col-md-6" >
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
              <div className="col-md-6" >
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
              <div className="col-md-6" >
                <CardLabel>{`${t("TL_LOCALIZATION_DOOR_NO")}`}</CardLabel>
                <TextInput
                    t={t}
                    isMandatory={false}
                    type={"text"}
                    optionKey="i18nKey"
                    name="DoorNoBuild"
                    value={DoorNoBuild}
                    onChange={setSelectDoorNoBuild}
                    disable={isEdit}
                    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
                  />
              </div>
              <div className="col-md-6" >
                <CardLabel>{`${t("TL_LOCALIZATION_DOOR_NO_SUB")}`}</CardLabel>
                <TextInput
                    t={t}
                    isMandatory={false}
                    type={"text"}
                    optionKey="i18nKey"
                    name="DoorSubBuild"
                    value={DoorSubBuild}
                    onChange={setSelectDoorSubBuild}
                    disable={isEdit}
                    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
                />
              </div>
                
            </div>
          </div>)}
        {value2 === "VEHI" && (
        <div>   
        <div className="row">    
          <div className="col-md-12" > 
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_VECHICLE_HEADER")}*`}</span>
              </h1>
          </div>        
        </div>              
          <div className="row">              
              <div className="col-md-12" >
                <CardLabel>{`${t("TL_VECHICLE_NO")}`}</CardLabel>
                <TextInput
                  t={t}
                  isMandatory={false}
                  type={"text"}
                  optionKey="i18nKey"
                  name="VechicleNo"
                  value={VechicleNo}
                  onChange={setSelectVechicleno}
                  disable={isEdit}
                  {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
                />
              </div>
                
            </div>
          </div>)}
          {value2 === "WATE" && (
        <div>   
        <div className="row">    
          <div className="col-md-12" > 
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_VESSEL_HEADER")}*`}</span>
              </h1>
          </div>        
        </div>              
          <div className="row">              
              <div className="col-md-12" >
                <CardLabel>{`${t("TL_VESSEL_NO")}`}</CardLabel>
                <TextInput
                  t={t}
                  isMandatory={false}
                  type={"text"}
                  optionKey="i18nKey"
                  name="VechicleNo"
                  value={VechicleNo}
                  onChange={setSelectVechicleno}
                  disable={isEdit}
                  {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
                />
              </div>
                
            </div>
          </div>)}
     
    </FormStep>
    </React.Fragment>
  );
};
export default SelectStructureType;
