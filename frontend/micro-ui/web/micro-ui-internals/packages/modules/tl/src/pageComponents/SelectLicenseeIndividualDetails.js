import React, { useState, useEffect } from "react";
import { CardLabel, TypeSelectCard, Dropdown, TextInput, TextArea } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectLicenseeIndividualDetails = ({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  const onSkip = () => onSelect();
  const [IndividualDesignation, setIndividualDesignation] = useState(formData.address?.IndividualDesignation);
  const [IndividualAadharNo, setIndividualAadharNo] = useState(formData.address?.IndividualAadharNo);
  const [IndividualName, setIndividualName] = useState(formData.address?.IndividualName);
  const [IndividualAddress, setIndividualAddress] = useState(formData.address?.IndividualAddress);
  const [IndividualMobNo, setIndividualMobNo] = useState(formData.address?.IndividualMobNo);
  const [IndividualEmailID, setIndividualEmailID] = useState(formData.address?.IndividualEmailID);
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");

  function setSelectIndividualDesignation(e) {
    setIndividualDesignation(e.target.value);
  }
  function setSelectIndividualAadharNo(e) {
    setIndividualAadharNo(e.target.value);
  }
  function setSelectIndividualName(e) {
    setIndividualName(e.target.value);
  }
  function setSelectIndividualAddress(e) {
    setIndividualAddress(e.target.value);
  }
  function setSelectIndividualMobNo(e) {
    setIndividualMobNo(e.target.value);
  }
  function setSelectIndividualEmailID(e) {
    setIndividualEmailID(e.target.value);
  }
  function goNext() {
    sessionStorage.setItem("IndividualDesignation", IndividualDesignation);
    sessionStorage.setItem("IndividualAadharNo", IndividualAadharNo);
    sessionStorage.setItem("IndividualName", IndividualName);
    sessionStorage.setItem("IndividualAddress", IndividualAddress);
    sessionStorage.setItem("IndividualMobNo", IndividualMobNo);
    sessionStorage.setItem("IndividualEmailID", IndividualEmailID);
    onSelect(config.key, { IndividualDesignation,IndividualAadharNo, IndividualName, IndividualAddress, IndividualMobNo, IndividualEmailID });
  }
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}
      <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!IndividualAadharNo} >

        {formData.TradeDetails?.LicenseeType.code === "INDIVIDUAL" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_LICENSEE_INDIVIDUAL_HEADER_MSG")}*`}</span> </h1>
              </div>
            </div>
            <div className="row">
              <div className="col-md-6" ><CardLabel>{`${t("TL_LICENSEE_AADHAR_NO")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="IndividualAadharNo" value={IndividualAadharNo} onChange={setSelectIndividualAadharNo} disable={isEdit} placeholder={`${t("TL_LICENSEE_AADHAR_NO")}`} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true,maxLength:12,minLength:12, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
              <div className="col-md-6" ><CardLabel>{`${t("TL_LICENSEE_NAME")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="IndividualName" value={IndividualName} onChange={setSelectIndividualName} disable={isEdit} placeholder={`${t("TL_LICENSEE_NAME")}`} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
            </div>
            <div className="row">
              <div className="col-md-12" ><CardLabel>{`${t("TL_LICENSEE_ADDRESS")}`}</CardLabel>
                <TextArea t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="IndividualAddress" value={IndividualAddress} onChange={setSelectIndividualAddress} disable={isEdit} placeholder={`${t("TL_LICENSEE_ADDRESS")}`} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
            </div>
            <div className="row">
              <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_MOBILE_NO")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="IndividualMobNo" value={IndividualMobNo} onChange={setSelectIndividualMobNo} disable={isEdit} placeholder={`${t("TL_LOCALIZATION_MOBILE_NO")}`} {...(validation = { pattern: "[6-9]{1}[0-9]{9}", type: "tel", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
              <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_EMAIL_ID")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type="email" optionKey="i18nKey" name="IndividualEmailID" value={IndividualEmailID} onChange={setSelectIndividualEmailID} disable={isEdit} placeholder={`${t("TL_LOCALIZATION_EMAIL_ID")}`} {...(validation = { isRequired: true, title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
            </div>
          </div>
        )} {formData.TradeDetails?.LicenseeType.code === "INSTITUTION" && (
          <div>
            <div className="row">
              <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("TL_LICENSEE_INDIVIDUAL_HEADER_MSG")}*`}</span> </h1>
              </div>
            </div>
            <div className="row">
              <div className="col-md-4" >
                <CardLabel>{`${t("TL_LICENSEE_DESIGNATION")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="IndividualDesignation" value={IndividualDesignation} onChange={setSelectIndividualDesignation} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
              <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSEE_AADHAR_NO")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="IndividualAadharNo" value={IndividualAadharNo} onChange={setSelectIndividualAadharNo} disable={isEdit} placeholder={`${t("TL_LICENSEE_AADHAR_NO")}`} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true,maxLength:12,minLength:12, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
              <div className="col-md-4" ><CardLabel>{`${t("TL_LICENSEE_NAME")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="IndividualName" value={IndividualName} onChange={setSelectIndividualName} disable={isEdit} placeholder={`${t("TL_LICENSEE_NAME")}`} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
            </div>
            <div className="row">
              <div className="col-md-12" ><CardLabel>{`${t("TL_LICENSEE_ADDRESS")}`}</CardLabel>
                <TextArea t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="IndividualAddress" value={IndividualAddress} onChange={setSelectIndividualAddress} disable={isEdit} placeholder={`${t("TL_LICENSEE_ADDRESS")}`} {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
            </div>
            <div className="row">
              <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_MOBILE_NO")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="IndividualMobNo" value={IndividualMobNo} onChange={setSelectIndividualMobNo} disable={isEdit} placeholder={`${t("TL_LOCALIZATION_MOBILE_NO")}`} {...(validation = { pattern: "[6-9]{1}[0-9]{9}", type: "tel", isRequired: true, type: "text", title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
              <div className="col-md-6" ><CardLabel>{`${t("TL_LOCALIZATION_EMAIL_ID")}`}</CardLabel>
                <TextInput t={t} isMandatory={false} type="email" optionKey="i18nKey" name="IndividualEmailID" value={IndividualEmailID} onChange={setSelectIndividualEmailID} disable={isEdit} placeholder={`${t("TL_LOCALIZATION_EMAIL_ID")}`} {...(validation = { isRequired: true, title: t("TL_INVALID_SUBDIVISION_NO") })} />
              </div>
            </div>
          </div>
        )}


      </FormStep>
    </React.Fragment>
  );
};
export default SelectLicenseeIndividualDetails;
