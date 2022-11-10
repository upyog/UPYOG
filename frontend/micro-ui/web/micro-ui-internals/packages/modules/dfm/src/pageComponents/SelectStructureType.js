
import React, { useState } from "react";
import { CardLabel, TypeSelectCard } from "@egovernments/digit-ui-react-components";
// import React, { useState,useEffect } from "react";
// import { CardLabel, TypeSelectCard,Dropdown } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectStructureType = ({ t, config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();
  const { data: place = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "PlaceOfActivity");
  const { data: dataitem = {}, isLoading } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "TradeStructureSubtype");
  console.log(formData);
  const [setPlaceofActivity, setSelectedPlaceofActivity] = useState();
  const [StructureType, setStructureType] = useState(formData?.TradeDetails?.StructureType);
  const [activities, setActivity] = useState(0);
  const [isInitialRender, setIsInitialRender] = useState(true);
  const isEdit = window.location.href.includes("/edit-application/")||window.location.href.includes("renew-trade");
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
   
  // const menu = [
  //   { i18nKey: "TL_COMMON_LAND", code: "LAND" },
  //   { i18nKey: "TL_COMMON_BUILDING", code: "BUILDINg" },
  // ];
 
  const onSkip = () => onSelect();

  function selectPlaceofactivity(value) {
    setIsInitialRender(true);
    naturetypecmbvalue=value.code.substring(0, 4);
    setSelectedPlaceofActivity(value);
    setStructureType(null);
    setActivity(null);
    // console.log(naturetypecmbvalue);    
    //  if(naturetypecmbvalue =="LAND"){      
    //     routeComponent = "land-type";
    //     console.log(routeComponent);        
    //   } else if(naturetypecmbvalue =="BUIL"){
    //     routeComponent = "building-det";
    //     console.log(routeComponent);
    //   }    
      // sessionStorage.removeItem("routeElement");
      // sessionStorage.setItem("routeElement", routeComponent);
      // onSelect(config.key, { routeElement });
  }
  function selectStructuretype(value) {
    setStructureType(value);
    
  }

  React.useEffect(() => {
    if (isInitialRender) {
      if(setPlaceofActivity){
        setIsInitialRender(false);
        naturetype = setPlaceofActivity.code.substring(0, 4);    
        setActivity(cmbStructure.filter( (cmbStructure) => cmbStructure.code.includes(naturetype)));
      }
    }
  }, [activities,isInitialRender]);
 
  function goNext() {
   
    sessionStorage.setItem("PlaceOfActivity", setPlaceofActivity.code);
    onSelect(config.key, { setPlaceofActivity });
    sessionStorage.setItem("StructureType", StructureType.name);
    onSelect(config.key, { StructureType });
    // onSelect(config.key, { routeElement });
    
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!StructureType}>
      

      {/* <CardLabel>Place Of Activity</CardLabel>
      <RadioOrSelect />

      <CardLabel>Nature Of Structure</CardLabel>
      <RadioOrSelect /> */}
      {/* <RadioButtons */}
      <CardLabel>Place Of Activity</CardLabel>
      <Dropdown
        t={t}
        optionKey="code"
        isMandatory={config.isMandatory}
        option={cmbPlace}
        selected={setPlaceofActivity}
        select={selectPlaceofactivity}
        disabled={isEdit}
      />
      {/* <RadioOrSelect 
          options={menu}
          selectedOption={PlaceofActivity}
          optionKey="mainName"
          onSelect={selectPlaceofactivity}
          t={t}
          labelKey=""
          // onChange={(e) => onChangeLB(e.target.value)}
        //  disabled={isEdit}
        /> */}

      <CardLabel>Nature Of Structure</CardLabel>
      <Dropdown
        t={t}
        optionKey="name"
        isMandatory={config.isMandatory}
        option={activities}
        selected={StructureType}
        select={selectStructuretype}
        disabled={isEdit}
      />
      {/* <RadioOrSelect 
          options={activities}
          selectedOption={StructureType}
          optionKey="name"
          onSelect={selectStructuretype}
          t={t}
          labelKey=""
          // onChange={(e) => onChangeLB(e.target.value)}
        //  disabled={isEdit}
        /> */}
      {/* <RadioButtons
>>>>>>> 98956340d46c8cd43ff9bc1be576479d43931349
        t={t}
        optionsKey="i18nKey"
        isMandatory={config.isMandatory}
        options={menu}
        selectedOption={StructureType}
        onSelect={selectStructuretype}
        disabled={isEdit}
      /> */}
    </FormStep>
    </React.Fragment>
  );
};
export default SelectStructureType;
