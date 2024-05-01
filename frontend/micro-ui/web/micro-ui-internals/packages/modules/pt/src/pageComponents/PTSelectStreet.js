import { CardLabel, FormStep, LabelFieldPair, TextInput, Dropdown, Loader, LinkButton, CardLabelError } from "@egovernments/digit-ui-react-components";

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
        validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          // maxlength: 256,
          title: t("CORE_COMMON_STREET_INVALID"),
        },
      },
      {
        label: "PT_PROPERTY_ADDRESS_HOUSE_NO",
        type: "text",
        name: "doorNo",
        validation: {
          pattern: "[a-zA-Z0-9 !@#$%^&*()_+\-={};':\\\\|,.<>/?]{1,64}",
          // maxlength: 256,
          title: t("CORE_COMMON_DOOR_INVALID"),
        },
      },
      {
        label: "PT_PROPERTY_ADDRESS_VILLAGE",
        type: "text",
        name: "village",
        validation: {
        },
      },
      {
        label: "PT_PROPERTY_ADDRESS_PATTA_NO",
        type: "text",
        name: "pattaNo",
        validation: {
        },
      },
      {
        label: "PT_PROPERTY_ADDRESS_DAG_NO",
        type: "text",
        name: "dagNo",
        validation: {
        },
      },
      {
        label: "PT_PROPERTY_ADDRESS_CMN_NAME_OF_BUILDING",
        type: "text",
        name: "commonNameOfBuilding",
        validation: {
        },
      },
      {
        label: "PT_PROPERTY_ADDRESS_NAME_OF_PRINCIPAL_ROAD",
        type: "text",
        name: "principalRoadName",
        error: 'This field is required',
        labelChildren: '*',
        showErrorBelowChildren: true,
        validation: {
          required: true,
          
          // title: "CORE_COMMON_STREET_INVALID",
        },
      },
      // {
      //   label: "PT_PROPERTY_ADDRESS_NAME_OF_SUB_ROAD",
      //   type: "text",
      //   name: "subSideRoadName",
      //   validation: {
      //   },
      // },
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
                  disable={isRenewal}
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
  const [subSideRoadName, setsubSideRoadName] = useState();
  useEffect(() => {
    setsubSideRoadName(formData?.address?.subSideRoadName);
  }, [formData?.address?.subSideRoadName]);

  function onChangeSubRoad(e) {
    setsubSideRoadName(e.target.value);
  }
  const { data: mdmsData, isLoading } = Digit.Hooks.useCommonMDMS(
    Digit.ULBService.getStateId(),
    "PropertyTax",
    ["TypeOfRoad"],
    {
      select: (data) => {
        return {
          TypeOfRoad: data?.PropertyTax?.TypeOfRoad?.filter((roadType) => roadType.active)?.map((roadType) => ({
            i18nKey: `PROPERTYTAX_ROADTYPE_${roadType.code}`,
            code: roadType.code,
          }))
        };
      },
      retry: false,
      enable: false,
    }
  );
  const [typeOfRoad, setTypeOfRoad] = useState();
  useEffect(() => {
    if(formData?.address?.typeOfRoad?.code) {
      setTypeOfRoad({i18nKey: `PROPERTYTAX_ROADTYPE_${formData?.address?.typeOfRoad?.code}`,
    code: formData?.address?.typeOfRoad?.code});
    }
  }, [formData?.address?.typeOfRoad]);
  function selectTypeOfRoad(value) {
    console.log("selectTypeOfRoad=",value)
    setTypeOfRoad(value);
  }
  function onSubmit(data) {
    onSelect(config.key, { ...data, subSideRoadName: subSideRoadName, typeOfRoad: typeOfRoad });
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline currentStep={1}/> : null}
    <FormStep
      config={{ ...config, inputs }}
      _defaultValues={{ street: formData?.address.street, doorNo: formData?.address.doorNo, village : formData?.address.village,
        pattaNo : formData?.address.pattaNo, dagNo : formData?.address.dagNo,
        commonNameOfBuilding : formData?.address.commonNameOfBuilding, principalRoadName : formData?.address.principalRoadName,}}
      // onSelect={(data) => onSelect(config.key, data)}
      onSelect={onSubmit}
      onSkip={onSkip}
      showErrorBelowChildren={true}
      t={t}
      isDisabled={(!typeOfRoad && !formData?.address?.typeOfRoad) ? true : false}
    >
    <CardLabel>{`${t("PT_PROPERTY_ADDRESS_NAME_OF_SUB_ROAD")}`}</CardLabel>
    <TextInput
      style={{ background: "#FAFAFA" }}
      key={'subSideRoadName'}
      name={'subSideRoadName'}
      value={subSideRoadName}
      onChange={(e) => onChangeSubRoad(e)}
      isMandatory={false}
      disable={false}
    />


    <CardLabel>{`${t("PT_PROPERTY_ADDRESS_TYPE_OF_ROAD")}`}</CardLabel>
    <div className={"form-pt-dropdown-only"}>
      <Dropdown
        t={t}
        optionKey="i18nKey"
        isMandatory={true}
        option={mdmsData?.TypeOfRoad}
        selected={typeOfRoad}
        select={(e) => selectTypeOfRoad(e)}
        
      />
    </div>
    
    </FormStep>
    </React.Fragment>
  );
};

export default PTSelectStreet;
