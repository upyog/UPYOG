import React, { useState, useEffect } from "react";
import { CardLabel, TextInput, Dropdown } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons, LabelFieldPair } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";
import SelectLand from "./SelectLand";

const SelectStructureType = ({ t, config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const { data: place = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "PlaceOfActivity");
  const { data: dataitem = {}, isLoading } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "TradeStructureSubtype");
  const [setPlaceofActivity, setSelectedPlaceofActivity] = useState(formData?.TradeDetails?.setPlaceofActivity);
  const [StructureType, setStructureType] = useState(formData?.TradeDetails?.StructureType);
  const [activities, setActivity] = useState(0);
  const [isInitialRender, setIsInitialRender] = useState(true);
  const [isInitialRendercombo, setisInitialRendercombo] = useState(true);
  const [isInitialRenderRadio, setisInitialRenderRadio] = useState(true);
  const [value2, setValue2] = useState();
  const [value3, setValue3] = useState();
  const [BlockNo, setBlockno] = useState(formData.TradeDetails?.BlockNo);
  const [SurveyNo, setSurveyNo] = useState(formData.TradeDetails?.SurveyNo);
  const [SubDivNo, setSubDivNo] = useState(formData.TradeDetails?.SubDivNo);
  const [PartitionNo, setPartitionNo] = useState(formData.TradeDetails?.PartitionNo);
  const { data: boundaryList = {} } = Digit.Hooks.tl.useTradeLicenseMDMS(tenantId, "cochin/egov-location", "boundary-data");
  const [ZonalBuilding, setZonal] = useState(() => formData?.address?.ZonalBuilding || {});
  const [WardNoBuilding, setWardNo] = useState(() => formData?.address?.Building || {});
  const [DoorNoBuild, setDoorNoBuild] = useState(formData.TradeDetails?.DoorNoBuild);
  const [DoorSubBuild, setDoorSubBuild] = useState(formData.TradeDetails?.DoorSubBuild);
  const [wards, setFilterWard] = useState(0);
  const [ResurveyedLand, setResurveyedLand] = useState(formData?.TradeDetails?.ResurveyedLand);
  const [VechicleNo, setVechicleNo] = useState(formData.TradeDetails?.VechicleNo);
  const [VesselNo, setVesselNo] = useState(formData.TradeDetails?.VesselNo);
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  let validation = {};
  let naturetype = null;
  let naturetypecmbvalue = null;
  let routeComponent = null;
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
      if (ZonalBuilding) {
        setIsInitialRender(false);
        setFilterWard(ZonalBuilding.children)
      }
    }
  }, [wards, isInitialRender]);

  const onSkip = () => onSelect();

  function selectPlaceofactivity(value) {
    setIsInitialRender(true);
    setisInitialRendercombo(true);
    naturetypecmbvalue = value.code.substring(0, 4);
    setValue2(naturetypecmbvalue);
    setSelectedPlaceofActivity(value);
    setStructureType(null);
    setActivity(null);
  }
  function selectStructuretype(value) {
    setStructureType(value);

  }
  function selectResurveyedLand(value) {
    setResurveyedLand(value);
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
  function setSelectPartitionNo(e) {
    setPartitionNo(e.target.value);
  }
  function setSelectVechicleno(e) {
    setVechicleNo(e.target.value);
  }
  function setSelectVesselNo(e) {
    setVesselNo(e.target.value);
  }

  React.useEffect(() => {
    if (isInitialRender) {
      if (setPlaceofActivity) {
        setIsInitialRender(false);
        naturetype = setPlaceofActivity.code.substring(0, 4);
        setValue2(naturetype);
        setActivity(cmbStructure.filter((cmbStructure) => cmbStructure.maincode.includes(naturetype)));
        if (naturetype === "LAND") {
          setValue3(formData?.TradeDetails?.ResurveyedLand ? formData?.TradeDetails?.ResurveyedLand.code : null);
          // setisInitialRendercombo(true);
        }
      }
    }
  }, [activities, isInitialRender]);

  function goNext() {
    sessionStorage.setItem("PlaceOfActivity", setPlaceofActivity.code);
    sessionStorage.setItem("StructureType", StructureType.name);
    if (value2 === "LAND") {
      sessionStorage.setItem("ResurveyedLand", ResurveyedLand.code);
      sessionStorage.setItem("BlockNo", BlockNo);
      sessionStorage.setItem("SurveyNo", SurveyNo);
      sessionStorage.setItem("SubDivNo", SubDivNo);
      sessionStorage.setItem("PartitionNo", PartitionNo);
      // sessionStorage.setItem("ZonalBuilding", null);
      // sessionStorage.setItem("WardNoBuilding", null);
      sessionStorage.setItem("DoorNoBuild", '');
      sessionStorage.setItem("DoorSubBuild", '');
      sessionStorage.setItem("VechicleNo", '');
      sessionStorage.setItem("VesselNo", '');
      onSelect(config.key, { StructureType, setPlaceofActivity, ResurveyedLand, BlockNo, SurveyNo, SubDivNo, PartitionNo, ZonalBuilding, WardNoBuilding, DoorNoBuild, DoorSubBuild, VechicleNo, VesselNo });

    } else if (value2 === "BUIL") {
      sessionStorage.setItem("ResurveyedLand", false);
      sessionStorage.setItem("BlockNo", '');
      sessionStorage.setItem("SurveyNo", '');
      sessionStorage.setItem("SubDivNo", '');
      sessionStorage.setItem("PartitionNo", '');
      // sessionStorage.setItem("ZonalBuilding", ZonalBuilding.name);
      // sessionStorage.setItem("WardNoBuilding", WardNo.name);
      sessionStorage.setItem("DoorNoBuild", DoorNoBuild);
      sessionStorage.setItem("DoorSubBuild", DoorSubBuild);
      sessionStorage.setItem("VechicleNo", '');
      sessionStorage.setItem("VesselNo", '');
      onSelect(config.key, { StructureType, setPlaceofActivity, ResurveyedLand, BlockNo, SurveyNo, SubDivNo, PartitionNo, ZonalBuilding, WardNoBuilding, DoorNoBuild, DoorSubBuild, VechicleNo, VesselNo });

    } else if (value2 === "VEHI") {
      sessionStorage.setItem("ResurveyedLand", false);
      sessionStorage.setItem("BlockNo", '');
      sessionStorage.setItem("SurveyNo", '');
      sessionStorage.setItem("SubDivNo", '');
      sessionStorage.setItem("PartitionNo", '');
      // sessionStorage.setItem("ZonalBuilding", null);
      // sessionStorage.setItem("WardNoBuilding", null);
      sessionStorage.setItem("DoorNoBuild", '');
      sessionStorage.setItem("DoorSubBuild", '');
      sessionStorage.setItem("VechicleNo", VechicleNo);
      sessionStorage.setItem("VesselNo", '');
      onSelect(config.key, { StructureType, setPlaceofActivity, ResurveyedLand, BlockNo, SurveyNo, SubDivNo, PartitionNo, ZonalBuilding, WardNoBuilding, DoorNoBuild, DoorSubBuild, VechicleNo, VesselNo });

    } else if (value2 === "WATE") {
      sessionStorage.setItem("ResurveyedLand", false);
      sessionStorage.setItem("BlockNo", '');
      sessionStorage.setItem("SurveyNo", '');
      sessionStorage.setItem("SubDivNo", '');
      sessionStorage.setItem("PartitionNo", '');
      // sessionStorage.setItem("ZonalBuilding", null);
      // sessionStorage.setItem("WardNoBuilding", null);
      sessionStorage.setItem("DoorNoBuild", '');
      sessionStorage.setItem("DoorSubBuild", '');
      sessionStorage.setItem("VechicleNo", '');
      sessionStorage.setItem("VesselNo", VesselNo);
      onSelect(config.key, { StructureType, setPlaceofActivity, ResurveyedLand, BlockNo, SurveyNo, SubDivNo, PartitionNo, ZonalBuilding, WardNoBuilding, DoorNoBuild, DoorSubBuild, VechicleNo, VesselNo });

    }


  }
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline /> : null}
      <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!StructureType}>
        <div className="row">
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_PLACE_HEADER_MSG")}*`}</span> </h1>
          </div>
        </div>
        <div className="row">
          <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_PLACE_ACTVITY")}`}</CardLabel>
            <Dropdown t={t} optionKey="code" isMandatory={config.isMandatory} option={cmbPlace} selected={setPlaceofActivity} select={selectPlaceofactivity} disabled={isEdit} />
          </div>
          <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_NATURE_STRUCTURE")}`}</CardLabel>
            <Dropdown t={t} optionKey="name" isMandatory={config.isMandatory} option={activities} selected={StructureType} select={selectStructuretype} disabled={isEdit}
            />
          </div>
        </div>
        {value2 === "LAND" && (
          <div>
            <div className="row"><div className="col-md-12" >
              <LabelFieldPair style={{ display: "flex" }}><CardLabel>{`${t("TL_RESURVEY_LAND")}`}</CardLabel>
                <RadioButtons t={t} optionsKey="i18nKey" isMandatory={config.isMandatory} options={menu} selectedOption={ResurveyedLand} onSelect={selectResurveyedLand} disabled={isEdit} style={{ marginTop: "-8px", paddingLeft: "5px", height: "25px" }} />
              </LabelFieldPair></div>
            </div>

            {value3 === "YES" && (
              <div> <div className="row"><div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_RESURVEY_LAN_DETAILS")}*`}</span>   </h1> </div>
              </div>
                <div className="row">
                  <div className="col-md-3" ><CardLabel>{`${t("TL_LOCALIZATION_BLOCK_NO")}`}</CardLabel>
                    <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="BlockNo" value={BlockNo} onChange={setSelectBlockNo} disable={isEdit}  {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_BLOCK_NO") })} />
                  </div>
                  <div className="col-md-3" > <CardLabel>{`${t("TL_LOCALIZATION_SURVEY_NO")}`}</CardLabel>
                    <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="SurveyNo" value={SurveyNo} onChange={setSelectSurveyNo} disable={isEdit}     {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SURVEY_NO") })} />
                  </div>
                  <div className="col-md-3" ><CardLabel>{`${t("TL_LOCALIZATION_SUBDIVISION_NO")}`}</CardLabel>
                    <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="SubDivNo" value={SubDivNo} onChange={setSelectSubDivNo} disable={isEdit}     {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
                  </div>
                  <div className="col-md-3" > <CardLabel>{`${t("TL_LOCALIZATION_PARTITION_NO")}`}</CardLabel>
                    <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PartitionNo" value={PartitionNo} onChange={setSelectPartitionNo} disable={isEdit}     {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
                  </div>
                </div>
              </div>)}
            {value3 === "NO" && (
              <div> <div className="row">
                <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_RESURVEY_LAN_DETAILS")}*`}</span></h1> </div>
              </div>
                <div className="row">
                  <div className="col-md-4" ><CardLabel>{`${t("TL_LOCALIZATION_BLOCK_NO")}`}</CardLabel>
                    <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="BlockNo" value={BlockNo} onChange={setSelectBlockNo} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_BLOCK_NO") })} />
                  </div>
                  <div className="col-md-4" > <CardLabel>{`${t("TL_LOCALIZATION_SURVEY_NO")}`}</CardLabel>
                    <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="SurveyNo" value={SurveyNo} onChange={setSelectSurveyNo} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SURVEY_NO") })} />
                  </div>
                  <div className="col-md-4" > <CardLabel>{`${t("TL_LOCALIZATION_SUBDIVISION_NO")}`}</CardLabel>
                    <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="SubDivNo" value={SubDivNo} onChange={setSelectSubDivNo} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
                  </div>
                </div>
              </div>)}
          </div>
        )}

        {value2 === "BUIL" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_BUILDING_HEADER")}*`}</span> </h1>
              </div>
            </div>
            <div className="row"> 
              {/* <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_ZONAL_OFFICE")}`}</CardLabel>
                <Dropdown t={t} optionKey="name" isMandatory={config.isMandatory} option={cmbZonal[0]} selected={ZonalBuilding} select={setSelectZonalOffice} disabled={isEdit}  {...(validation = { isRequired: true, title: t("TL_INVALID_ZONAL_NAME") })} /> </div>
              <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_WARD_NO")}`}</CardLabel>
                <Dropdown t={t} optionKey="name" isMandatory={config.isMandatory} option={wards} selected={WardNoBuilding} select={setSelectWard} disabled={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$",isRequired: true, title: t("TL_INVALID_WARD_NO") })} />
              </div> */}
              <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_DOOR_NO")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="DoorNoBuild" value={DoorNoBuild} onChange={setSelectDoorNoBuild} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
              </div>
              <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_DOOR_NO_SUB")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="DoorSubBuild" value={DoorSubBuild} onChange={setSelectDoorSubBuild} disable={isEdit}       {...(validation = { pattern: "^[a-zA-Z-0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
              </div>
            </div>
          </div>)}
        {value2 === "VEHI" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_VECHICLE_HEADER")}*`}</span> </h1>
              </div>
            </div>
            <div className="row">
              <div className="col-md-12" ><CardLabel>{`${t("TL_VECHICLE_NO")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="VechicleNo" value={VechicleNo} onChange={setSelectVechicleno} disable={isEdit}     {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} /> </div>    </div>
          </div>)}
        {value2 === "WATE" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_VESSEL_HEADER")}*`}</span> </h1>
              </div>
            </div>
            <div className="row">
              <div className="col-md-12" ><CardLabel>{`${t("TL_VESSEL_NO")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="VesselNo" value={VesselNo} onChange={setSelectVesselNo} disable={isEdit}     {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} /> </div>    </div>
          </div>)}

      </FormStep>
    </React.Fragment>
  );
};
export default SelectStructureType;
