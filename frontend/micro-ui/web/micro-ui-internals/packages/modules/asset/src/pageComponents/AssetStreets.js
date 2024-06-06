



import { CardLabel, FormStep, LabelFieldPair, TextInput ,CardLabelError} from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/ASTTimeline";

const AssetStreets = ({ t, config, onSelect, userType, formData, formState, setError, clearErrors }) => {
  const onSkip = () => onSelect();
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const [street,setStreet]=useState(formData?.address?.street || "")
  const [addressLine1,setaddressLine1]=useState(formData?.address?.addressLine1 ||"" )
  const [addressLine2,setaddressLine2]=useState(formData?.address?.addressLine2 ||"" )
  const [landmark,setlandmark]=useState(formData?.address?.landmark ||"" )
  const [latitude,setlatitude]=useState(formData?.address?.latitude ||"" )
  const [longitude,setlongitude]=useState(formData?.address?.longitude ||"" )
  const [doorNo,setDoorNo]=useState(formData?.address?.doorNo ||"" )










  const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };
  const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger } = useForm();
  const formValue = watch();
  const { errors } = localFormState;
  const checkLocation = window.location.href.includes("tl/new-application") || window.location.href.includes("tl/renew-application-details");
  const isRenewal = window.location.href.includes("edit-application") || window.location.href.includes("tl/renew-application-details");
  let validation = {};
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
        isMandatory:"true",
        validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&()_+\-={};':\\\\|,.<>/?]{1,64}",
          isRequired: true,
          title: t("CORE_COMMON_STREET_INVALID"),
        },
      },
      {
        label: "PT_PROPERTY_ADDRESS_HOUSE_NO",
        type: "text",
        name: "doorNo",
        isMandatory:"true",
        validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&()_+\-={};':\\\\|,.<>/?]{1,64}",
          isRequired: true,
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
      let required = (value) => (valReq ? (!!value ? true : messages?.required || `${name.toUpperCase()}_REQUIRED`) : true);
      return { pattern, required, minLength, maxLength };
    }
    return {};
  };
const setData=(config,data)=>{
  let dataNew ={street,doorNo,addressLine1,addressLine2,landmark,latitude,longitude }
  onSelect(config, dataNew)
}
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
    console.log("key",formValue)
    if (!_.isEqual(formValue, part)) {
      onSelect(config.key, { ...formData[config.key], ...formValue });
      for (let key in formValue) {
      
        if (!formValue[key] && !localFormState?.errors[key]) {
          setLocalError(key, { type: `${key.toUpperCase()}_REQUIRED`, message: t(`CORE_COMMON_REQUIRED_ERRMSG`) });
        } else if (formValue[key] && localFormState.errors[key]) {
          clearLocalErrors([key]);
        }
      }
      trigger();
    } 
  }, [formValue]);

  function selectStreet(e) {
    setFocusIndex({ index:1 });
    setStreet(e.target.value);
  }
  function selectDoorNo(e) {
    setDoorNo(e.target.value);
  }


  function selectLatitude(e) {
    setlatitude(e.target.value);
  }
  function selectLandmark(e) {
    setlandmark(e.target.value);
  }
  function selectaddressLineone(e) {
    setaddressLine1(e.target.value);
  }
  function selectaddressLinetwo(e) {
    setaddressLine2(e.target.value);
  }
  function selectLongitude(e) {
    setlongitude(e.target.value);
  }

  
  return (
    <React.Fragment>
    {window.location.href.includes("/employee") ? <Timeline currentStep={3}/> : null}
    <FormStep
      config={{ ...config }}
      onSelect={(data) => {setData(config.key,data)}}
      onSkip={onSkip}
      isDisabled={street =="" || doorNo ==""}
      t={t}
    >
        <CardLabel>{`${t("AST_STREET")}*`}</CardLabel>
          <TextInput
            t={t}
            //isMandatory={true}
            type={"text"}
            optionKey="i18nKey"
            name="street"
            onChange={selectStreet}
            value={street}
            style={{width:"50%"}}
            errorStyle={true}
            autoFocus={focusIndex?.index == 1}
          />
          <CardLabel>{`${t("AST_DOOR_NO")}*`}</CardLabel>
          <TextInput
            t={t}
            //isMandatory={true}
            type={"text"}
            optionKey="i18nKey"
            name="doorNo"
            onChange={selectDoorNo}
            style={{width:"50%"}}
            value={doorNo}
            errorStyle={false}
            autoFocus={focusIndex?.index == 1}
           
          />
          <CardLabel>{`${t("AST_ADDRESS_LINE_1")}`}</CardLabel>
          <TextInput
            t={t}
            //isMandatory={true}
            type={"text"}
            optionKey="i18nKey"
            name="addressLine1"
            onChange={selectaddressLineone}
            value={addressLine1}
            style={{width:"50%"}}
            errorStyle={true}
            autoFocus={focusIndex?.index == 1}
          />
          <CardLabel>{`${t("AST_ADDRESS_LINE_2")}`}</CardLabel>
          <TextInput
            t={t}
            //isMandatory={true}
            type={"text"}
            optionKey="i18nKey"
            name="addressLine2"
            onChange={selectaddressLinetwo}
            style={{width:"50%"}}
            value={addressLine2}
            errorStyle={false}
            autoFocus={focusIndex?.index == 1}
           
          />
          <CardLabel>{`${t("AST_LANDMARK")}`}</CardLabel>
          <TextInput
            t={t}
            //isMandatory={true}
            type={"text"}
            optionKey="i18nKey"
            name="landmark"
            onChange={selectLandmark}
            value={landmark}
            style={{width:"50%"}}
            errorStyle={true}
            autoFocus={focusIndex?.index == 1}
          />
          <CardLabel>{`${t("AST_LATITUDE")}`}</CardLabel>
          <TextInput
            t={t}
            //isMandatory={true}
            type={"text"}
            optionKey="i18nKey"
            name="latitude"
            onChange={selectLatitude}
            style={{width:"50%"}}
            value={latitude}
            errorStyle={false}
            autoFocus={focusIndex?.index == 1}
           
          />
          <CardLabel>{`${t("AST_LONGITUDE")}`}</CardLabel>
          <TextInput
            t={t}
            //isMandatory={true}
            type={"text"}
            optionKey="i18nKey"
            name="longitude"
            onChange={selectLongitude}
            value={longitude}
            style={{width:"50%"}}
            errorStyle={true}
            autoFocus={focusIndex?.index == 1}
          />
          
      </FormStep>
    </React.Fragment>
  );
};

export default AssetStreets;
