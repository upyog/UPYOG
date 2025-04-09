// Importing necessary components and hooks from external libraries and local files
import { CardLabel, CardLabelError, FormStep, LabelFieldPair, TextInput } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for form steps, labels, and input fields
import React, { useEffect, useState } from "react"; // React hooks for state and lifecycle management
import { useLocation } from "react-router-dom"; // Hook to access the current location
import Timeline from "../components/EWASTETimeline"; // Component for displaying the timeline

// Main component for selecting the pincode in the E-Waste module
const EWASTESelectPincode = ({ t, config, onSelect, formData = {}, userType, register, errors, setError, formState, clearErrors }) => {
  const tenants = Digit.Hooks.ew.useTenants(); // Fetching the list of tenants
  const { pathname } = useLocation(); // Extracting the current URL path
  const presentInModifyApplication = pathname.includes("modify"); // Check if the user is modifying an application

  // State to manage the pincode value
  const [pincode, setPincode] = useState(() => {
    if (presentInModifyApplication && userType === "employee") return formData?.originalData?.address?.pincode || ""; // Use original data if modifying
    return formData?.address?.pincode || ""; // Use existing form data or default to an empty string
  });

  // Input configuration for the pincode field
  const inputs = [
    {
      label: "EWASTE_ADDRESS_PINCODE", // Label for the pincode field
      type: "text", // Input type
      name: "pincode", // Input name
      validation: {
        minlength: 6, // Minimum length for the pincode
        maxlength: 6, // Maximum length for the pincode
        pattern: "[0-9]+", // Validation pattern for numeric input
        max: "999999", // Maximum value for the pincode
      },
    },
  ];

  const [pincodeServicability, setPincodeServicability] = useState(null); // State to manage pincode servicability errors
  const [error, setLocalError] = useState(""); // State to manage local error messages

  // Effect to update the pincode state when form data changes
  useEffect(() => {
    if (formData?.address?.pincode) {
      setPincode(formData.address.pincode);
    }
  }, [formData?.address?.pincode]);

  // Function to handle changes in the pincode input field
  function onChange(e) {
    setPincode(e.target.value); // Update the pincode state
    setPincodeServicability(null); // Reset pincode servicability errors
    setLocalError(""); // Reset local error messages
    let validPincode = Digit.Utils.getPattern("Pincode").test(e.target.value); // Validate the pincode format

    if (userType === "employee") {
      if (e.target.value && !validPincode) setLocalError(t("ERR_DEFAULT_INPUT_FIELD_MSG")); // Set error if pincode is invalid
      if (validPincode) {
        const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item.toString() === e.target.value)); // Check if the pincode is servicable
        if (!foundValue) setLocalError(t("EWASTE_COMMON_PINCODE_NOT_SERVICABLE")); // Set error if pincode is not servicable
      }
      onSelect(config.key, { ...formData.address, pincode: e.target.value }); // Pass the updated pincode to the parent component
    }
  }

  // Function to handle the "Next" button click
  const goNext = async (data) => {
    const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item == data?.pincode)); // Check if the pincode is servicable
    if (foundValue) {
      onSelect(config.key, { pincode }); // Pass the pincode to the parent component
    } else {
      setPincodeServicability("EWASTE_COMMON_PINCODE_NOT_SERVICABLE"); // Set servicability error if pincode is not valid
    }
  };

  // Rendering the component for employees
  if (userType === "employee") {
    return inputs?.map((input, index) => {
      return (
        <React.Fragment key={index}>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t(input.label)}</CardLabel>
            <div className="field">
              <TextInput
                key={input.name}
                value={pincode}
                onChange={onChange}
                {...input.validation} // Apply validation rules
                disable={presentInModifyApplication} // Disable input if modifying
                autoFocus={presentInModifyApplication} // Autofocus input if modifying
              />
            </div>
          </LabelFieldPair>
          {error ? <CardLabelError style={{ width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" }}>{error}</CardLabelError> : null}
        </React.Fragment>
      );
    });
  }

  // Function to handle skipping the step
  const onSkip = () => onSelect();

  // Rendering the component for citizens
  return (
    <React.Fragment>
      {/* Display the timeline if the user is on the citizen portal */}
      {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
      <FormStep
        t={t} // Translation function
        config={{ ...config, inputs }} // Configuration for the form step
        onSelect={goNext} // Function to call when the "Next" button is clicked
        _defaultValues={{ pincode }} // Default values for the form
        onChange={onChange} // Function to handle changes in the input field
        onSkip={onSkip} // Function to handle skipping the step
        forcedError={t(pincodeServicability)} // Display forced error messages
      ></FormStep>
    </React.Fragment>
  );
};

export default EWASTESelectPincode; // Exporting the component