import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, MobileNumber } from "@upyog/digit-ui-react-components";
import Timeline from "../components/EWASTETimeline";

/**
 * Form component for capturing owner details in the E-Waste module.
 * Manages personal information including name, contact details, and email.
 * Supports both citizen and employee user types with different behaviors.
 *
 * @param {Object} props Component properties
 * @param {Function} props.t Translation function
 * @param {Object} props.config Form configuration settings
 * @param {Function} props.onSelect Handler for form submission
 * @param {string} props.userType Type of user (citizen/employee)
 * @param {Object} props.formData Existing form data
 * @returns {JSX.Element} Owner details form component
 */
const EWOwnerDetails = ({ t, config, onSelect, userType, formData }) => {
  let index = 0;
  let validation = {};

  const user = Digit.UserService.getUser().info;

  /**
   * State management for owner details form fields
   * Initializes with existing data or defaults
   */
  const [applicantName, setName] = useState(
    (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].applicantName) || formData?.ownerKey?.applicantName || ""
  );
  const [emailId, setEmail] = useState(
    (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].emailId) || formData?.ownerKey?.emailId || ""
  );
  const [mobileNumber, setMobileNumber] = useState(
    (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].mobileNumber) || formData?.ownerKey?.mobileNumber || user?.mobileNumber
  );
  const [altMobileNumber, setAltMobileNumber] = useState(
    (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].altMobileNumber) || formData?.ownerKey?.altmobileNumber || ""
  );

  /**
   * Form field change handlers
   * Update state with new values while maintaining data format
   */
  const setOwnerName = e => setName(e.target.value);
  const setMobileNo = e => setMobileNumber(e.target.value);
  const setAltMobileNo = e => setAltMobileNumber(e.target.value);
  const setOwnerEmail = e => setEmail(e.target.value);

  /**
   * Processes form submission by combining state values
   * Handles different data structures for citizen and employee users
   */
  const goNext = () => {
    let owner = formData.ownerKey && formData.ownerKey[index];
    let ownerStep = { ...owner, applicantName, mobileNumber, altMobileNumber, emailId };
    
    if (userType === "citizen") {
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      onSelect(config.key, ownerStep, false, index);
    }
  };

  const onSkip = () => onSelect();

  /**
   * Auto-saves form data for citizen users when fields change
   */
  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [applicantName, mobileNumber, emailId]);

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null}
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!applicantName || !mobileNumber || !emailId}
      >
        <div>
          <CardLabel>{`${t("EWASTE_APPLICANT_NAME")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="applicantName"
            value={applicantName}
            onChange={setOwnerName}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z ]+$",
              type: "text",
              title: t("EW_ENTER_CORRECT_NAME"),
            })}
          />

          <CardLabel>{`${t("EWASTE_MOBILE_NUMBER")}`}</CardLabel>
          <MobileNumber
            value={mobileNumber}
            name="mobileNumber"
            onChange={(value) => setMobileNo({ target: { value } })}
            {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel" }}
          />

          <CardLabel>{`${t("EWASTE_ALT_MOBILE_NUMBER")}`}</CardLabel>
          <MobileNumber
            value={altMobileNumber}
            name="altmobileNumber"
            onChange={(value) => setAltMobileNo({ target: { value } })}
            {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel" }}
          />

          <CardLabel>{`${t("EWASTE_EMAIL_ID")}`}</CardLabel>
          <TextInput
            t={t}
            type={"email"}
            isMandatory={true}
            optionKey="i18nKey"
            name="emailId"
            value={emailId}
            onChange={setOwnerEmail}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "[A-Za-z]{i}\\.[A-Za-z]{i}\\.[A-Za-z]{i}",
              type: "email",
            })}
          />
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default EWOwnerDetails;