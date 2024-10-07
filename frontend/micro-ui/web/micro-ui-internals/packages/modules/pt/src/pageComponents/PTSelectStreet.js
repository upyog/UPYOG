import { CardLabel, FormStep, LabelFieldPair, TextInput ,CardLabelError} from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/TLTimeline";

const PTSelectStreet = ({ t, config, onSelect, userType, formData, formState, setError, clearErrors }) => {
  const onSkip = () => onSelect();
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger } = useForm();
  const formValue = watch();
  const { errors } = localFormState;
  const checkLocation = window.location.href.includes("tl/new-application") || window.location.href.includes("tl/renew-application-details");
  const isRenewal = window.location.href.includes("edit-application") || window.location.href.includes("tl/renew-application-details");
  const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };
  const [error, setErros]= useState(false)
  let inputs;
  if (window.location.href.includes("tl")) {
    inputs = config.inputs;
    config.inputs[0].disable = window.location.href.includes("edit-application");
    config.inputs[1].disable = window.location.href.includes("edit-application");
  } else {
    inputs = [
      {
        label: "PT_PROPERTY_ADDRESS_STREET_NAME",
        type: "text",
        name: "street",
        isMandatory:true,
        validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          // maxlength: 256,
          required:true,
          title: t("CORE_COMMON_STREET_INVALID"),
        },
      },
      {
        label: "PT_PROPERTY_ADDRESS_HOUSE_NO",
        type: "text",
        name: "doorNo",
        isMandatory:true,
        validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          // maxlength: 256,
          required:true,
          title: t("CORE_COMMON_DOOR_INVALID"),
        },
      },
    ];
  }

  const convertValidationToRules = ({ validation, name, messages }) => {
    if (validation) {
      let { pattern: valPattern, maxlength, minlength, required: valReq } = validation || {};
      let pattern = (value) => {
        if (valPattern) {
          if (valPattern instanceof RegExp) return valPattern.test(value) ? true : messages?.pattern || `${name.toUpperCase()}_PATTERN`;
          else if (typeof valPattern === "string")
            return new RegExp(valPattern)?.test(value) ? true : messages?.pattern || `${name.toUpperCase()}_PATTERN`;
        }
        return true;
      };
      let maxLength = (value) => (maxlength ? (value?.length <= maxlength ? true : messages?.maxlength || `${name.toUpperCase()}_MAXLENGTH`) : true);
      let minLength = (value) => (minlength ? (value?.length >= minlength ? true : messages?.minlength || `${name.toUpperCase()}_MINLENGTH`) : true);
      let required = (value) => (valReq ? (!value ? true : `${name.toUpperCase()}_REQUIRED` || messages?.required ) : true);
      return { pattern, required, minLength, maxLength };
    }
    return {};
  };
  useEffect(() => {
    trigger();
  }, []);
useEffect(() =>{
console.log("dddddddddd",formData, setError, clearErrors,setLocalError)
if(formData?.address?.doorNo !== "" &&formData?.address?.street !== "")
{
  //setError(config?.key, { type: errors })
  setErros(false)
  
}
else {
  setErros(true)

  
}
//setErros()
},[formState])
  useEffect(() => {
    if (userType === "employee") {
      if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors)) setError(config.key, { type: errors });
      else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
    }
  }, [errors]);

  useEffect(() => {
    const keys = Object.keys(formValue);
    const part = {};
    keys.forEach((key) => (part[key] = formData[config.key]?.[key]));

    if (!_.isEqual(formValue, part)) {
      onSelect(config.key, { ...formData[config.key], ...formValue });
      trigger();
    }
  }, [formValue]);
const setValueForm =(config,data)=>{
  if(data?.doorNo && data?.street    )
  {
    onSelect(config, data)
  }
  else {
    alert("please fill all values")
  }
  
}
  if (userType === "employee") {
    return inputs?.map((input, index) => {
      return (
        <LabelFieldPair key={index}>
          <CardLabel className="card-label-smaller">
            {!checkLocation ? t(input.label) : `${t(input.label)}:`}
            {config.isMandatory ? " * " : null}
          </CardLabel>
          <div className="field">
            <Controller
              control={control}
              defaultValue={formData?.address?.[input.name]}
              name={input.name}
              rules={{
                required: t("REQUIRED_FIELD"),
                validate: (value) => {
                  return /^[a-zA-Z ]*$/i.test(value) ? true : t("PT_NAME_ERROR_MESSAGE");
                },
              }}
              render={(_props) => (
                <TextInput
                  id={input.name}
                  key={input.name}
                  value={_props.value}
                  onChange={(e) => {
                    setFocusIndex({ index });
                    _props.onChange(e.target.value);
                  }}
                  onBlur={_props.onBlur}
                  disable={isRenewal}
                  autoFocus={focusIndex?.index == index}
                  {...input.validation}
                />
              )}
            />
              {formData?.address?.[input?.name] == "" && (
          <CardLabelError>Required</CardLabelError> 
        )}
          </div>
        
         
        </LabelFieldPair>
        
      );
    });
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline currentStep={1}/> : null}
    <FormStep
      config={{ ...config, inputs }}
      _defaultValues={{ street: formData?.address?.street, doorNo: formData?.address.doorNo }}
      onSelect={(data) => setValueForm(config.key, data)}
      //isDisabled={formData?.address?.street ? false : true}
      //onSkip={onSkip}
      t={t}
    />
    </React.Fragment>
  );
};

export default PTSelectStreet;
