import { CardLabel,  LabelFieldPair, TextInput, CardLabelError } from "@egovernments/digit-ui-react-components";
import FormStep from "../../../../react-components/src/molecules/FormStep";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../components/TLTimeline";

const UID = ({ t, config, onSelect, value, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState, onBlur }) => {

  let index = window.location.href.split("/").pop();
  let validation = {};
  const onSkip = () => onSelect();
  let uid;
  let setUid;
  const [hidden, setHidden] = useState(true);
  const pattern = /^[a-zA-Z0-9-]*$/;
  if (!isNaN(index)) {
    [uid, setUid] = useState(formData?.originalData?.additionalDetails?.uid || "");
  } else {
    [uid, setUid] = useState(formData?.originalData?.additionalDetails?.uid || "");

  }
  const [error, setError] = useState(null);
  const [unitareaerror, setunitareaerror] = useState(null);
  const [areanotzeroerror, setareanotzeroerror] = useState(null);

  const { pathname } = useLocation();
  const presentInModifyApplication = pathname.includes("modify");
  function setUidzno(e) {
    setUid(e.target.value);
  }
  useEffect(() => {
    if (uid !== "undefined" && uid?.length == 15 && pattern.test(uid)) {
      setHidden(false);
    }
    else {
      setHidden(true);
    }
  }, [uid])

  function onChange(e) {
    setUid(e.target.value);
  }


  function goNext() {
    sessionStorage.setItem("uid", uid.i18nKey);
    onSelect("uid", { uid });

  };

  useEffect(() => {
    if (userType === "employee") {
      if (uid !== "undefined" && uid?.length === 0) setFormError(config.key, { type: "required", message: t("CORE_COMMON_REQUIRED_ERRMSG") });
      else if (uid !== "undefined" && uid?.length < 15 || uid?.length > 15 || !pattern.test(uid)) setFormError(config.key, { type: "invalid", message: t("ERR_DEFAULT_INPUT_FIELD_MSG") });
      else clearFormErrors(config.key);

      onSelect(config.key, uid);

    }
  }, [uid]);
  //  useEffect(()=>{
  //     if (window.location.href.includes("/citizen")){
  //       if (uid.length===0) setError(config.key, { type: "required", message: t("CORE_COMMON_REQUIRED_ERRMSG") });
  //      else if (!uid.length ==15 || !pattern.test(uid) ) setError(config.key, { type: "invalid", message: t("ERR_DEFAULT_INPUT_FIELD_MSG") });
  //     else clearFormErrors(config.key);

  //       onSelect(config.key, uid);

  //     }
  //   })

  useEffect(() => {
    if (presentInModifyApplication && userType === "employee") {
      console.log("elec123", formData)
      setUid(formData?.originalData?.additionalDetails?.uid)
    }
  }, []);

  const inputs = [
    {
      label: "PT_ELECTRICITY_UID_LABEL",
      type: "text",
      name: "electricity_uid",
      error: "ERR_HRMS_INVALID_ELECTRICITY_UID_NO",
      validation: {
        pattern: "/^[a-zA-Z0-9-]*$"
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
                //isMandatory={config.isMandatory}
                value={uid}
                onChange={onChange}
                //onChange={setElectricityNo}
                onSelect={goNext}
                placeholder={"Enter a valid 15-digit alphanumeric UID"}
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
        <CardLabel>{`${t("PT_ELECTRICITY_UID")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="uid"
          value={uid}
          onChange={setUidzno}
          placeholder={"Enter a valid 15-digit alphanumeric UID"}
          {...inputs[0].validation}
        // {...(validation = { pattern: "/^[a-zA-Z0-9-]*$", type: "text", title: t("PT_UNIQUE_ID_ERROR_MESSAGE") })}
        />
      </FormStep>
    </React.Fragment>
  );



};

export default UID;