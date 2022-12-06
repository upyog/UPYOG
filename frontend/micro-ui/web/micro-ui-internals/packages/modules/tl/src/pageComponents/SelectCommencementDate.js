import React, { useState } from "react";
import { CardLabel, DatePicker, TextInput } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectCommencementDate = ({ t, config, onSelect, userType, formData }) => {
  const [CommencementDate, setCommencementDate] = useState(formData?.owners?.CommencementDate);
  const [LicensePeriod, setLicensePeriod] = useState(formData.owners?.LicensePeriod);
  const [documents, setdocuments] = useState(formData.owners?.documents);
  const isEdit = window.location.href.includes("/edit-application/")||window.location.href.includes("renew-trade");
  let validation = {};
  const onSkip = () => onSelect();
  function selectCommencementDate(value) {
    setCommencementDate(value);
  }
  function setSelectLicensePeriod(e) {
    setLicensePeriod(e.target.value);
  }
  function goNext() {
    sessionStorage.setItem("CommencementDate", CommencementDate);   
    sessionStorage.setItem("LicensePeriod", LicensePeriod);
    onSelect(config.key, { CommencementDate,LicensePeriod,documents });
  }
  console.log(formData);
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!CommencementDate}>
        <div className="row">    
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_LICENSE_DECLARATION")}*`}</span></h1>
          </div>        
        </div>
        <div className="row">
          <div className="col-md-6" ><CardLabel>{t("TL_NEW_TRADE_DETAILS_TRADE_COMM_DATE_LABEL")}</CardLabel>
            <DatePicker date={CommencementDate} name="CommencementDate" onChange={selectCommencementDate} disabled={isEdit} />
          </div>
          <div className="col-md-6" ><CardLabel>{`${t("TL_LICENSE_PERIOD")}`}</CardLabel>
            <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="LicensePeriod" value={LicensePeriod} onChange={setSelectLicensePeriod} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
          </div>
        </div>
        <div className="row">    
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{background:"#fff",padding:"0 10px" }}>{`${t("TL_LICENSE_DECLARATION_FINAL")}*`}</span></h1>
        </div>        
        </div>
        <div className="row"><div className="col-md-12" ><CardLabel>{`${t("TL_LICENSE_DECLARATION_MSG_ONE")}`}</CardLabel></div> 
        </div>
        <div className="row"><div className="col-md-12" ><CardLabel>{`${t("TL_LICENSE_DECLARATION_MSG_TWO")}`}</CardLabel></div> 
        </div>
        <div className="row"><div className="col-md-12" ><CardLabel>{`${t("TL_LICENSE_DECLARATION_MSG_THREE")}`}</CardLabel></div> 
        </div>
        <div className="row"><div className="col-md-12" ><CardLabel>{`${t("TL_LICENSE_DECLARATION_MSG_FOUR")}`}</CardLabel></div> 
        </div>
    </FormStep>
    </React.Fragment>
  );
};
export default SelectCommencementDate;
