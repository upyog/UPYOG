import { CardLabel, Dropdown, FormStep, LinkButton, Loader, RadioButtons, RadioOrSelect, TextInput,TextArea } from "@egovernments/digit-ui-react-components";
import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../components/TLTimeline";
import { sortDropdownNames } from "../utils/index";

const SelectTradeUnits = ({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  const [TradeCategory, setTradeCategory] = useState("");
  const [TradeType, setTradeType] = useState(formData?.TadeDetails?.Units?.TradeType || "");
  const [TradeSubType, setTradeSubType] = useState(formData?.TadeDetails?.Units?.TradeSubType || "");
  const [CustomType, setCustomType] = useState(formData?.TadeDetails?.Units?.CustomType || "");
  const [BusinessActivity, setBusinessActivity] = useState(formData?.TadeDetails?.Units?.BusinessActivity || "");
  const [fields, setFeilds] = useState(
    (formData?.TradeDetails && formData?.TradeDetails?.units) || [{ tradecategory: "", tradetype: "", tradesubtype: "", unit: null, uom: null }]
  );
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

  function handleAdd() {
    const values = [...fields];
    values.push({ tradecategory: "", tradetype: "", tradesubtype: "", unit: null, uom: null });
    setFeilds(values);
  }

  function handleRemove(index) {
    const values = [...fields];
    if (values.length != 1) {
      values.splice(index, 1);
      setFeilds(values);
    }
  }

  const { isLoading, data: Data = {} } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "TradeUnits", "[?(@.type=='TL')]");
  let TradeCategoryMenu = [];
  //let TradeTypeMenu = [];

  Data &&
    Data.TradeLicense &&
    Data.TradeLicense.TradeType.map((ob) => {
      if (!TradeCategoryMenu.some((TradeCategoryMenu) => TradeCategoryMenu.code === `${ob.code.split(".")[0]}`)) {
        TradeCategoryMenu.push({ i18nKey: `TRADELICENSE_TRADETYPE_${ob.code.split(".")[0]}`, code: `${ob.code.split(".")[0]}` });
      }
    });

  function getTradeTypeMenu(TradeCategory) {
    let TradeTypeMenu = [];
    Data &&
      Data.TradeLicense &&
      Data.TradeLicense.TradeType.map((ob) => {
        if (
          ob.code.split(".")[0] === TradeCategory.code &&
          !TradeTypeMenu.some((TradeTypeMenu) => TradeTypeMenu.code === `${ob.code.split(".")[1]}`)
        ) {
          TradeTypeMenu.push({ i18nKey: `TRADELICENSE_TRADETYPE_${ob.code.split(".")[1]}`, code: `${ob.code.split(".")[1]}` });
        }
      });
    return TradeTypeMenu;
  }

  function getTradeSubTypeMenu(TradeType) {
    let TradeSubTypeMenu = [];
    TradeType &&
      Data &&
      Data.TradeLicense &&
      Data.TradeLicense.TradeType.map((ob) => {
        if (ob.code.split(".")[1] === TradeType.code && !TradeSubTypeMenu.some((TradeSubTypeMenu) => TradeSubTypeMenu.code === `${ob.code}`)) {
          TradeSubTypeMenu.push({ i18nKey: `TL_${ob.code}`, code: `${ob.code}` });
        }
      });
    return TradeSubTypeMenu;
  }

  const isUpdateProperty = formData?.isUpdateProperty || false;
  let isEditProperty = formData?.isEditProperty || false;
  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");

  function selectTradeCategory(i, value) {
    let units = [...fields];
    units[i].tradecategory = value;
    setTradeCategory(value);
    selectTradeType(i, null);
    selectTradeSubType(i, null);
    setFeilds(units);
  }
  function selectTradeType(i, value) {
    let units = [...fields];
    units[i].tradetype = value;
    setTradeType(value);
    selectTradeSubType(i, null);
    setFeilds(units);
  }
  function selectTradeSubType(i, value) {
    let units = [...fields];
    units[i].tradesubtype = value;
    setTradeSubType(value);
    // if (value == null) {
    //   units[i].unit = null;
    //   setUnitOfMeasure(null);
    // }
    // Array.from(document.querySelectorAll("input")).forEach((input) => (input.value = ""));
    // value &&
    //   Data &&
    //   Data.TradeLicense &&
    //   Data.TradeLicense.TradeType.map((ob) => {
    //     if (value.code === ob.code) {
    //       units[i].unit = ob.uom;
    //       setUnitOfMeasure(ob.uom);
    //       // setFeilds(units);
    //     }
    //   });
    setFeilds(units);
  }
  // function selectUnitOfMeasure(i, e) {
  //   let units = [...fields];
  //   units[i].unit = e.target.value;
  //   setUnitOfMeasure(e.target.value);
  //   setFeilds(units);
  // }
  // function selectUomValue(i, e) {
  //   let units = [...fields];
  //   units[i].uom = e.target.value;
  //   setUomValue(e.target.value);
  //   setFeilds(units);
  // }
  function selectCustomType(i, e) {
    
    let units = [...fields];
    units[i].unit = e.target.value;
    setCustomType(e.target.value);
    setFeilds(units);
  }
  function selectBusinessActivity(i, e) {
    let units = [...fields];
    units[i].uom = e.target.value;
    setBusinessActivity(e.target.value);
    setFeilds(units);
  }

  const goNext = () => {
    let units = fields;
    // formData.TradeDetails.Units;    
    let unitsdata;

    unitsdata = { ...units, units: fields };
    onSelect(config.key, unitsdata);
  };

  const onSkip = () => onSelect();
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline /> : null}
      {isLoading ? ( <Loader />) : (
        <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t} isDisabled={!fields[0].tradecategory || !fields[0].tradetype || !fields[0].tradesubtype || !fields[0].unit || !fields[0].uom } >
          {fields.map((field, index) => {
            return (
              <div key={`${field}-${index}`}>                
                <div style={{ borderRadius: "5px", borderColor: "#f3f3f3", background: "white", display:"flow-root", }} >
                    <div className="row">    
                      <div className="col-md-12" ><h1 className="headingh1" ><span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_TRADE_UNITS_TEXT")}*`}</span></h1>
                      </div>        
                    </div>
                    <div className="row">
                      {!isLoading ? (
                      <div className="col-md-4" ><CardLabel>{`${t("TL_NEW_TRADE_DETAILS_TRADE_CAT_LABEL")}*`}</CardLabel>
                        <Dropdown t={t} option={TradeCategoryMenu} optionKey="i18nKey" name={`TradeCategory-${index}`} value={field?.tradecategory} selected={field?.tradecategory} select={(e) => selectTradeCategory(index, e)} placeholder={`${t("TL_NEW_TRADE_DETAILS_TRADE_CAT_LABEL")}*`} />
                      </div>
                      ) : (
                        <Loader />
                      )}
                       <div className="col-md-4" >
                       <CardLabel>{`${t("TL_NEW_TRADE_DETAILS_TRADE_TYPE_LABEL")}*`}</CardLabel>
                        <Dropdown t={t} optionKey="i18nKey" isMandatory={config.isMandatory} option={getTradeTypeMenu(field?.tradecategory)} selected={field?.tradetype} select={(e) => selectTradeType(index, e)} placeholder={`${t("TL_NEW_TRADE_DETAILS_TRADE_TYPE_LABEL")}*`} />
                       </div>
                       <div className="col-md-4" >                  
                        <CardLabel>{`${t("TL_NEW_TRADE_DETAILS_TRADE_SUBTYPE_LABEL")}*`}</CardLabel>
                          <Dropdown t={t} optionKey="i18nKey" isMandatory={config.isMandatory} option={sortDropdownNames(getTradeSubTypeMenu(field?.tradetype), "i18nKey", t)} selected={field?.tradesubtype} select={(e) => selectTradeSubType(index, e)} placeholder={`${t("TL_NEW_TRADE_DETAILS_TRADE_SUBTYPE_LABEL")}*`} />
                      </div>
                    </div>
                    <div className="row">                      
                      <div className="col-md-6">
                        <CardLabel>{`${t("TL_CUSTOM_DETAILED_TYPE_LABEL")}`}</CardLabel>
                        <TextInput t={t} type={"text"} isMandatory={config.isMandatory} optionKey="i18nKey" name="CustomType" value={field?.unit} onChange={(e) => selectCustomType(index, e)} placeholder={`${t("TL_CUSTOM_DETAILED_TYPE_LABEL")}`} />
                      </div>
                      <div className="col-md-6">
                        <CardLabel>{`${t("TL_BUSINESS_ACTIVITY_LABEL")}`}</CardLabel> 
                        <TextArea t={t} type={"text"} isMandatory={config.isMandatory} optionKey="i18nKey" placeHolder={`${t("TL_BUSINESS_ACTIVITY_LABEL")}`} name="BusinessActivity" value={field?.uom} onChange={(e) => selectBusinessActivity(index, e)} {...(validation = { isRequired: true, type: "text", title: t("TL_WRONG_UOM_VALUE_ERROR"),})} placeholder={`${t("TL_BUSINESS_ACTIVITY_LABEL")}`} />
                      </div>
                    </div>
                </div>
              </div>
            );
          })}
        </FormStep>
      )}
    </React.Fragment>
  );
};
export default SelectTradeUnits;