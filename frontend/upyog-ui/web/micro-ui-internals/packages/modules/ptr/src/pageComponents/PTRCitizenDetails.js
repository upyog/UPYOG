/**
 * @component PTRCitizenDetails
 * @description
 * This component handles the collection of citizen personal details in a multi-step form for
 * the Pet Registration (PTR) application. It captures and validates essential contact information
 * from the applicant.
 * 
 * Features:
 * - Auto-fills user data when available (from user profile)
 * - Supports both new applications and renewal applications
 * - Different behavior for citizen and employee user types
 * - Form validation for required fields with appropriate patterns
 * - Timeline integration to show current step in the application process
 * 
 * Form fields include:
 * - Applicant Name (required)
 * - Mobile Number (required with validation)
 * - Alternate Mobile Number (optional)
 * - Father/Husband Name (required)
 * - Email ID (required with validation)
 * 
 * The component automatically updates the parent form state when fields change
 * for citizen users, while employee users need to explicitly submit the form.
 */

import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, MobileNumber } from "@upyog/digit-ui-react-components";
import Timeline from "../components/PTRTimeline";

const PTRCitizenDetails = ({ t, config, onSelect, userType, formData, renewApplication }) => {
  const user = Digit.UserService.getUser().info; // service to fetch user information
  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "86%" };
  let validation = {};

  // added data from renewapplication, renders data if there is data in renewapplication
  const [applicantName, setName] = useState(renewApplication?.applicantName ||( user.type === "EMPLOYEE" ?"":user?.name) || formData?.ownerss?.applicantName || "");
  const [emailId, setEmail] = useState(renewApplication?.emailId || formData?.ownerss?.emailId || "");
  const [mobileNumber, setMobileNumber] = useState(renewApplication?.mobileNumber || (user.type === "EMPLOYEE" ?"":user?.mobileNumber) || formData?.ownerss?.mobileNumber || "");
  const [alternateNumber, setAltMobileNumber] = useState(formData?.ownerss?.alternateNumber || "");
  const [fatherName, setFatherOrHusbandName] = useState(renewApplication?.fatherName || formData?.ownerss?.fatherName || "");

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
    let owner = formData.ownerss;
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, applicantName, mobileNumber,alternateNumber, fatherName, emailId};
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
    } else {
      ownerStep = { ...owner, applicantName,  mobileNumber,alternateNumber, fatherName,emailId };
      onSelect(config.key, ownerStep, false);
    }
  };

  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [applicantName, mobileNumber, fatherName, emailId]);

 

  return (
    <React.Fragment>
    { <Timeline currentStep={1} />}

    <FormStep
      config={config}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={!applicantName || !mobileNumber || !fatherName || !emailId}
    >
      <div>
        <CardLabel>{`${t("PTR_APPLICANT_NAME")}`} <span className="astericColor">*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="applicantName"
          value={applicantName}
          style={inputStyles}
          onChange={setOwnerName}
          ValidationRequired = {true}
          {...(validation = {
            isRequired: true,
            pattern: "^[a-zA-Z ]+$",
            type: "tel",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
        />
       
        <CardLabel>{`${t("PTR_MOBILE_NUMBER")}`} <span className="astericColor">*</span></CardLabel>
        <MobileNumber
          value={mobileNumber}
          name="mobileNumber"
          onChange={(value) => setMobileNo({ target: { value } })}
          style={ {width: user.type === "EMPLOYEE" ? "49%" : "86%"}}
          {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
        />

        <CardLabel>{`${t("PTR_ALT_MOBILE_NUMBER")}`}</CardLabel>
          <MobileNumber
            value={alternateNumber}
            name="alternateNumber"
            onChange={(value) => setAltMobileNo({ target: { value } })}
            style={ {width: user.type === "EMPLOYEE" ? "49%" : "86%"}}
            {...{ required: false, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
          />
        <CardLabel>{`${t("PTR_FATHER_HUSBAND_NAME")}`} <span className="astericColor">*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="fatherName"
          style={inputStyles}
          value={fatherName}
          onChange={setGuardiansName}
          ValidationRequired = {true}
          {...(validation = {
            isRequired: true,
            pattern: "^[a-zA-Z ]+$",
            type: "tel",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
        />
      <CardLabel>{`${t("PTR_EMAIL_ID")}`} <span className="astericColor">*</span></CardLabel>
      <TextInput
        t={t}
        type={"email"}
        isMandatory={true}
        optionKey="i18nKey"
        name="emailId"
        value={emailId}
        style={inputStyles}
        onChange={setOwnerEmail}
        ValidationRequired={true}
        {...(validation = {
          isRequired: true,
          pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z]+\\.[a-zA-Z]{3,4}$",
          type: "email",
          title: t("PT_NAME_ERROR_MESSAGE"),
        })}
      />
        
        
      </div>
    </FormStep>
    </React.Fragment>
  );
};

export default PTRCitizenDetails;