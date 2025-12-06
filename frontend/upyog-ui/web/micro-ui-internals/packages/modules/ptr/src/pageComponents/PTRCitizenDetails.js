<<<<<<< HEAD
import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons, LabelFieldPair, Dropdown, Menu, MobileNumber } from "@upyog/digit-ui-react-components";
import { cardBodyStyle } from "../utils";
import { useLocation, useRouteMatch } from "react-router-dom";
import Timeline from "../components/PTRTimeline";

const PTRCitizenDetails
 = ({ t, config, onSelect, userType, formData, ownerIndex }) => {
  const { pathname: url } = useLocation();

  let index = 0
  
   
  let validation = {};

  const [applicantName, setName] = useState((formData.ownerss && formData.ownerss[index] && formData.ownerss[index].applicantName) || formData?.ownerss?.applicantName || "");
  const [emailId, setEmail] = useState((formData.ownerss && formData.ownerss[index] && formData.ownerss[index].emailId) || formData?.ownerss?.emailId || "");
  const [mobileNumber, setMobileNumber] = useState(
    (formData.ownerss && formData.ownerss[index] && formData.ownerss[index].mobileNumber) || formData?.ownerss?.mobileNumber || ""
  );
  const [alternateNumber, setAltMobileNumber] = useState(
    (formData.ownerss && formData.ownerss[index] && formData.ownerss[index].alternateNumber) || formData?.ownerss?.alternateNumber || ""
  );

  
  const [fatherName, setFatherOrHusbandName] = useState(
    (formData.ownerss && formData.ownerss[index] && formData.ownerss[index].fatherName) || formData?.ownerss?.fatherName || ""
  );
  
 

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

  
=======
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
>>>>>>> master-LTS

  function setOwnerName(e) {
    setName(e.target.value);
  }
  function setOwnerEmail(e) {
    setEmail(e.target.value);
  }
<<<<<<< HEAD
  

  function setMobileNo(e) {
    setMobileNumber(e.target.value);
  }
  
=======
  function setMobileNo(e) {
    setMobileNumber(e.target.value);
  }
>>>>>>> master-LTS
  function setAltMobileNo(e) {
    setAltMobileNumber(e.target.value);
  }
  function setGuardiansName(e) {
    setFatherOrHusbandName(e.target.value);
  }
  
<<<<<<< HEAD

  const goNext = () => {
    let owner = formData.ownerss && formData.ownerss[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, applicantName, mobileNumber,alternateNumber, fatherName, emailId};
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      
      ownerStep = { ...owner, applicantName,  mobileNumber,alternateNumber, fatherName,emailId };
      onSelect(config.key, ownerStep, false,index);
=======
  const goNext = () => {
    let owner = formData.ownerss;
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, applicantName, mobileNumber,alternateNumber, fatherName, emailId};
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
    } else {
      ownerStep = { ...owner, applicantName,  mobileNumber,alternateNumber, fatherName,emailId };
      onSelect(config.key, ownerStep, false);
>>>>>>> master-LTS
    }
  };

  const onSkip = () => onSelect();

<<<<<<< HEAD
  
  

=======
>>>>>>> master-LTS
  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [applicantName, mobileNumber, fatherName, emailId]);

 

  return (
    <React.Fragment>
<<<<<<< HEAD
    {
      window.location.href.includes("/citizen") ?
 <Timeline currentStep={1} />
    : null
    }
=======
    { <Timeline currentStep={1} />}
>>>>>>> master-LTS

    <FormStep
      config={config}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={!applicantName || !mobileNumber || !fatherName || !emailId}
    >
      <div>
<<<<<<< HEAD
        <CardLabel>{`${t("PTR_APPLICANT_NAME")}`}</CardLabel>
=======
        <CardLabel>{`${t("PTR_APPLICANT_NAME")}`} <span className="astericColor">*</span></CardLabel>
>>>>>>> master-LTS
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="applicantName"
          value={applicantName}
<<<<<<< HEAD
=======
          style={inputStyles}
>>>>>>> master-LTS
          onChange={setOwnerName}
          ValidationRequired = {true}
          {...(validation = {
            isRequired: true,
            pattern: "^[a-zA-Z ]+$",
            type: "tel",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
<<<<<<< HEAD
       
         
        />
       
        <CardLabel>{`${t("PTR_MOBILE_NUMBER")}`}</CardLabel>
=======
        />
       
        <CardLabel>{`${t("PTR_MOBILE_NUMBER")}`} <span className="astericColor">*</span></CardLabel>
>>>>>>> master-LTS
        <MobileNumber
          value={mobileNumber}
          name="mobileNumber"
          onChange={(value) => setMobileNo({ target: { value } })}
<<<<<<< HEAD
=======
          style={ {width: user.type === "EMPLOYEE" ? "49%" : "86%"}}
>>>>>>> master-LTS
          {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
        />

        <CardLabel>{`${t("PTR_ALT_MOBILE_NUMBER")}`}</CardLabel>
          <MobileNumber
            value={alternateNumber}
            name="alternateNumber"
            onChange={(value) => setAltMobileNo({ target: { value } })}
<<<<<<< HEAD
            {...{ required: false, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
          />
        <CardLabel>{`${t("PTR_FATHER_HUSBAND_NAME")}`}</CardLabel>
=======
            style={ {width: user.type === "EMPLOYEE" ? "49%" : "86%"}}
            {...{ required: false, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
          />
        <CardLabel>{`${t("PTR_FATHER_HUSBAND_NAME")}`} <span className="astericColor">*</span></CardLabel>
>>>>>>> master-LTS
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="fatherName"
<<<<<<< HEAD
=======
          style={inputStyles}
>>>>>>> master-LTS
          value={fatherName}
          onChange={setGuardiansName}
          ValidationRequired = {true}
          {...(validation = {
            isRequired: true,
<<<<<<< HEAD
            pattern: "^[a-zA-Z-.`' ]*$",
            type: "text",
            title: t("PTR_NAME_ERROR_MESSAGE"),
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
          ValidationRequired = {true}
          {...(validation = {
            isRequired: true,
            pattern: "[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$",
            type: "text",
            title: t("PTR_NAME_ERROR_MESSAGE"),
          })}
        />
=======
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
>>>>>>> master-LTS
        
        
      </div>
    </FormStep>
    </React.Fragment>
  );
};

export default PTRCitizenDetails;