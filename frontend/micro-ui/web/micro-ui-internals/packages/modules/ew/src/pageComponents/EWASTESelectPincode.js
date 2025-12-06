import { CardLabel, CardLabelError, FormStep, LabelFieldPair, TextInput } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../components/EWASTETimeline";

/**
 * Form component for capturing and validating pincodes in the E-Waste module.
 * Provides different interfaces for citizens and employees with real-time validation.
 * 
 * @param {Object} props Component properties
 * @param {Function} props.t Translation function
 * @param {Object} props.config Form configuration object
 * @param {Function} props.onSelect Callback for form submission
 * @param {Object} props.formData Current form state
 * @param {string} props.userType Type of user (citizen/employee)
 * @param {Function} props.register Form field registration function
 * @param {Object} props.errors Form validation errors
 * @param {Function} props.setError Error setting function
 * @param {Object} props.formState Form validation state
 * @param {Function} props.clearErrors Error clearing function
 * @returns {JSX.Element} Pincode input form
 */
const EWASTESelectPincode = ({ t, config, onSelect, formData = {}, userType, register, errors, setError, formState, clearErrors }) => {
  const tenants = Digit.Hooks.ew.useTenants();
  const { pathname } = useLocation();
  const presentInModifyApplication = pathname.includes("modify");

  const [pincode, setPincode] = useState(() => {
    if (presentInModifyApplication && userType === "employee") return formData?.originalData?.address?.pincode || "";
    return formData?.address?.pincode || "";
  });

  /**
   * Configuration for pincode input field validation
   */
  const inputs = [
    {
      label: "EWASTE_ADDRESS_PINCODE",
      type: "text",
      name: "pincode",
      validation: {
        minlength: 6,
        maxlength: 6,
        pattern: "[0-9]+",
        max: "999999",
      },
    },
  ];

  const [pincodeServicability, setPincodeServicability] = useState(null);
  const [error, setLocalError] = useState("");

  /**
   * Updates pincode when form data changes externally
   */
  useEffect(() => {
    if (formData?.address?.pincode) {
      setPincode(formData.address.pincode);
    }
  }, [formData?.address?.pincode]);

  /**
   * Handles pincode input changes with validation
   * 
   * @param {Event} e Change event from input field
   */
  function onChange(e) {
    setPincode(e.target.value);
    setPincodeServicability(null);
    setLocalError("");
    let validPincode = Digit.Utils.getPattern("Pincode").test(e.target.value);

    if (userType === "employee") {
      if (e.target.value && !validPincode) setLocalError(t("ERR_DEFAULT_INPUT_FIELD_MSG"));
      if (validPincode) {
        const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item.toString() === e.target.value));
        if (!foundValue) setLocalError(t("EWASTE_COMMON_PINCODE_NOT_SERVICABLE"));
      }
      onSelect(config.key, { ...formData.address, pincode: e.target.value });
    }
  }

  /**
   * Validates and processes form submission
   * 
   * @param {Object} data Form data to validate
   */
  const goNext = async (data) => {
    const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item == data?.pincode));
    if (foundValue) {
      onSelect(config.key, { pincode });
    } else {
      setPincodeServicability("EWASTE_COMMON_PINCODE_NOT_SERVICABLE");
    }
  };

  if (userType === "employee") {
    return inputs?.map((input, index) => (
      <React.Fragment key={index}>
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t(input.label)}</CardLabel>
          <div className="field">
            <TextInput
              key={input.name}
              value={pincode}
              onChange={onChange}
              {...input.validation}
              disable={presentInModifyApplication}
              autoFocus={presentInModifyApplication}
            />
          </div>
        </LabelFieldPair>
        {error ? <CardLabelError style={{ width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" }}>{error}</CardLabelError> : null}
      </React.Fragment>
    ));
  }

  const onSkip = () => onSelect();

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
      <FormStep
        t={t}
        config={{ ...config, inputs }}
        onSelect={goNext}
        _defaultValues={{ pincode }}
        onChange={onChange}
        onSkip={onSkip}
        forcedError={t(pincodeServicability)}
      />
    </React.Fragment>
  );
};

export default EWASTESelectPincode;