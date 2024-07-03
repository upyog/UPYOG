import React, { useState, useEffect } from "react";
import { TextArea, LabelFieldPair, CardLabel, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import FormStep from "../../../../react-components/src/molecules/FormStep"
import Timeline from "../components/TLTimelineInFSM";
const SelectPropertyID = ({ t, config, onSelect, formData, userType, setError: setFormError, clearErrors: clearFormErrors }) => {
const [propertyID, setPropertyID] = useState(formData?.propertyID?.propertyID ||"");
const [disable,setDisable]=useState(false)
const [error, setError] = useState("");
const inputs = [
  {
    type: "text",
    name: "propertyID",
    placeholder:"Enter a valid property ID",
    validation: {
    maxLength: 256,
    },
  },
  ];
  const goNext=()=>{
    onSelect(config.key, { ...formData[config.key], ...data })
  }
  useEffect(()=>{

if(window.location.href.includes("employee/fsm/new-application"))
{
  setDisable(false)
}
else if(window.location.href.includes("employee"))
{
  setDisable(true)
}
  },[])
  useEffect(() => {
    setPropertyID(formData?.additionalDetails?.propertyID);
  }, [formData?.additionalDetails?.propertyID]);
  function onChange(e) {
    setPropertyID(e.target.value);
    if (userType === "employee") {
      onSelect(config.key, { ...formData[config.key], propertyID: e.target.value });
    }
  }
  if (userType === "employee") {
    return (
      <TextInput className="form-field" id="propertyID" value={propertyID} onChange={onChange} t={t} disable={disable}/>
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
         placeholder={inputs.placeholder}
         onChange={onChange}
         onSkip={onSkip}
         forcedError={t(error)}
         isDisabled={false}
       ></FormStep>
      </React.Fragment>
    );
};
export default SelectPropertyID;
