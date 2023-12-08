import React, { useState, useEffect } from "react";
import { FormStep, TextArea, LabelFieldPair, CardLabel, TextInput } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimelineInFSM";
const SelectPropertyID = ({ t, config, onSelect, formData, userType, setError: setFormError, clearErrors: clearFormErrors }) => {
const [propertyID, setPropertyID] = useState(formData?.propertyID?.propertyID);
const [error, setError] = useState("");
const inputs = [
  {
    type: "text",
    name: "propertyID",
    validation: {
    maxLength: 10,
    minLength : 10,
    },
  },
  ];
  const goNext=()=>{
    onSelect(config.key, { ...formData[config.key], ...data })
  }
  useEffect(() => {
    setPropertyID(formData?.additionalDetails?.propertyID);
  }, [formData?.additionalDetails?.propertyID]);
  function onChange(e) {
    if (e.target.value.length !==10) {
      setError("ERR_DEFAULT_INPUT_FIELD_MSG")
     
  } else {
      setPropertyID(e.target.value);
      if (userType === "employee") {
        /*if (propertyID !== "undefined" && propertyID?.length === 0) setFormError(config.key, { type: "required", message: t("CORE_COMMON_REQUIRED_ERRMSG") });
          else if (propertyID !== "undefined" && propertyID?.length < 10 || propertyID?.length > 10 || !Number(propertyID)) setFormError(config.key, { type: "invalid", message: t("ERR_DEFAULT_INPUT_FIELD_MSG") });
          else (clearFormErrors(config.key) && setFormError(false));*/
          onSelect(config.key, { ...formData[config.key], propertyID: e.target.value });
        }
      }
    }
    if (userType === "employee") {
        return (
          <TextInput className="form-field" id="propertyID" value={propertyID} onChange={onChange} t={t} />
         );
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
