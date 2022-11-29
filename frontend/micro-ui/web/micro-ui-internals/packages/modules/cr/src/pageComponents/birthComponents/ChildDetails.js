import React, { useState } from "react";
import { FormStep,CardLabel, TextInput,Dropdown,DatePicker } from "@egovernments/digit-ui-react-components";
import Timeline from "../../components/CRTimeline";
import { useTranslation } from "react-i18next";

const ChildDetails = ({ config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  let validation = {};
  const { data: place = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "PlaceOfActivity");
  const [setPlaceofActivity, setSelectedPlaceofActivity] = useState(formData?.TradeDetails?.setPlaceofActivity);
  const isEdit = window.location.href.includes("/edit-application/")||window.location.href.includes("renew-trade");
  const [TradeName, setTradeName] = useState(null);
  const [CommencementDate, setCommencementDate] = useState();
  let naturetypecmbvalue =null;
  let cmbPlace = [];
  place &&
  place["TradeLicense"] &&
  place["TradeLicense"].PlaceOfActivity.map((ob) => {
        cmbPlace.push(ob);
    });

  const onSkip = () => onSelect();

  function selectPlaceofactivity(value) {
    naturetypecmbvalue=value.code.substring(0, 4);
    setSelectedPlaceofActivity(value);    
  }
  
  function setSelectTradeName(e) {
    setTradeName(e.target.value);
  }
  function selectCommencementDate(value) {
    setCommencementDate(value);
  }
  
 
  const goNext = () => {
    sessionStorage.setItem("PlaceOfActivity", setPlaceofActivity);   
    onSelect(config.key, { setPlaceofActivity });
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!CommencementDate}>
    <header className="card-header" style={{fontSize:"35px"}}>Child Information</header>
    <div className="row">    
          <div className="col-md-12" > 
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("Registration Details")}*`}</span>
              </h1>
          </div>        
    </div>
    <div className="row">
        <div className="col-md-4" >
            <CardLabel>{t("Date of Birth")}</CardLabel>
            {/* date={CommencementDate} */}
            <DatePicker date={CommencementDate} name="CommencementDate" onChange={selectCommencementDate}  />
        </div>
        <div className="col-md-4" >
            <CardLabel>{t("Date of Reporting")}</CardLabel>
            <DatePicker date={CommencementDate} name="CommencementDate" onChange={selectCommencementDate}  />
        </div>
        <div className="col-md-4" >
            <CardLabel>{`${t("Gender")}`}</CardLabel>
            <Dropdown
                t={t}
                optionKey="code"
                isMandatory={false}
                option={cmbPlace}
                selected={setPlaceofActivity}
                select={selectPlaceofactivity}
                disabled={isEdit}
            />
        </div>
    </div>
    <div className="row">    
          <div className="col-md-12" > 
              <h1 className="headingh1" >
                  <span style={{background:"#fff",padding:"0 10px" }}>{`${t("Name of Child")}*`}</span>
              </h1>
          </div>        
    </div>
    <div className="row">    
        <div className="col-md-4" > 
            <CardLabel>{`${t("First Name (English)")}`}</CardLabel>
            <TextInput       
            t={t}
            isMandatory={false}
            type={"text"}
            optionKey="i18nKey"
            name="TradeName"
            value={TradeName}
            onChange={setSelectTradeName}
            disable={isEdit}
            {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />
        </div>
        <div className="col-md-4" >
            <CardLabel>{`${t("Middle Name (English)")}`}</CardLabel>
            <TextInput       
            t={t}
            isMandatory={false}
            type={"text"}
            optionKey="i18nKey"
            name="TradeName"
            value={TradeName}
            onChange={setSelectTradeName}
            disable={isEdit}
            {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />
        </div>
        <div className="col-md-4" >
            <CardLabel>{`${t("Last Name (English)")}`}</CardLabel>
            <TextInput       
            t={t}
            isMandatory={false}
            type={"text"}
            optionKey="i18nKey"
            name="TradeName"
            value={TradeName}
            onChange={setSelectTradeName}
            disable={isEdit}
            {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />
        </div>
    </div>
    <div className="row">    
        <div className="col-md-4" > 
            <CardLabel>{`${t("First Name (Malayalam)")}`}</CardLabel>
            <TextInput       
            t={t}
            isMandatory={false}
            type={"text"}
            optionKey="i18nKey"
            name="TradeName"
            value={TradeName}
            onChange={setSelectTradeName}
            disable={isEdit}
            {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />
        </div>
        <div className="col-md-4" >
            <CardLabel>{`${t("Middle Name (Malayalam)")}`}</CardLabel>
            <TextInput       
            t={t}
            isMandatory={false}
            type={"text"}
            optionKey="i18nKey"
            name="TradeName"
            value={TradeName}
            onChange={setSelectTradeName}
            disable={isEdit}
            {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />
        </div>
        <div className="col-md-4">
            <CardLabel>{`${t("Last Name (Malayalam)")}`}</CardLabel>
            <TextInput       
            t={t}
            isMandatory={false}
            type={"text"}
            optionKey="i18nKey"
            name="TradeName"
            value={TradeName}
            onChange={setSelectTradeName}
            disable={isEdit}
            {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />  
        </div>
    </div>     
      
    </FormStep>
    </React.Fragment>
  );
};
export default ChildDetails;
