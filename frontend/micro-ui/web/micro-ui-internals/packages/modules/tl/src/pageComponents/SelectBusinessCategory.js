import React, { useState,useEffect } from "react";
import { CardLabel, TypeSelectCard,Dropdown,TextInput } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectBusinessCategory = ({ t, config, onSelect, userType, formData, }) => {
  let validation = {};
  const stateId = Digit.ULBService.getStateId();
  const { data: sector = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "EnterpriseType");
  const [setSector, setSelectedSector] = useState(formData?.TradeDetails?.setPlaceofActivity);
  const [CapitalAmount, setCapitalAmount] = useState(formData.TradeDetails?.BlockNo);
  const [isInitialRender, setIsInitialRender] = useState(true);
  let cmbSector = [];
  let cmbSectorFileterData = [];
  sector &&
  sector["TradeLicense"] &&
  sector["TradeLicense"].EnterpriseType.map((ob) => {
    cmbSector.push(ob);
    });
 const menu = [
    { name: "MANUFACTORING SECTOR", code: "MANUFACTORING" },
    { name: "SERVICE SECTOR", code: "SERVICE" },
  ];
  const onSkip = () => onSelect();

  function selectSector(value) {
    setSelectedSector(value);    
       
  }
  function selectedsetCapitalAmount(e) {
    setCapitalAmount(e.target.value);
    // setIsInitialRender(true);
    if(setSector){
        cmbSectorFileterData.push((cmbSector.filter( (cmbSector) => cmbSector.code.includes(setSector.code))));
        console.log(cmbSectorFileterData);
        cmbSectorFileterData[0].forEach(element => {
            console.log('element',parseFloat(CapitalAmount).toString());
            console.log('element',parseFloat(element.investmentTo).toString());
            if(parseFloat(CapitalAmount).toString()<=parseFloat(element.investmentTo).toString()){
                console.log(element.typeName);
            }
            
        //    this.deductionAmountTemp=+element.amount ;
        //    this.deductionAmountTotal=this.deductionAmountTotal+this.deductionAmountTemp;
      });
    }
    //    this.paymentorderForm.controls.pamount.setValue(this.paymentorderForm.controls.grossamount.value-this.deductionAmountTotal);
  }

//   useEffect(() => {
//     if (isInitialRender) {
//       if(setSector){
//         setIsInitialRender(false);
//         cmbSectorFileterData.push((cmbSector.filter( (cmbSector) => cmbSector.code.includes(setSector.code))));
//       }
//     }
//   }, [isInitialRender]);
 
  function goNext() {
   
    sessionStorage.setItem("setSector", setSector.sectorName);
    onSelect(config.key, { setSector });
    sessionStorage.setItem("CapitalAmount", CapitalAmount);
    onSelect(config.key, { CapitalAmount });
    // onSelect(config.key, { routeElement });
    
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!setSector}>
      
      <CardLabel>Sector</CardLabel>
      <Dropdown
        t={t}
        optionKey="name"
        isMandatory={config.isMandatory}
        option={menu}
        selected={setSector}
        select={selectSector}
        // disabled={isEdit}
        {...(validation = { isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
      />
     <CardLabel>Amount Of Capital Investment</CardLabel>
       <TextInput
          t={t}
          isMandatory={false}
          type={"text"}
          optionKey="i18nKey"
          name="CapitalAmount"
          value={CapitalAmount}
          onChange={selectedsetCapitalAmount}
        //   disable={isEdit}
          {...(validation = { pattern: "^[0-9 ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
        />
    </FormStep>
    </React.Fragment>
  );
};
export default SelectBusinessCategory;
