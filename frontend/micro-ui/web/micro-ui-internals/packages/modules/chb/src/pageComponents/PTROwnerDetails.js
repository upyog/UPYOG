import { CardLabel, CardLabelError, Dropdown, LabelFieldPair, LinkButton, MobileNumber, TextInput, Toast } from "@upyog/digit-ui-react-components";
  import _ from "lodash";
  import React, { useEffect, useMemo, useState } from "react";
  import { Controller, useForm } from "react-hook-form";
  import { useTranslation } from "react-i18next";
  import { useLocation } from "react-router-dom";
  import { stringReplaceAll, CompareTwoObjects } from "../utils";

  const createOwnerDetails = () => ({
    applicantName: "",
    mobileNumber: "",
    fatherName: "",
    emailId: "",
    alternateNumber: "",
    key: Date.now(),
  });

  const PTROwnerDetails = ({ config, onSelect, userType, formData, setError, formState, clearErrors }) => {
    const { t } = useTranslation();

    const { pathname } = useLocation();
    const [owners, setOwners] = useState(formData?.owners || [createOwnerDetails()]);
    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();






    useEffect(() => {
      onSelect(config?.key, owners);


    }, [owners]);


    const commonProps = {
      focusIndex,
      allOwners: owners,
      setFocusIndex,
      formData,
      formState,
      setOwners,
      t,
      setError,
      clearErrors,
      config,
    };


    return (
      <React.Fragment>
        {owners.map((owner, index) => (
          <OwnerForm key={owner.key} index={index} owner={owner} {...commonProps} />
        ))}

      </React.Fragment>
    )
  };

  const OwnerForm = (_props) => {
    const {
      owner,
      index,
      focusIndex,
      allOwners,
      setFocusIndex,
      setOwners,
      t,
      formData,
      config,
      setError,
      clearErrors,
      formState,
    } = _props;

    const [showToast, setShowToast] = useState(null);
    const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger } = useForm();
    const formValue = watch();
    const { errors } = localFormState;
    const tenantId = Digit.ULBService.getCurrentTenantId();






    



    useEffect(() => {
      trigger();
    }, []);


    const [part, setPart] = React.useState({});

    useEffect(() => {
      

      if (!_.isEqual(part, formValue)) {
        setPart({ ...formValue });

        setOwners((prev) => prev.map((o) => (o.key && o.key === owner.key ? { ...o, ...formValue } : { ...o })));
        trigger();
      }
    }, [formValue]);
    



    useEffect(() => {
      if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors)) setError(config.key, { type: errors });
      else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
    }, [errors]);

    const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };



    return (
      <React.Fragment>
        <div style={{ marginBottom: "16px" }}>

          <div style={{ border: "1px solid #E3E3E3", padding: "16px", marginTop: "8px" }}>
            {allOwners?.length > 2 ? (
              <div
                style={{ marginBottom: "16px", padding: "5px", cursor: "pointer", textAlign: "right" }}
              >
                X
              </div>
            ) : null}



            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("PTR_APPLICANT_NAME") + " *"}</CardLabel>
              <div className="field">
                <Controller
                  control={control}
                  name={"applicantName"}
                  defaultValue={owner?.applicantName}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                  }}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      // disable={isEditScreen}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "applicantName"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "applicantName" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </div>
            </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.applicantName ? errors?.applicantName?.message : ""}</CardLabelError>


            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("PTR_FATHER_HUSBAND_NAME") + " *"}</CardLabel>
              <div className="field">
                <Controller
                  control={control}
                  name={"fatherName"}
                  defaultValue={owner?.fatherName}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                  }}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      // disable={isEditScreen}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "fatherName"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "fatherName" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </div>
            </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.fatherName ? errors?.fatherName?.message : ""}</CardLabelError>


            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("PTR_MOBILE_NUMBER") + " *"}</CardLabel>
              <div className="field">
                <Controller
                  control={control}
                  name={"mobileNumber"}
                  defaultValue={owner?.mobileNumber}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: (v) => (/^[6789]\d{9}$/.test(v) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                  }}
                  render={(props) => (
                    <MobileNumber
                      value={props.value}
                      // disable={isEditScreen}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "mobileNumber"}
                      onChange={(e) => {
                        props.onChange(e);
                        setFocusIndex({ index: owner.key, type: "mobileNumber" });
                      }}
                      labelStyle={{ marginTop: "unset" }}
                      onBlur={props.onBlur}
                    />
                  )}
                />
              </div>
            </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.mobileNumber ? errors?.mobileNumber?.message : ""}</CardLabelError>

            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("PTR_ALT_MOBILE_NUMBER")+ " *"}</CardLabel>
              <div className="field">
                <Controller
                  control={control}
                  name={"alternateNumber"}
                  defaultValue={owner?.alternateNumber}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: (v) => (/^[6789]\d{9}$/.test(v)),
                  }}
                  render={(props) => (
                    <MobileNumber
                      value={props.value}
                      // disable={isEditScreen}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "alternateNumber"}
                      onChange={(e) => {
                        props.onChange(e);
                        setFocusIndex({ index: owner.key, type: "alternateNumber" });
                      }}
                      labelStyle={{ marginTop: "unset" }}
                      onBlur={props.onBlur}
                    />
                  )}
                />
              </div>
            </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.alternateNumber ? errors?.alternateNumber?.message : ""}</CardLabelError>

            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("PTR_EMAIL_ID")}</CardLabel>
              <div className="field">
                <Controller
                  control={control}
                  name={"emailId"}
                  defaultValue={owner?.emailId}
                  rules={{
                    validate: (value) => {
                      const normalized = value.trim().toLowerCase();  
                      const emailRegex = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
                  
                      return emailRegex.test(normalized) || 'TL_EMAIL_ID_ERROR_MESSAGE';
                    } 
                  }}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      // disable={isEditScreen}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "emailId"}
                      onChange={(e) => {
                        props.onChange(e);
                        setFocusIndex({ index: owner.key, type: "emailId" });
                      }}
                      labelStyle={{ marginTop: "unset" }}
                      onBlur={props.onBlur}
                    />
                  )}
                />
              </div>
            </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.emailId ? errors?.emailId?.message : ""}</CardLabelError>

          </div>
        </div>
        {showToast?.label && (
          <Toast
            label={showToast?.label}
            onClose={(w) => {
              setShowToast((x) => null);
            }}
          />
        )}
      </React.Fragment>
    );
  };

  export default PTROwnerDetails;