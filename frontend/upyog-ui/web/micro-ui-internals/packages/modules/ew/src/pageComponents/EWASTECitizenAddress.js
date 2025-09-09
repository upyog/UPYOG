import { CardLabel, FormStep, TextInput } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/EWASTETimeline";

/**
 * Form component for capturing citizen address details in the E-Waste module.
 * Manages a multi-field address form with validation and automatic data saving.
 *
 * @param {Object} props Component properties
 * @param {Function} props.t Translation function
 * @param {Object} props.config Form configuration settings
 * @param {Function} props.onSelect Handler for form submission
 * @param {string} props.userType Type of user (citizen/employee)
 * @param {Object} props.formData Existing form data
 * @returns {JSX.Element} Address form component
 */
const EWASTECitizenAddress = ({ t, config, onSelect, userType, formData }) => {
  const onSkip = () => onSelect();
  let validation;
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger } = useForm();
  const formValue = watch();

  /**
   * State management for address form fields
   * Each field maintains its own state for controlled input behavior
   */
  const [street, setStreet] = useState(formData?.address?.street || "");
  const [addressLine1, setAddressLine1] = useState(formData?.address?.addressLine1 || "");
  const [addressLine2, setAddressLine2] = useState(formData?.address?.addressLine2 || "");
  const [landmark, setLandmark] = useState(formData?.address?.landmark || "");
  const [buildingName, setBuildingName] = useState(formData?.address?.buildingName || "");
  const [doorNo, setDoorNo] = useState(formData?.address?.doorNo || "");

  /**
   * Triggers form validation on component mount
   */
  useEffect(() => {
    trigger();
  }, []);

  /**
   * Syncs form data with parent component when values change
   * Prevents unnecessary updates using deep comparison
   */
  useEffect(() => {
    const keys = Object.keys(formValue);
    const part = {};
    keys.forEach((key) => (part[key] = formData[config.key]?.[key]));

    if (!_.isEqual(formValue, part)) {
      onSelect(config.key, { ...formData[config.key], ...formValue });
      trigger();
    }
  }, [formValue]);

  /**
   * Event handlers for form field updates
   */
  const selectStreet = (e) => setStreet(e.target.value);
  const selectDoorNo = (e) => setDoorNo(e.target.value);
  const selectBuilding = (e) => setBuildingName(e.target.value);
  const selectLandmark = (e) => setLandmark(e.target.value);
  const selectAddressLine1 = (e) => setAddressLine1(e.target.value);
  const selectAddressLine2 = (e) => setAddressLine2(e.target.value);

  /**
   * Handles form submission and data updates
   * Formats address data before passing to parent component
   */
  const goNext = () => {
    let owner = formData.address;
    let ownerStep = {
      ...owner,
      street,
      addressLine1,
      addressLine2,
      landmark,
      buildingName,
      doorNo
    };
    
    if (userType === "citizen") {
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
    } else {
      onSelect(config.key, ownerStep, false);
    }
  };

  /**
   * Automatically saves form data for citizen users
   */
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