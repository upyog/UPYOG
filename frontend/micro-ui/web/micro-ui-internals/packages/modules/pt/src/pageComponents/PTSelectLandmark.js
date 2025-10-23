import React, { useState, useEffect } from "react";
import { FormStep, TextArea, LabelFieldPair, CardLabel } from "@upyog/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const PTSelectLandmark = ({ t, config, onSelect, formData, userType }) => {
  const [landmark, setLandmark] = useState();

  const [error, setError] = useState("");

  const inputs = [
    {
      label: "ES_NEW_APPLICATION_LOCATION_LANDMARK",
      type: "textarea",
      name: "landmark",
      validation: {
        maxLength: 1024,
      },
    },
  ];

  useEffect(() => {
    setLandmark(formData?.address?.landmark);
  }, [formData?.address?.landmark]);

  function onChange(e) {
    if (e.target.value.length > 1024) {
      setError("CS_COMMON_LANDMARK_MAX_LENGTH");
    } else {
      setError(null);
      setLandmark(e.target.value);
      if (userType === "employee") {
        const value = e?.target?.value;
        const key = e?.target?.id;
        onSelect(config.key, { ...formData[config.key], landmark: e.target.value });
      }
    }
  }

  if (userType === "employee") {
    return inputs?.map((input, index) => {
      return (
        <LabelFieldPair key={index}>
          <CardLabel className="card-label-smaller">
            {t(input.label)}
            {config.isMandatory ? " * " : null}
          </CardLabel>
          <TextArea className="form-field" id={input.name} value={landmark} onChange={onChange} name={input.name || ""} {...input.validation} />
        </LabelFieldPair>
      );
    });
  }
  const onSkip = () => onSelect();

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1}/> : null}
      <FormStep
        config={{ ...config, inputs }}
        value={landmark}
        onChange={onChange}
        onSelect={(data) => onSelect(config.key, { ...formData[config.key], ...data })}
        onSkip={onSkip}
        t={t}
        forcedError={t(error)}
      ></FormStep>
    </React.Fragment>
  );
};

export default PTSelectLandmark;
