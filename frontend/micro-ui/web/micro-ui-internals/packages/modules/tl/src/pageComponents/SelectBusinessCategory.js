import React, { useState,useEffect } from "react";
import { CardLabel, TypeSelectCard,Dropdown,TextInput } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectBusinessCategory = ({ t, config, onSelect, userType, formData, }) => {
  let validation = {};
  const stateId = Digit.ULBService.getStateId();
  const { data: sector = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "EnterpriseType");
  const [setSector, setSelectedSector] = useState(formData?.TradeDetails?.setSector);
  const [CapitalAmount, setCapitalAmount] = useState(formData.TradeDetails?.CapitalAmount);
  let cmbSector = [];
  let cmbSectorFileterData = [];
  sector &&
  sector["TradeLicense"] &&
  sector["TradeLicense"].EnterpriseType.map((ob) => {
    cmbSector.push(ob);
    });
 const menu = [
    { name: "Manufacturing Sector", code: "MANUFACTORING" },
    { name: "Service Sector", code: "SERVICE" },
  ];
  const onSkip = () => onSelect();

  function selectSector(value) {
    setSelectedSector(value);   
  }
  function selectedsetCapitalAmount(e) {
    setCapitalAmount(e.target.value);    
  }

  function goNext() { 
    let accessories =[];
    let details =[];
    let enterpriseType = null;
    // details.propertyId ="PG-PT-2022-09-14-006185";
    // sessionStorage.setItem("details", details.propertyId ="PG-PT-2022-09-14-006185");
    // sessionStorage.setItem("accessories", accessories);
    sessionStorage.setItem("setSector", setSector.sectorName);
    sessionStorage.setItem("CapitalAmount", CapitalAmount);   
    if(sessionStorage.getItem("CapitalAmount")){
      cmbSectorFileterData.push((cmbSector.filter( (cmbSector) => cmbSector.code.includes(setSector.code))));
        cmbSectorFileterData[0].forEach(element => {
          if(parseFloat(CapitalAmount) >= parseFloat(element.investmentFrom) && parseFloat(CapitalAmount) <= parseFloat(element.investmentTo))
          { 
            enterpriseType = element.code; 
          }
        });
    } 
    sessionStorage.setItem("enterpriseType", enterpriseType);    
    onSelect(config.key, { setSector,CapitalAmount,enterpriseType });  
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline currentStep={2} /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!setSector}>
        <div className="row">    
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_BUISINESS_HEADER_MSG")}*`}</span> </h1>
          </div>        
        </div>
        <div className="row">
          <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_SECTOR")}`}</CardLabel>
            <Dropdown t={t} optionKey="name" isMandatory={config.isMandatory} option={menu} selected={setSector} select={selectSector}  {...(validation = { isRequired: true, title: t("TL_INVALID_SECTOR_NAME") })} />
          </div>
          <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_CAPITAL_AMOUNT")}`}</CardLabel>
          <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="CapitalAmount" value={CapitalAmount} onChange={selectedsetCapitalAmount} {...(validation = { pattern: "^[0-9 ]*$", isRequired: true, type: "text", title: t("TL_INVALID_CAPITAL_AMOUNT") })} />
          </div>
        </div>    
        <div className="row">
          <div className="col-md-12" ><CardLabel>{`${t("TL_BUISINESS_DECLARATION_ONE")}`}{`${t("TL_BUISINESS_DECLARATION_TWO")}`}{`${t("TL_BUISINESS_DECLARATION_THREE")}`}{`${t("TL_BUISINESS_DECLARATION_FOUR")}`}</CardLabel>
          </div> 
        </div> 
    </FormStep>
    </React.Fragment>
  );
};
export default SelectBusinessCategory;
