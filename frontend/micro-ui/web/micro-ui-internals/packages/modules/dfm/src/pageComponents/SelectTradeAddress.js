import React, { useState,useEffect } from "react";
import { CardLabel, TypeSelectCard,Dropdown,TextInput } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectTradeAddress = ({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  const onSkip = () => onSelect();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const { data: boundaryList = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS("kl.cochin", "cochin/egov-location", "boundary-data");
  const [Zonal, setZonal] = useState(formData.TradeDetails?.Zonal);
  const [WardNo, setWardNo] = useState(formData.TradeDetails?.WardNo);
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
    // console.log(e);
    setZonal(e);
    setWardNo(null);
    setFilterWard(null);
  }
  function setSelectWard(e) {
    setWardNo(e);
  }
  React.useEffect(() => {

    if (isInitialRender) {
      console.log(isInitialRender);
      if(Zonal){
        setIsInitialRender(false);
        console.log(Zonal.children); 
        setFilterWard(Zonal.children)
      }
    }
  }, [wards,isInitialRender]);
 
 
  function goNext() {
    sessionStorage.setItem("Zonal", Zonal);
    onSelect(config.key, { Zonal });
    sessionStorage.setItem("WardNo", WardNo);
    onSelect(config.key, { WardNo });
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isEdit={[!Zonal]} >
  
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
      
        
    </FormStep>
    </React.Fragment>
  );
};
export default SelectTradeAddress;
