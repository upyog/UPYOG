import { CardLabel,  LabelFieldPair, TextInput, CardLabelError } from "@upyog/digit-ui-react-components";
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
  //const pattern = /^[a-zA-Z0-9-]*$/;
  if (!isNaN(index)) {
    [uid, setUid] = useState(formData?.uid?.uid || "");
  } else {
    [uid, setUid] = useState(formData?.uid?.uid || "");

  }
  const [error, setError] = useState(null);
  const { pathname } = useLocation();
  const presentInModifyApplication = pathname.includes("modify");
  useEffect(() => {
    validateUID();
  }, [uid])

  const onChange=(e)=> {
    setUid(e.target.value);
    validateUID();
  }


  const goNext=()=> {
    sessionStorage.setItem("uid", uid.i18nKey);
    onSelect("uid", {uid});

  };

  useEffect(() => {
    if (userType === "employee") {
      if (uid !== "undefined" && uid?.length === 0) setFormError(config.key, { type: "required", message: t("CORE_COMMON_REQUIRED_ERRMSG") });
      else if (uid !== "undefined" && (!/^[a-zA-Z0-9-]{0,15}$/.test(uid) || uid?.length !== 15)) setFormError(config.key, { type: "invalid", message: t("ERR_DEFAULT_INPUT_FIELD_MSG") });
      else clearFormErrors(config.key);

      onSelect(config.key, uid);

    }
  }, [uid]);

  useEffect(() => {
    if (presentInModifyApplication && userType === "employee") {
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
        pattern: "/^[a-zA-Z0-9-]*$",
        required: true,
        minLength: 15,
        maxLength: 15
      }
    },

  ];
  const validateUID=()=>{
    if(/^[a-zA-Z0-9-]{0,15}$/.test(uid) || uid===""){
      setError(" ");
    }
   
  };
  const handleUIDChange=(e)=>{
    const value=e.target.value;
      if(/^[a-zA-Z0-9-]{0,15}$/.test(value)|| value===""){
        onChange(e);
        validateUID();
      }
      else{
        setError("ERR_DEFAULT_INPUT_FIELD_MSG");
      }

  }



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
                onChange={handleUIDChange}
                //onChange={setElectricityNo}
                onSelect={goNext}
                placeholder={"Enter a valid 15-digit alphanumeric characters UID"}
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
        onChange={handleUIDChange}
        onSelect={goNext}
        onSkip={onSkip}
        defaultValues={ formData?.uid?.uid}
        t={t}
        isDisabled={uid.length===15|| formData?.uid?.uid ? false:true}
        showErrorBelowChildren={true}
      >
        <CardLabel>{`${t("PT_ELECTRICITY_UID")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="uid"
          value={uid || formData?.uid?.uid}
          onChange={handleUIDChange}
          placeholder={"Enter a valid 15-digit alphanumeric characters UID"}
          {...inputs[0].validation}
        // {...(validation = { pattern: "/^[a-zA-Z0-9-]*$", type: "text", title: t("PT_UNIQUE_ID_ERROR_MESSAGE") })}
        />
      </FormStep>
    </React.Fragment>
  );
  };

export default UID;