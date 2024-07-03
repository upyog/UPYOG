  import { CardLabel, FormStep, LabelFieldPair, TextInput } from "@upyog/digit-ui-react-components";
  import _ from "lodash";
  import React, { useEffect, useState } from "react";
  import { Controller, useForm } from "react-hook-form";
  import Timeline from "../components/PTRTimeline";

  const PTRSelectStreet = ({ t, config, onSelect, userType, formData, formState, setError, clearErrors }) => {
    
    const onSkip = () => onSelect();
    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
    const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger } = useForm();
    const formValue = watch();
    const { errors } = localFormState;
    const checkLocation = window.location.href.includes("ptr/petservice/new-application");

    let inputs;


    //To do - Need to change this whole page and have to make like same as owner details page because when uncomment the validation , it is throwing error  
    
      inputs = [
        {
          label: "PTR_STREET_NAME",
          type: "text",
          name: "street",
          validation: {
            // pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
            // // maxlength: 256,
            // title: t("CORE_COMMON_STREET_INVALID"),
          },
        },
        {
          label: "PTR_HOUSE_NO",
          type: "text",
          name: "doorNo",
          validation: {
            // pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
            // // maxlength: 256,
            // title: t("CORE_COMMON_DOOR_INVALID"),
          },
        },
        {
          label: "PTR_HOUSE_NAME",
          type: "text",
          name: "buildingName",
           validation: {
            // pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
            // // maxlength: 256,
            // title: t("CORE_COMMON_DOOR_INVALID"),
           },
        },
        {
          label: "PTR_ADDRESS_LINE1",
          type: "text",
          name: "addressLine1",
           validation: {
            // pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
            // // maxlength: 256,
            // title: t("CORE_COMMON_DOOR_INVALID"),
           },
        },
        {
          label: "PTR_ADDRESS_LINE2",
          type: "text",
          name: "addressLine2",
           validation: {
            // pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
            // // maxlength: 256,
            // title: t("CORE_COMMON_DOOR_INVALID"),
           },
        },
        {
          label: "PTR_landmark",
          type: "text",
          name: "landmark",
           validation: {
            // pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
            // // maxlength: 256,
            // title: t("CORE_COMMON_DOOR_INVALID"),
           },
        },
      
        
      ];

   ;

    useEffect(() => {
      trigger();
    }, []);

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
                  // required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: { pattern: (val) => (/^[A-Za-z]+( +[A-Za-z]+)*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
                    autoFocus={focusIndex?.index == index}
                    {...input.validation}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
        );
      });
    }
    return (
      <React.Fragment>
      <FormStep
        config={{ ...config, inputs }}
        _defaultValues={{
          street: formData?.address.street,
          doorNo: formData?.address.doorNo,
           
         }}

        onSelect={(data) => onSelect(config.key, data)}
        onSkip={onSkip}
        t={t}
      />
      </React.Fragment>
    );
  };

  export default PTRSelectStreet;
