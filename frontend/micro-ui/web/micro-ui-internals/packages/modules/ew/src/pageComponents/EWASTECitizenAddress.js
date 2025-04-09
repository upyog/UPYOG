// Importing necessary components and hooks from external libraries and local files
import { CardLabel, FormStep, TextInput } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for form steps and input fields
import _ from "lodash"; // Utility library for deep comparison
import React, { useEffect, useState } from "react"; // React hooks for state and lifecycle management
import { Controller, useForm } from "react-hook-form"; // React Hook Form for managing form state
import Timeline from "../components/EWASTETimeline"; // Component for displaying the timeline

// Main component for capturing the citizen's address details in the E-Waste module
const EWASTECitizenAddress = ({ t, config, onSelect, userType, formData }) => {
  const onSkip = () => onSelect(); // Function to handle skipping the step
  let validation; // Variable to store validation rules
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" }); // State to manage focus on input fields
  const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger } = useForm(); // React Hook Form methods
  const formValue = watch(); // Watching form values for changes

  // State variables to manage address fields
  const [street, setStreet] = useState(formData?.address?.street || "");
  const [addressLine1, setAddressLine1] = useState(formData?.address?.addressLine1 || "");
  const [addressLine2, setAddressLine2] = useState(formData?.address?.addressLine2 || "");
  const [landmark, setLandmark] = useState(formData?.address?.landmark || "");
  const [buildingName, setBuildingName] = useState(formData?.address?.buildingName || "");
  const [doorNo, setDoorNo] = useState(formData?.address?.doorNo || "");

  // Trigger validation on component mount
  useEffect(() => {
    trigger();
  }, []);

  // Effect to update form data when form values change
  useEffect(() => {
    const keys = Object.keys(formValue);
    const part = {};
    keys.forEach((key) => (part[key] = formData[config.key]?.[key]));

    if (!_.isEqual(formValue, part)) {
      onSelect(config.key, { ...formData[config.key], ...formValue });
      trigger();
    }
  }, [formValue]);

  // Handlers for updating state variables when input fields change
  const selectStreet = (e) => setStreet(e.target.value);
  const selectDoorNo = (e) => setDoorNo(e.target.value);
  const selectBuilding = (e) => setBuildingName(e.target.value);
  const selectLandmark = (e) => setLandmark(e.target.value);
  const selectAddressLine1 = (e) => setAddressLine1(e.target.value);
  const selectAddressLine2 = (e) => setAddressLine2(e.target.value);

  // Function to handle the "Next" button click
  const goNext = () => {
    let owner = formData.address;
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, street, addressLine1, addressLine2, landmark, buildingName, doorNo };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
    } else {
      ownerStep = { ...owner, street, addressLine1, addressLine2, landmark, buildingName, doorNo };
      onSelect(config.key, ownerStep, false);
    }
  };

  // Effect to automatically call goNext when address fields change
  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [street, addressLine1, addressLine2, landmark, buildingName, doorNo]);

  return (
    <React.Fragment>
      {/* Display the timeline if the user is on the citizen portal */}
      {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
      <FormStep
        config={{ ...config }} // Configuration for the form step
        onSelect={goNext} // Function to call when the "Next" button is clicked
        onSkip={onSkip} // Function to call when the "Skip" button is clicked
        isDisabled={addressLine1 == "" || doorNo == ""} // Disable the "Next" button if required fields are empty
        t={t} // Translation function
      >
        {/* Input field for street name */}
        <CardLabel>{`${t("EWASTE_STREET_NAME")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="street"
          onChange={selectStreet}
          value={street}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}
        />

        {/* Input field for house number */}
        <CardLabel>{`${t("EWASTE_HOUSE_NO")}`}<span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="doorNo"
          onChange={selectDoorNo}
          value={doorNo}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}
          ValidationRequired={true}
          {...(validation = {
            isRequired: true,
            pattern: "^[0-9]*$",
            type: "text",
            title: t("EW_HOUSE_NO_ERROR_MESSAGE"),
          })}
        />

        {/* Input field for house name */}
        <CardLabel>{`${t("EWASTE_HOUSE_NAME")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="buildingName"
          onChange={selectBuilding}
          value={buildingName}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}
        />

        {/* Input field for address line 1 */}
        <CardLabel>{`${t("EWASTE_ADDRESS_LINE1")}`}<span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="addressLine1"
          onChange={selectAddressLine1}
          value={addressLine1}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}
        />

        {/* Input field for address line 2 */}
        <CardLabel>{`${t("EWASTE_ADDRESS_LINE2")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="addressLine2"
          onChange={selectAddressLine2}
          value={addressLine2}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}
        />

        {/* Input field for landmark */}
        <CardLabel>{`${t("EWASTE_landmark")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="landmark"
          onChange={selectLandmark}
          value={landmark}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}
        />
      </FormStep>
    </React.Fragment>
  );
};

export default EWASTECitizenAddress; // Exporting the component