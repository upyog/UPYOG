// Importing necessary components and hooks from external libraries and local files
import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, MobileNumber } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for form steps, input fields, and labels
import Timeline from "../components/EWASTETimeline"; // Component for displaying the timeline

// Main component for capturing the owner's details in the E-Waste module
const EWOwnerDetails = ({ t, config, onSelect, userType, formData }) => {
  let index = 0; // Index for managing multiple owners (if applicable)
  let validation = {}; // Variable to store validation rules

  const user = Digit.UserService.getUser().info; // Fetching the logged-in user's information

  // State variables to manage owner details
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

  // Handlers for updating state variables when input fields change
  function setOwnerName(e) {
    setName(e.target.value);
  }

  function setMobileNo(e) {
    setMobileNumber(e.target.value);
  }

  function setAltMobileNo(e) {
    setAltMobileNumber(e.target.value);
  }

  function setOwnerEmail(e) {
    setEmail(e.target.value);
  }

  // Function to handle the "Next" button click
  const goNext = () => {
    let owner = formData.ownerKey && formData.ownerKey[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, applicantName, mobileNumber, altMobileNumber, emailId }; // Combine form data with state variables
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index); // Pass the data to the parent component
    } else {
      ownerStep = { ...owner, applicantName, mobileNumber, altMobileNumber, emailId };
      onSelect(config.key, ownerStep, false, index);
    }
  };

  // Function to handle skipping the step
  const onSkip = () => onSelect();

  // Effect to automatically call goNext when owner details change
  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [applicantName, mobileNumber, emailId]);

  return (
    <React.Fragment>
      {/* Display the timeline if the user is on the citizen portal */}
      {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null}

      <FormStep
        config={config} // Configuration for the form step
        onSelect={goNext} // Function to call when the "Next" button is clicked
        onSkip={onSkip} // Function to call when the "Skip" button is clicked
        t={t} // Translation function
        isDisabled={!applicantName || !mobileNumber || !emailId} // Disable the "Next" button if required fields are empty
      >
        <div>
          {/* Input field for applicant name */}
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
              pattern: "^[a-zA-Z ]+$", // Validation pattern for alphabets and spaces
              type: "text",
              title: t("EW_ENTER_CORRECT_NAME"), // Error message for invalid input
            })}
          />

          {/* Input field for mobile number */}
          <CardLabel>{`${t("EWASTE_MOBILE_NUMBER")}`}</CardLabel>
          <MobileNumber
            value={mobileNumber}
            name="mobileNumber"
            onChange={(value) => setMobileNo({ target: { value } })}
            {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel" }} // Validation for a valid mobile number
          />

          {/* Input field for alternate mobile number */}
          <CardLabel>{`${t("EWASTE_ALT_MOBILE_NUMBER")}`}</CardLabel>
          <MobileNumber
            value={altMobileNumber}
            name="altmobileNumber"
            onChange={(value) => setAltMobileNo({ target: { value } })}
            {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel" }} // Validation for a valid mobile number
          />

          {/* Input field for email ID */}
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
              pattern: "[A-Za-z]{i}\\.[A-Za-z]{i}\\.[A-Za-z]{i}", // Validation pattern for email
              type: "email",
            })}
          />
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default EWOwnerDetails; // Exporting the component