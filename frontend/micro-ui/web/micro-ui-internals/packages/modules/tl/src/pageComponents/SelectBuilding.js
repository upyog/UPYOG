import React, { useState,useEffect } from "react";
import { CardLabel, TypeSelectCard,Dropdown,TextInput } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectBuilding = ({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  const onSkip = () => onSelect();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const { data: boundaryList = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS("kl.cochin", "cochin/egov-location", "boundary-data");
  const [Zonal, setZonal] = useState(() => formData?.address?.Zonal || {});
  const [WardNo, setWardNo] = useState(() => formData?.address?.WardNo || {});
  const [DoorNoBuild, setDoorNoBuild] = useState(formData.TradeDetails?.DoorNoBuild);
  const [DoorSubBuild, setDoorSubBuild] = useState(formData.TradeDetails?.DoorSubBuild);
  const [wards, setFilterWard] = useState(0);
  const [isInitialRender, setIsInitialRender] = useState(true);
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  let cmbZonal = [];
  let cmbWard = [];
  let code =null;
  boundaryList &&
  boundaryList["egov-location"] &&
  boundaryList["egov-location"].TenantBoundary.map((ob) => {    
    cmbZonal.push(ob.boundary.children);
  });
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
 
  function goNext() {
    sessionStorage.setItem("Zonal", Zonal);
    sessionStorage.setItem("WardNo", WardNo);
    sessionStorage.setItem("DoorNoBuild", DoorNoBuild);
    sessionStorage.setItem("DoorSubBuild", DoorSubBuild);
    onSelect(config.key, { Zonal,WardNo,DoorNoBuild,DoorSubBuild });
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={[!Zonal,!WardNo,!DoorNoBuild,!DoorSubBuild]} >
  
      <CardLabel>Zonal Office</CardLabel>
      <Dropdown
        t={t}
        optionKey="name"
        isMandatory={config.isMandatory}
        option={cmbZonal[0]}
        selected={setZonal}
        select={setSelectZonalOffice}
        disabled={isEdit}
      />
      <CardLabel>Ward No</CardLabel>
      <Dropdown
        t={t}
        optionKey="name"
        isMandatory={config.isMandatory}
        option={wards}
        selected={setWardNo}
        select={setSelectWard}
        disabled={isEdit}
      />
      <CardLabel>Door No</CardLabel>
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
        <CardLabel>Door Sub No</CardLabel>
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
        
    </FormStep>
    </React.Fragment>
  );
};
export default SelectBuilding;
