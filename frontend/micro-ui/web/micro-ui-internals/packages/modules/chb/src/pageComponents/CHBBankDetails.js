import { CardLabel, FormStep, LabelFieldPair, TextInput } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/CHBTimeline";

const CHBBankDetails = ({ t, config, onSelect, userType, formData, formState, setError, clearErrors }) => {
  const onSkip = () => onSelect();
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger } = useForm();
  const formValue = watch();
  const { errors } = localFormState;
  const checkLocation = window.location.href.includes("chb/bookHall");

  let inputs;
 
  
    inputs = [
      {
        label: "ACCOUNT_NUMBER",
        type: "text",
        name: "AccountNumber",
        validation: {
          pattern: "[0-9]{8,25}",
          title: t("INVALID_ACCOUNT_NUMBER"),
        },
      },
      {
        label: "CONFIRM_ACCOUNT_NUMBER",
        type: "text",
        name: "ConfirmAccountNumber",
        validation: {
          pattern: "[0-9]{8,25}",
          title: t("INVALID_CONFIRM_ACCOUNT_NUMBER"),
        },
      },
      {
        label: "IFSC_CODE",
        type: "text",
        name: "Ifsc",
         validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          title: t("INVALID_IFSC_CODE"),
         },
      },
      {
        label: "BANK_NAME",
        type: "text",
        name: "BankName",
         validation: {
          pattern: "^[a-zA-Z ]+$",
          title: t("INVALID_BANK_NAME"),
         },
      },
      {
        label: "BANK_BRANCH_NAME",
        type: "text",
        name: "BankBranchName",
         validation: {
          pattern: "^[a-zA-Z ]+$",
          title: t("INVALID_BANK_BRANCH_NAME"),
         },
      },
      {
        label: "ACCOUNT_HOLDER_NAME",
        type: "text",
        name: "AccountHolderName",
         validation: {
          pattern: "^[a-zA-Z ]+$",
          title: t("INVALID_ACCOUNT_HOLDER_NAME"),
         },
      },
    
      
    ];

  const convertValidationToRules = ({ validation, name, messages }) => {
    if (validation) {
      let { pattern: valPattern, maxlength,minlength, required: valReq } = validation || {};
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
      let required = (value) => (valReq ? (!!value ? true : messages?.required || `${name.toUpperCase()}_REQUIRED`) : true);

      return { pattern, required, maxLength,minlength };
    }
    return {};
  };

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
              rules={{ validate: convertValidationToRules(input) }}
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
                  // disable={isRenewal}
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
    {window.location.href.includes("/citizen") ? <Timeline currentStep={3}/> : null}
    <FormStep
      config={{ ...config, inputs }}
      _defaultValues={{
        AccountNumber: formData?.AccountNumber,
        ConfirmAccountNumber: formData?.ConfirmAccountNumber,
        Ifsc: formData?.Ifsc,
        BankName: formData?.BankName,
        BankBranchName: formData?.BankBranchName,
        AccountHolderName: formData?.AccountHolderName  
       }}

      onSelect={(data) => onSelect(config.key, data)}
      onSkip={onSkip}
      t={t}
    />
    </React.Fragment>
  );
};

export default CHBBankDetails;
