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

  const [street, setStreet] = useState(formData?.address?.street || "");
  const [addressLine1, setAddressLine1] = useState(formData?.address?.addressLine1 || "");
  const [addressLine2, setAddressLine2] = useState(formData?.address?.addressLine2 || "");
  const [landmark, setLandmark] = useState(formData?.address?.landmark || "");
  const [buildingName, setBuildingName] = useState(formData?.address?.latitude || "");
  const [doorNo, setDoorNo] = useState(formData?.address?.doorNo || "");

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

  useEffect(() => {
    trigger();
  }, []);

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



  const selectStreet = (e) => setStreet(e.target.value);
  const selectDoorNo = (e) => setDoorNo(e.target.value);
  const selectBuilding = (e) => setBuildingName(e.target.value);
  const selectLandmark = (e) => setLandmark(e.target.value);
  const selectAddressLine1 = (e) => setAddressLine1(e.target.value);
  const selectAddressLine2 = (e) => setAddressLine2(e.target.value);


  const goNext = () => {
    let owner = formData.address;
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, street, addressLine1, addressLine2, landmark, buildingName, doorNo };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
    } else {
      ownerStep = { ...owner, street, addressLine1, addressLine2, landmark, buildingName, doorNo };
      onSelect(config.key, ownerStep, false);
    }
  };

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [street, addressLine1, addressLine2, landmark, buildingName, doorNo]);

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
      <FormStep
        config={{ ...config }}
        // onSelect={(data) => onSelect(config.key, data)}
        onSelect={goNext}
        onSkip={onSkip}
        isDisabled={addressLine1 == "" || doorNo == ""}
        t={t}
      >
        <CardLabel>{`${t("EWASTE_STREET_NAME")}`}</CardLabel>
        <TextInput
          t={t}
          //isMandatory={true}
          type={"text"}
          optionKey="i18nKey"
          name="street"
          onChange={selectStreet}
          value={street}
          errorStyle={true}
          autoFocus={focusIndex?.index == 1}
        />
        <CardLabel>{`${t("EWASTE_HOUSE_NO")}*`}</CardLabel>
        <TextInput
          t={t}
          //isMandatory={true}
          type={"text"}
          optionKey="i18nKey"
          name="doorNo"
          onChange={selectDoorNo}
          value={doorNo}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}

        />
        <CardLabel>{`${t("EWASTE_HOUSE_NAME")}`}</CardLabel>
        <TextInput
          t={t}
          //isMandatory={true}
          type={"text"}
          optionKey="i18nKey"
          name="buildingName"
          onChange={selectBuilding}
          value={buildingName}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}

        />
        <CardLabel>{`${t("EWASTE_ADDRESS_LINE1")}*`}</CardLabel>
        <TextInput
          t={t}
          //isMandatory={true}
          type={"text"}
          optionKey="i18nKey"
          name="addressLine1"
          onChange={selectAddressLine1}
          value={addressLine1}
          errorStyle={true}
          autoFocus={focusIndex?.index == 1}
        />
        <CardLabel>{`${t("EWASTE_ADDRESS_LINE2")}`}</CardLabel>
        <TextInput
          t={t}
          //isMandatory={true}
          type={"text"}
          optionKey="i18nKey"
          name="addressLine2"
          onChange={selectAddressLine2}
          value={addressLine2}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}

        />
        <CardLabel>{`${t("EWASTE_landmark")}`}</CardLabel>
        <TextInput
          t={t}
          //isMandatory={true}
          type={"text"}
          optionKey="i18nKey"
          name="landmark"
          onChange={selectLandmark}
          value={landmark}
          errorStyle={true}
          autoFocus={focusIndex?.index == 1}
        />
      </FormStep>
    </React.Fragment>
  );
};

export default EWASTECitizenAddress;
