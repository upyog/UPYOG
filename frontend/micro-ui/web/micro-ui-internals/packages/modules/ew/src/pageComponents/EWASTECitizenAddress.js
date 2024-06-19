import { CardLabel, FormStep, LabelFieldPair, TextInput } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/EWASTETimeline";

const EWASTECitizenAddress = ({ t, config, onSelect, userType, formData, formState, setError, clearErrors }) => {
  const onSkip = () => onSelect();
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger } = useForm();
  const formValue = watch();
  // const { errors } = localFormState;
  // const checkLocation = window.location.href.includes("ew/raiseRequest");

  let inputs;
  
    inputs = [
      {
        label: "EWASTE_STREET_NAME",
        type: "text",
        name: "street",
        validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          // maxlength: 256,
          title: t("CORE_COMMON_STREET_INVALID"),
        },
      },
      {
        label: "EWASTE_HOUSE_NO",
        type: "text",
        name: "doorNo",
        validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          // maxlength: 256,
          title: t("CORE_COMMON_DOOR_INVALID"),
        },
      },
      {
        label: "EWASTE_HOUSE_NAME",
        type: "text",
        name: "buildingName",
         validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          // maxlength: 256,
          title: t("CORE_COMMON_DOOR_INVALID"),
         },
      },
      {
        label: "EWASTE_ADDRESS_LINE1",
        type: "text",
        name: "addressLine1",
         validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          // maxlength: 256,
          title: t("CORE_COMMON_DOOR_INVALID"),
         },
      },
      {
        label: "EWASTE_ADDRESS_LINE2",
        type: "text",
        name: "addressLine2",
         validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          // maxlength: 256,
          title: t("CORE_COMMON_DOOR_INVALID"),
         },
      },
      {
        label: "EWASTE_landmark",
        type: "text",
        name: "landmark",
         validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          // maxlength: 256,
          title: t("CORE_COMMON_DOOR_INVALID"),
         },
      },
    
      
    ];
  // }

  // const convertValidationToRules = ({ validation, name, messages }) => {
  //   if (validation) {
  //     let { pattern: valPattern, maxlength,minlength, required: valReq } = validation || {};
  //     let pattern = (value) => {
  //       if (valPattern) {
  //         if (valPattern instanceof RegExp) return valPattern.test(value) ? true : messages?.pattern || `${name.toUpperCase()}_PATTERN`;
  //         else if (typeof valPattern === "string")
  //           return new RegExp(valPattern)?.test(value) ? true : messages?.pattern || `${name.toUpperCase()}_PATTERN`;
  //       }
  //       return true;
  //     };
  //     let maxLength = (value) => (maxlength ? (value?.length <= maxlength ? true : messages?.maxlength || `${name.toUpperCase()}_MAXLENGTH`) : true);
  //     let minLength = (value) => (minlength ? (value?.length >= minlength ? true : messages?.minlength || `${name.toUpperCase()}_MINLENGTH`) : true);
  //     let required = (value) => (valReq ? (!!value ? true : messages?.required || `${name.toUpperCase()}_REQUIRED`) : true);

  //     return { pattern, required, maxLength,minlength };
  //   }
  //   return {};
  // };

  // let isDis = true;

  useEffect(() => {
    trigger();
  }, []);

  // useEffect(() => {
  //   console.log("euseffect")
  //   if(inputs.street){
  //     isDis=false
  //   }
  // }, [inputs.street])

  // useEffect(() => {
  //   if (userType === "employee") {
  //     if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors)) setError(config.key, { type: errors });
  //     else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
  //   }
  // }, [errors]);

  useEffect(() => {
    const keys = Object.keys(formValue);
    const part = {};
    keys.forEach((key) => (part[key] = formData[config.key]?.[key]));

    if (!_.isEqual(formValue, part)) {
      onSelect(config.key, { ...formData[config.key], ...formValue });
      trigger();
    }
  }, [formValue]);

  // if (userType === "employee") {
  //   return inputs?.map((input, index) => {
  //     return (
  //       <LabelFieldPair key={index}>
  //         <CardLabel className="card-label-smaller">
  //           {!checkLocation ? t(input.label) : `${t(input.label)}:`}
  //           {config.isMandatory ? " * " : null}
  //         </CardLabel>
  //         <div className="field">
  //           <Controller
  //             control={control}
  //             defaultValue={formData?.address?.[input.name]}
  //             name={input.name}
  //             rules={{ validate: convertValidationToRules(input) }}
  //             render={(_props) => (
  //               <TextInput
  //                 id={input.name}
  //                 key={input.name}
  //                 value={_props.value}
  //                 onChange={(e) => {
  //                   setFocusIndex({ index });
  //                   _props.onChange(e.target.value);
  //                 }}
  //                 onBlur={_props.onBlur}
  //                 // disable={isRenewal}
  //                 autoFocus={focusIndex?.index == index}
  //                 {...input.validation}
  //               />
  //             )}
  //           />
  //         </div>
  //       </LabelFieldPair>
  //     );
  //   });
  // }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline currentStep={4}/> : null}
    <FormStep
      config={{ ...config, inputs }}
      _defaultValues={{
        street: formData?.address.street,
        doorNo: formData?.address.doorNo,
        buildingName: formData?.address.buildingName,
        addressLine1: formData?.address.addressLine1,
        addressLine2: formData?.address.addressLine2,
        landmark: formData?.address.landmark  
       }}

      onSelect={(data) => onSelect(config.key, data)}
      onSkip={onSkip}
      t={t}
    />
    </React.Fragment>
  );
};

export default EWASTECitizenAddress;
