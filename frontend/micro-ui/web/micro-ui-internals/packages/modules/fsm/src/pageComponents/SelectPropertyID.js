import React, { useState, useEffect } from "react";
import { FormStep, TextArea, LabelFieldPair, CardLabel, TextInput } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimelineInFSM";
const SelectPropertyID = ({ t, config, onSelect, formData, userType, setError: setFormError, clearErrors: clearFormErrors }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId(); 
  const [propertyID, setPropertyID] = useState(formData?.propertyID?.propertyID);
  console.log("formData", formData)
  
    const [error, setError] = useState("");
  
    const inputs = [
      {
        label: "CS_FILE_APPLICATION_PROPERTY_ID_LABEL",
        type: "text",
        name: "propertyID",
        validation: {
          maxLength: 10,
          minLength : 10,
        },
      },
    ];
  
    useEffect(() => {
      setPropertyID(formData?.additionalDetails?.propertyID);
    }, [formData?.additionalDetails?.propertyID]);
  
    function onChange(e) {
      if (e.target.value.length > 10 && e.target.length<10) {
        setError("CS_COMMON_PROPERTY_ID_MAX_LENGTH");
      } else {
        setError(null);
        setPropertyID(e.target.value);
        if (userType === "employee") {
          console.log("propID", propertyID)
          /*if (propertyID !== "undefined" && propertyID?.length === 0) setFormError(config.key, { type: "required", message: t("CORE_COMMON_REQUIRED_ERRMSG") });
          else if (propertyID !== "undefined" && propertyID?.length < 10 || propertyID?.length > 10 || !Number(propertyID)) setFormError(config.key, { type: "invalid", message: t("ERR_DEFAULT_INPUT_FIELD_MSG") });
          else (clearFormErrors(config.key) && setFormError(false));*/
          const value = e?.target?.value;
          const key = e?.target?.id;
          onSelect(config.key, { ...formData[config.key], propertyID: e.target.value });
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
            <TextInput className="form-field" id={input.name} value={propertyID} onChange={onChange} name={input.name || ""} {...input.validation} />
          </LabelFieldPair>
        );
      });
    }
    const onSkip = () => onSelect();
    return (
      <React.Fragment>
        <Timeline currentStep={1} flow="APPLY" />
        <FormStep
          t={t}
          config={{ ...config, inputs }}
          value={propertyID}
          onSelect={(data) => onSelect(config.key, { ...formData[config.key], ...data })}
          
          onChange={onChange}
          onSkip={onSkip}
          forcedError={t(error)}
        isDisabled={propertyID ? false : true}
        ></FormStep>
      </React.Fragment>
    );
};
export default SelectPropertyID;

