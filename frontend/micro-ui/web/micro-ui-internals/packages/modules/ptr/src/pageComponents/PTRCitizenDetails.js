import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons, LabelFieldPair, Dropdown, Menu, MobileNumber } from "@egovernments/digit-ui-react-components";
import { cardBodyStyle } from "../utils";
import { useLocation, useRouteMatch } from "react-router-dom";
import Timeline from "../components/TLTimeline";

const PTRCitizenDetails
 = ({ t, config, onSelect, userType, formData, ownerIndex }) => {
  const { pathname: url } = useLocation();
  // const editScreen = url.includes("/modify-application/");
  //const mutationScreen = url.includes("/property-mutation/");

  let index =  window.location.href.charAt(window.location.href.length - 1);
  let validation = {};

  const [applicantName, setName] = useState((formData.owners && formData.owners[index] && formData.owners[index].applicantName) || formData?.owners?.applicantName || "");
  const [emailId, setEmail] = useState((formData.owners && formData.owners[index] && formData.owners[index].emailId) || formData?.owners?.emailId || "");
  const [mobileNumber, setMobileNumber] = useState(
    (formData.owners && formData.owners[index] && formData.owners[index].mobileNumber) || formData?.owners?.mobileNumber || ""
  );
  const [alternateNumber, setAltMobileNumber] = useState(
    (formData.owners && formData.owners[index] && formData.owners[index].alternateNumber) || formData?.owners?.alternateNumber || ""
  );

  
  const [fatherName, setFatherOrHusbandName] = useState(
    (formData.owners && formData.owners[index] && formData.owners[index].fatherName) || formData?.owners?.fatherName || ""
  );
  
  // const isUpdateProperty = formData?.isUpdateProperty || false;
  // let isEditProperty = formData?.isEditProperty || false;

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

  

  function setOwnerName(e) {
    setName(e.target.value);
  }
  function setOwnerEmail(e) {
    setEmail(e.target.value);
  }
  

  function setMobileNo(e) {
    setMobileNumber(e.target.value);
  }
  
  function setAltMobileNo(e) {
    setAltMobileNumber(e.target.value);
  }
  function setGuardiansName(e) {
    setFatherOrHusbandName(e.target.value);
  }
  

  const goNext = () => {
    let owner = formData.owners && formData.owners[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, applicantName, mobileNumber,alternateNumber, fatherName, emailId};
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      
      ownerStep = { ...owner, applicantName,  mobileNumber,alternateNumber, fatherName,emailId };
      onSelect(config.key, ownerStep, false,index);
    }
  };

  const onSkip = () => onSelect();

  
  

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [applicantName, mobileNumber,alternateNumber, fatherName, emailId]);

 

  return (
    <React.Fragment>
    {
      window.location.href.includes("/citizen") ?
        window.location.href.includes("/citizen/pt/property/property-mutation") ? 
          <Timeline currentStep={1} flow="PT_MUTATE" /> : <Timeline currentStep={1} />
    : null
    }

    <FormStep
      config={config}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={!applicantName || !mobileNumber || !alternateNumber|| !fatherName || !emailId}
    >
      <div>
        <CardLabel>{`${t("PTR_APPLICANT_NAME")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="applicantName"
          value={applicantName}
          onChange={setOwnerName}
          //disable={isUpdateProperty || isEditProperty}
          ValidationRequired = {false}
          {...(validation = {
            isRequired: false,
            pattern: "^[a-zA-Z-.`' ]*$",
            type: "text",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
        />
       
        <CardLabel>{`${t("PTR_MOBILE_NUMBER")}`}</CardLabel>
        <MobileNumber
          value={mobileNumber}
          name="mobileNumber"
          onChange={(value) => setMobileNo({ target: { value } })}
          //disable={isUpdateProperty || isEditProperty}
          {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
        />

        <CardLabel>{`${t("PTR_ALT_MOBILE_NUMBER")}`}</CardLabel>
          <MobileNumber
            value={alternateNumber}
            name="alternateNumber"
            onChange={(value) => setAltMobileNo({ target: { value } })}
            //disable={isUpdateProperty || isEditProperty}
            {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
          />
        <CardLabel>{`${t("PTR_FATHER_HUSBAND_NAME")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="fatherName"
          value={fatherName}
          onChange={setGuardiansName}
          //disable={isUpdateProperty || isEditProperty}
          ValidationRequired = {false}
          {...(validation = {
            isRequired: true,
            pattern: "^[a-zA-Z-.`' ]*$",
            type: "text",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
        />

        <CardLabel>{`${t("PTR_EMAIL_ID")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={true}
          optionKey="i18nKey"
          name="emailId"
          value={emailId}
          onChange={setOwnerEmail}
          //disable={isUpdateProperty || isEditProperty}
          ValidationRequired = {false}
          {...(validation = {
            isRequired: true,
            pattern: "[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$",
            type: "text",
            title: t("PTR_NAME_ERROR_MESSAGE"),
          })}
        />
        
        
      </div>
    </FormStep>
    </React.Fragment>
  );
};

export default PTRCitizenDetails;