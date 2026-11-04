import React, { useState, useEffect } from "react";
import { CardLabel, LabelFieldPair, TextInput, CardLabelError, MobileNumber, DatePicker, Loader, CardSectionHeader } from "@nudmcdgnpm/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useTranslation } from "react-i18next";
import _ from "lodash";
import { useLocation } from "react-router-dom";
/**
 * ConsumerDetails component:
 * - Handles consumer information form
 * - Supports validation and state sync using react-hook-form
 */

const createConsumerDetails = () => ({
  ConsumerName: "",
  mobileNumber: "",
  emailId:"",
});

const ConsumerDetails = ({ config, onSelect, userType, formData, setError, formState, clearErrors }) => {
  if(window.location.href.includes("modify-challan") && sessionStorage.getItem("mcollectEditObject"))
  {
    formData = JSON.parse(sessionStorage.getItem("mcollectEditObject"))
  }
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const isEdit = pathname.includes("/modify-challan/");
  const [consumerDetails, setconsumerDetails] = useState(formData?.consomerDetails1 || [createConsumerDetails()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const [isErrors, setIsErrors] = useState(false);
  const stateCode = window?.globalConfigs?.getConfig("STATE_LEVEL_TENANT_ID");
  const data = Digit.Hooks.mcollect.useCommonMDMS(stateCode, "common-masters", ["HierarchyType"]);
  const type = data &&  data.data &&  data.data[`common-masters`] && data.data[`common-masters`]["HierarchyType"] && data.data[`common-masters`]["HierarchyType"][0];





  const selectCity = async (city) => {
    return;
  };


  useEffect(() => {
    const data = consumerDetails.map((e) => {
      return e;
    });
    onSelect(config?.key, data);
  }, [consumerDetails]);


  const commonProps = {
    focusIndex,
    allOwners: consumerDetails,
    setFocusIndex,
    formData,
    formState,
    t,
    setError,
    clearErrors,
    config,
    setconsumerDetails,
    isEdit,
    setIsErrors,
    isErrors,
    consumerDetails,
  };

  return (
    <React.Fragment>
      {consumerDetails.map((consumerdetail, index) => (
        <OwnerForm1 key={index} index={index} consumerdetail={consumerdetail} {...commonProps} />
      ))}
    </React.Fragment>
  );
};

const OwnerForm1 = (_props) => {
  const {
    consumerdetail,
    index,
    focusIndex,
    allOwners,
    setFocusIndex,
    t,
    formData,
    config,
    setError,
    clearErrors,
    formState,
    setconsumerDetails,
    isEdit,
    consumerDetails,
    setIsErrors,
    isErrors,
  } = _props;

  const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger, getValues } = useForm();
  const formValue = watch();
  const { errors, touchedFields, touched } = localFormState;
  const isMobile = window.Digit.Utils.browser.isMobile();

  



  useEffect(() => {
    trigger();
  }, []);

  useEffect(() => {
    if(Object.entries(formValue).length>0){
    const keys = Object.keys(formValue);
    const part = {};
    keys.forEach((key) => (part[key] = consumerdetail[key]));
    if (!_.isEqual(formValue, part)) {
      Object.keys(formValue).map(data => {
        if (data != "key" && formValue[data] != undefined && formValue[data] != "" && formValue[data] != null && !isErrors) {
          setIsErrors(true);
        }
      });
      let ob =[{...formValue}];
      let mcollectFormValue = JSON.parse(sessionStorage.getItem("mcollectFormData"));
      mcollectFormValue = {...mcollectFormValue,...ob[0]}
      sessionStorage.setItem("mcollectFormData",JSON.stringify(mcollectFormValue));
      setconsumerDetails(ob);
      trigger();
    }
    }
  }, [formValue]);


  useEffect(() => {
    if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors)) {
      setError(config.key, { type: errors });
    }
    else if (!Object.keys(errors).length && formState.errors[config.key] && isErrors) {
      clearErrors(config.key);
    }
  }, [errors]);

  
  return (
    <React.Fragment>
      <div>
        <div>
        <CardSectionHeader>{t("CONSUMERDETAILS")}</CardSectionHeader>
        <LabelFieldPair>
            <CardLabel className={isMobile?"card-label-APK":"card-label-smaller"}>{`${t("UC_CONS_NAME_LABEL")} * `}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"ConsumerName"}
                defaultValue={consumerdetail?.ConsumerName}
                rules={{ required: t("REQUIRED_FIELD"), validate: { pattern: (val) => (/^[a-zA-Z ]*$/.test(val) ? true : t("CS_ADDCOMPLAINT_NAME_ERROR")) } }}
                render={({ field: props }) => (
                  <TextInput
                    value={props.value}
                    autoFocus={focusIndex.index === consumerdetail?.key && focusIndex.type === "name"}
                    errorStyle={((touchedFields?.ConsumerName || touched?.ConsumerName) && errors?.ConsumerName?.message) ? true : false}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                    disable={isEdit}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError className="cg-error-style">{(touchedFields?.ConsumerName || touched?.ConsumerName) ? errors?.ConsumerName?.message : ""}</CardLabelError>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller cg-cardlabel-pt10">{`${t("UC_MOBILE_NUMBER")}`}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"mobileNumber"}
                defaultValue={consumerdetail?.mobileNumber}
                rules={{ required: t("REQUIRED_FIELD"), validate: (v) => (/^[6789]\d{9}$/.test(v) ? true : t("CORE_COMMON_MOBILE_ERROR")) }}
                render={({ field: props }) => (
                  <MobileNumber
                    value={props.value}
                    autoFocus={focusIndex.index === consumerdetail?.key && focusIndex.type === "mobileNumber"}
                    onChange={(e) => {
                      props.onChange(e);
                      setFocusIndex({ index: consumerdetail.key, type: "mobileNumber" });
                    }}
                    labelClassName="cg-mobile-label-style"
                    onBlur={props.onBlur}
                    errorStyle={((touchedFields?.mobileNumber || touched?.mobileNumber) && errors?.mobileNumber?.message) ? true : false}
                    disable={isEdit}
                  />
                )}
              />

            </div>
          </LabelFieldPair>  
          <CardLabelError className="cg-error-style">{(touchedFields?.mobileNumber || touched?.mobileNumber) ? errors?.mobileNumber?.message : ""}</CardLabelError>
      </div>
      </div>
    </React.Fragment>
  );
};
export default ConsumerDetails;