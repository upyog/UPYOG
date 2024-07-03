import React, { useEffect, useState } from "react";
import { TextInput, LabelFieldPair, CardLabel, WrapUnMaskComponent } from "@nudmcdgnpm/digit-ui-react-components";
import FormStep from "../../../../react-components/src/molecules/FormStep";
import { useForm, Controller } from "react-hook-form";
import _ from "lodash";
import Timeline from "../components/TLTimelineInFSM";

const SelectRoadDetails = ({ t, config, onSelect, userType, formData, formState, setError, clearErrors }) => {
  const onSkip = () => onSelect();

  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const {
    watch,
    trigger,
  } = useForm();
  const formValue = watch();
  const [distance, setDistance]=useState(formData?.roadWidth?.distancefromroad|| "");
  const [roadWidth, setRoadWidth]=useState(formData?.roadWidth?.roadWidth|| "");
  let inputs;
  if (window.location.href.includes("tl")) {
    inputs = config.inputs;
    config.inputs[0].disable = window.location.href.includes("edit-application");
    config.inputs[1].disable = window.location.href.includes("edit-application");
    inputs[0].validation = { minLength: 0, maxLength: 256 };
    inputs[1].validation = { minLength: 0, maxLength: 256 };
  } else {
    inputs = [
      {
        label: "ES_NEW_APPLICATION_ROAD_WIDTH",
        type: "text",
        name: "roadWidth",
        isMandatory: true,
        placeholder:"Enter road width in meters",
        validation: {
            maxlength: 256,
          title: t("CORE_COMMON_ROADWIDTH_INVALID"),
        },
      },
      {
        label: "ES_NEW_APPLICATION_DISTANCE_FROM_ROAD",
        type: "text",
        name: "distancefromroad",
        isMandatory:true,
        placeholder:"Enter distance of pit from road",
        validation: {
           maxlength: 256,
          title: t("CORE_COMMON_DISTANCE_INVALID"),
        },
      },
    ];
  }

  const convertValidationToRules = ({ validation, name, messages }) => {
    if (validation) {
      let {  maxlength, minlength, required: valReq } = validation || {};
   
      let maxLength = (value) => (maxlength ? (value?.length <= maxlength ? true : messages?.maxlength || `${name.toUpperCase()}_MAXLENGTH`) : true);
      let minLength = (value) => (minlength ? (value?.length >= minlength ? true : messages?.minlength || `${name.toUpperCase()}_MINLENGTH`) : true);
      let required = (value) => (valReq ? (!!value ? true : messages?.required || `${name.toUpperCase()}_REQUIRED`) : true);

      return { required, minLength, maxLength };
    }
    return {};
  };


  
  useEffect(() => {
    trigger();
  }, []);

  useEffect(() => {
    if (formData?.roadCharges?.distance) setDistance(formData?.roadCharges?.distance)
    if (formData?.roadCharges?.roadWidth) setRoadWidth(formData?.roadCharges?.roadWidth)
  }, [formData?.roadCharges])


  useEffect(() => {
    const keys = Object.keys(formValue);
    const part = {};
    keys.forEach((key) => (part[key] = formData[config.key]?.[key]));

    if (!_.isEqual(formValue, part)) {
      onSelect(config.key, { ...formData[config.key], ...formValue });
      trigger();
    }
  }, [formValue]);


  const handleSkip = (data) => {
    let { name } = data.target;
    let { value } = data.target;
    name === "roadWidth" ? setRoadWidth(value) : setDistance(value);
  };

  return (
    <React.Fragment>
      {window.location.href.includes("/tl") ? <Timeline currentStep={2} /> : <Timeline currentStep={1} flow="APPLY" />}
      <FormStep
        config={{ ...config, inputs }}
        isMandatory={true}
        _defaultValues={{ roadWidth: formData?.roadWidth?.roadWidth || "", distancefromroad: formData?.roadWidth?.distancefromroad || ""}}
        onChange={handleSkip}
        onSelect={(data) => onSelect(config.key, data)}
        isDisabled={roadWidth && distance ? false : true}
        t={t}
      />
    </React.Fragment>
  );
};

export default SelectRoadDetails;
