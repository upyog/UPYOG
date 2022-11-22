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
  const [isInitialRender, setIsInitialRender] = useState(true);
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

  // useEffect(() => {
  //   if (isInitialRender) {
  //     // if(setSector){
  //     //   setIsInitialRender(false);
  //     //   cmbSectorFileterData.push((cmbSector.filter( (cmbSector) => cmbSector.code.includes(setSector.code))));
  //     //   console.log(cmbSectorFileterData);
  //     //   if(setCapitalAmount){
  //     //     cmbSectorFileterData[0].forEach(element => {
  //     //       console.log('element',parseFloat(CapitalAmount).toString());
  //     //       console.log('element',parseFloat(element.investmentTo).toString());
  //     //       if(parseFloat(CapitalAmount).toString()<=parseFloat(element.investmentTo).toString()){
  //     //           console.log(element.code);
  //     //       }
  //     //     });
  //     //   }
        
  //     // }
  //   }
  // }, [isInitialRender]);
 
  function goNext() { 
    let accessories =[];
    let details =[];
    let enterpriseType = null;
    // details.propertyId ="PG-PT-2022-09-14-006185";
    sessionStorage.setItem("details", details.propertyId ="PG-PT-2022-09-14-006185");
    sessionStorage.setItem("accessories", accessories);
    sessionStorage.setItem("setSector", setSector.sectorName);
    sessionStorage.setItem("CapitalAmount", CapitalAmount);   
    if(sessionStorage.getItem("CapitalAmount")){
      cmbSectorFileterData.push((cmbSector.filter( (cmbSector) => cmbSector.code.includes(setSector.code))));
      console.log(cmbSectorFileterData);      
        cmbSectorFileterData[0].forEach(element => {
          if(parseFloat(CapitalAmount) >= parseFloat(element.investmentFrom) && parseFloat(CapitalAmount) <= parseFloat(element.investmentTo)){
              console.log(element.code);
              enterpriseType = element.code;
              
          }
        });
          
    } 
    sessionStorage.setItem("enterpriseType", enterpriseType);    
    onSelect(config.key, { accessories,setSector,CapitalAmount,details,enterpriseType });  
     
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!setSector}>
    <div className="row">    
          <div className="col-md-12" > 
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("Business Category Details")}*`}</span>
              </h1>
          </div>        
        </div>
        <div className="row">
          <div className="col-md-6" >
            <CardLabel>{`${t("TL_LOCALIZATION_SECTOR")}`}</CardLabel>
            <Dropdown
              t={t}
              optionKey="name"
              isMandatory={config.isMandatory}
              option={menu}
              selected={setSector}
              select={selectSector}
              // disabled={isEdit}
              {...(validation = { isRequired: true, title: t("TL_INVALID_SECTOR_NAME") })}
            />
          </div>
          <div className="col-md-6" >
          <CardLabel>{`${t("TL_LOCALIZATION_CAPITAL_AMOUNT")}`}</CardLabel>
          <TextInput
              t={t}
              isMandatory={false}
              type={"text"}
              optionKey="i18nKey"
              name="CapitalAmount"
              value={CapitalAmount}
              onChange={selectedsetCapitalAmount}
              // onBlur={() => selectedsetCapitalAmount()}
            //   disable={isEdit}
              {...(validation = { pattern: "^[0-9 ]*$", isRequired: true, type: "text", title: t("TL_INVALID_CAPITAL_AMOUNT") })}
            />
          </div>
        </div>    
        <div className="row">
          <div className="col-md-12" >
              <CardLabel>{`${t("I hereby declare that my business activity is Service/Production  (Pick data from inputs) with capital investment (Pick data from inputs) defined as Micro/Mini/Small/Medium/Large as per IFTE &OS Rules 2020")}`}</CardLabel>
          </div> 
        </div> 
    </FormStep>
    </React.Fragment>
  );
};
export default SelectBusinessCategory;
