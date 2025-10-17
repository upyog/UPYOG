import { CardLabel, FormStep, LabelFieldPair, TextInput, CardLabelError } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../components/TLTimeline";

const Area = ({ t, config, onSelect, value, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState, onBlur }) => {
  let validation = {};
  const onSkip = () => onSelect();
 const [floorarea, setfloorarea] = useState(formData.landArea?.floorarea);
  const [unitareaerror, setunitareaerror] = useState(null);


  function setPropertyfloorarea(e) {
    let value = e.target.value;

    // Allow only numbers with up to 2 decimals
    value = value.replace(/^(\d+(\.\d{0,2})?).*$/, "$1");
    setfloorarea(value);
  }
  // const onBlurArea = (e) => {
  //   setfloorarea((prev) => (prev ? parseFloat(prev).toFixed(2) : ""));
  // }
  const goNext = () => {
      onSelect("landArea", { floorarea });
  };

  function onChange(e) {
    if (e.target.value.length > 1024) {
      setunitareaerror("CS_COMMON_LANDMARK_MAX_LENGTH");
    } else {
      setunitareaerror(null);
      setfloorarea(e.target.value);
    }
  }



  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1}/> : null}
    <FormStep
      config={config}
      onChange={onChange}
      forcedError={t(unitareaerror) }
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={unitareaerror  || !floorarea}
      showErrorBelowChildren={true}
    >
      <CardLabel>{`${t("Land Area(sq ft)")}`}</CardLabel>
      <TextInput
        t={t}
        type={"number"}
        isMandatory={false}
        optionKey="i18nKey"
        name="floorarea"
        value={floorarea}
        step="0.01"
        onChange={setPropertyfloorarea}
        // onBlur={onBlurArea}
        {...(validation = { pattern: "^([0-9]){0,8}$", type: "number", title: t("PT_PLOT_SIZE_ERROR_MESSAGE") })}
      />
    </FormStep>
    </React.Fragment>
  );
};

export default Area;
