import { CardLabel, FormStep, LabelFieldPair, TextInput, CardLabelError } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../components/TLTimeline";

const Electricity = ({ t, config, onSelect, value, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState, onBlur }) => {
  //let index = window.location.href.charAt(window.location.href.length - 1);
  let index = window.location.href.split("/").pop();
  let validation = {};
  const onSkip = () => onSelect();

  let electricity;
  let setElectricity;
  const [hidden, setHidden] = useState(true);
  if (!isNaN(index)) {
    [electricity, setElectricity] = useState(formData?.originalData?.additionalDetails?.electricity || "");
  } else {
    [electricity, setElectricity] = useState(formData?.originalData?.additionalDetails?.electricity || "");
  }
  const [error, setError] = useState(null);
  const [unitareaerror, setunitareaerror] = useState(null);
  const [areanotzeroerror, setareanotzeroerror] = useState(null);

  const { pathname } = useLocation();
  const presentInModifyApplication = pathname.includes("modify");
  function setElectricityNo(e) {
    setElectricity(e.target.value);

  }
  useEffect(() => {
    electricity.length == 10 ? setHidden(false) : setHidden(true);
  }, [electricity])
  function onChange(e) {
    setElectricity(e.target.value);

  }

  //}
  function goNext() {
    console.log("eleccc", electricity)
    sessionStorage.setItem("electricity", electricity.i18nKey);
    onSelect("electricity", { electricity });

  };


  useEffect(() => {
    if (userType === "employee") {
      if (electricity !== "undefined" && electricity?.length === 0) setFormError(config.key, { type: "required", message: t("CORE_COMMON_REQUIRED_ERRMSG") });
      else if (electricity !== "undefined" && electricity?.length < 10 || electricity?.length > 10 || !Number(electricity)) setFormError(config.key, { type: "invalid", message: t("ERR_DEFAULT_INPUT_FIELD_MSG") });
      else clearFormErrors(config.key);

      onSelect(config.key, electricity);
      //onSelect("electricity", electricity);
    }
  }, [electricity]);

  useEffect(() => {
    if (presentInModifyApplication && userType === "employee") {

      setElectricity(formData?.originalData?.additionalDetails?.electricity)
    }
  }, []);

  const inputs = [
    {
      label: "PT_ELECTRICITY_LABEL",
      type: "text",
      name: "electricity",
      error: "ERR_HRMS_INVALID_ELECTRICITY_NO",
      validation: {
        required: true,
        minLength: 10,
        maxLength: 10
      }
    },


  ];

  if (userType === "employee") {
    return inputs?.map((input, index) => {
      return (
        <React.Fragment>
          <LabelFieldPair key={index}>
            <CardLabel className="card-label-smaller">{t(input.label) + " *"}</CardLabel>
            <div className="field">

              <TextInput
                key={input.name}
                id={input.name}
                isMandatory={config.isMandatory}
                value={electricity}
                onChange={onChange}
                //onChange={setElectricityNo}
                onSelect={goNext}
                {...input.validation}
                onBlur={onBlur}

              // autoFocus={presentInModifyApplication}
              />

            </div>
          </LabelFieldPair>
          {formState.touched[config.key] ? (
            <CardLabelError style={{ width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" }}>
              {formState.errors?.[config.key]?.message}
            </CardLabelError>
          ) : null}
        </React.Fragment>
      );
    });
  }
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}
      <FormStep
        config={config}
        onChange={onChange}

        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={hidden}
        showErrorBelowChildren={true}
      >
        <CardLabel>{`${t("PT_ELECTRICITY")}`}</CardLabel>
        <TextInput
          t={t}
          type={"number"}
          isMandatory={false}
          optionKey="i18nKey"
          name="electricity"
          value={electricity}
          onChange={setElectricityNo}
          {...(validation = {
            required: true,
            minLength: 10,
            maxLength: 10,
          })}
        />
      </FormStep>
    </React.Fragment>
  );



};

export default Electricity;