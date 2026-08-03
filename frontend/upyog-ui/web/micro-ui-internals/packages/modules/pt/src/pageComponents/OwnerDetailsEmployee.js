import {
  CardLabel,
  CardLabelError,
  Dropdown,
  LabelFieldPair,
  LinkButton,
  MobileNumber,
  TextInput,
  Toast,
} from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useMemo, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { stringReplaceAll, CompareTwoObjects } from "../utils";
import "../css/pt-inline.css";

const createOwnerDetails = () => ({
  name: "",
  mobileNumber: "",
  fatherOrHusbandName: "",
  emailId: "",
  permanentAddress: "",
  relationship: "",
  ownerType: "",
  gender: "",
  isCorrespondenceAddress: false,
  key: Date.now(),
});

const PTEmployeeOwnershipDetails = ({ config, onSelect, userType, formData, setError, formState, clearErrors }) => {
  const { t } = useTranslation();

  const { pathname } = useLocation();
  const isEditScreen = pathname.includes("/modify-application/");
  const [owners, setOwners] = useState(formData?.owners || [createOwnerDetails()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const { data: mdmsData, isLoading } = Digit.Hooks.pt.usePropertyMDMS(stateId, "PropertyTax", [
    "UsageCategory",
    "OccupancyType",
    "Floor",
    "OwnerType",
    "OwnerShipCategory",
    "Documents",
    "SubOwnerShipCategory",
    "OwnerShipCategory",
  ]);

  const { data: Menu } = Digit.Hooks.pt.usePTGenderMDMS(stateId, "common-masters", "GenderType");

  let menu = [];

  Menu &&
    Menu.map((formGender) => {
      menu.push({ i18nKey: `PT_FORM3_${formGender.code}`, code: `${formGender.code}`, value: `${formGender.code}` });
    });

  const [ownerErrors, setOwnerErrors] = useState({});

  const updateOwnerErrors = (ownerKey, errors) => {
    setOwnerErrors((prev) => {
      if (_.isEqual(prev[ownerKey], errors)) return prev;
      return { ...prev, [ownerKey]: errors };
    });
  };

  const addNewOwner = () => {
    const newOwner = createOwnerDetails();
    setOwners((prev) => [...prev, newOwner]);
  };

  const removeOwner = (owner) => {
    setOwners((prev) => prev.filter((o) => o.key != owner.key));
    setOwnerErrors((prev) => {
      const copy = { ...prev };
      delete copy[owner.key];
      return copy;
    });
  };

  useEffect(() => {
    onSelect(config?.key, owners);
  }, [owners]);

  useEffect(() => {
    if (!formData?.owners) {
      setOwners([createOwnerDetails()]);
      setOwnerErrors({});
    }
  }, [formData?.ownershipCategory?.code]);

  useEffect(() => {
    const combinedErrors = {};
    Object.keys(ownerErrors).forEach((key) => {
      if (ownerErrors[key] && Object.keys(ownerErrors[key]).length > 0) {
        Object.assign(combinedErrors, ownerErrors[key]);
      }
    });

    if (Object.keys(combinedErrors).length) {
      setError(config.key, { type: combinedErrors });
    } else {
      clearErrors(config.key);
    }
  }, [ownerErrors]);

  const commonProps = {
    focusIndex,
    allOwners: owners,
    setFocusIndex,
    removeOwner,
    formData,
    formState,
    setOwners,
    mdmsData,
    t,
    setError,
    clearErrors,
    config,
    menu,
    isEditScreen,
    updateOwnerErrors,
  };

  // if (isEditScreen) {
  //   return <React.Fragment />;
  // }

  return formData?.ownershipCategory?.code ? (
    <React.Fragment>
      {owners.map((owner, index) => (
        <OwnerForm key={owner.key} index={index} owner={owner} {...commonProps} />
      ))}
      {!isEditScreen && formData?.ownershipCategory?.code === "INDIVIDUAL.MULTIPLEOWNERS" ? (
        <LinkButton label="Add Owner" onClick={addNewOwner} className="pt-inline-add-owner-btn" />
      ) : null}
    </React.Fragment>
  ) : null;
};

const OwnerForm = (_props) => {
  const {
    owner,
    index,
    focusIndex,
    allOwners,
    setFocusIndex,
    removeOwner,
    setOwners,
    t,
    mdmsData,
    formData,
    config,
    setError,
    clearErrors,
    formState,
    menu,
    isEditScreen,
    updateOwnerErrors,
  } = _props;
  const { originalData = {} } = formData;
  const { institution = {} } = originalData;
  const [uuid, setUuid] = useState(null);
  const [showToast, setShowToast] = useState(null);
  const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger, unregister } = useForm({
    mode: "onChange",
  });

  const formValue = watch();
  // Destructure touchedFields and touched from formState to register and track field-level interactions
  // safely, guarding against undefined formState proxy properties.
  const { errors, touchedFields, touched } = localFormState;
  const tenantId = Digit.ULBService.getCurrentTenantId();

  // Only set institution fields for non-individual owners
  if (!formData?.ownershipCategory?.code.includes("INDIVIDUAL")) {
    owner["institution"] = { name: owner?.institution?.name ? formValue?.institution?.name : institution?.name };
    owner["institution"].type = {
      active: true,
      code: formValue?.institution?.type?.code || institution?.type?.code,
      i18nKey: `COMMON_MASTERS_OWNERSHIPCATEGORY_${stringReplaceAll(formValue?.institution?.type?.code || institution?.type || "")}`,
      name: t(`COMMON_MASTERS_OWNERSHIPCATEGORY_${stringReplaceAll(formValue?.institution?.type?.code || institution?.type || "")}`),
    };
    owner.designation = owner?.designation ? formValue?.designation : institution?.designation;
  }
  const specialDocsMenu = useMemo(
    () =>
      mdmsData?.PropertyTax?.Documents?.filter((e) => e.code === "OWNER.SPECIALCATEGORYPROOF")?.[0]
        ?.dropdownData?.filter((e) => e.parentValue.includes(formValue?.ownerType?.code))
        ?.map?.((e) => ({
          i18nKey: e.code?.replaceAll(".", "_"),
          code: e.code,
        })) || [],
    [mdmsData, formValue]
  );

  const ownerTypesMenu = useMemo(
    () =>
      mdmsData?.PropertyTax?.OwnerType?.map?.((e) => ({
        i18nKey: `${e.code.replaceAll("PROPERTY", "COMMON_MASTERS").replaceAll(".", "_")}`,
        code: e.code,
      })) || [],
    [mdmsData]
  );

  if (ownerTypesMenu?.length > 0) {
    ownerTypesMenu ? ownerTypesMenu.sort((a, b) => a.code.localeCompare(b.code)) : "";
    ownerTypesMenu?.forEach((data, index) => {
      if (data.code == "NONE") data.order = 0;
      else data.order = index + 1;
    });
    ownerTypesMenu.sort(function (a, b) {
      return a.order - b.order;
    });
  }
  const isIndividualTypeOwner = useMemo(() => formData?.ownershipCategory?.code.includes("INDIVIDUAL"), [formData?.ownershipCategory?.code]);

  const institutionTypeMenu = useMemo(() => {
    const code = formData?.ownershipCategory?.code;
    const arr = mdmsData?.PropertyTax?.SubOwnerShipCategory?.filter((e) => e?.ownerShipCategory?.includes(code));
    return arr?.map((e) => ({ ...e, i18nKey: `COMMON_MASTERS_OWNERSHIPCATEGORY_${e.code?.replaceAll(".", "_")}` }));
  }, [mdmsData, formData?.ownershipCategory]);

  useEffect(() => {
    // Only trigger validation on mount if the owner is pre-populated (e.g., loaded from formData).
    // Calling trigger() on a fresh/empty owner immediately marks all required fields as invalid,
    // which propagates errors to the parent form and blocks the submit button even when
    // the user has filled all the other form sections correctly.
    if (owner?.name) {
      trigger();
    }
  }, []);

  const [part, setPart] = React.useState({});

  useEffect(() => {
    let _ownerType = isIndividualTypeOwner ? {} : { ownerType: { code: "NONE" } };

    if (!_.isEqual(part, formValue)) {
      setPart({ ...formValue });
      setOwners((prev) => prev.map((o) => (o.key && o.key === owner.key ? { ...o, ...formValue, ..._ownerType } : { ...o })));
    }
  }, [formValue]);

  // Clear institution and designation errors for INDIVIDUAL owners
  useEffect(() => {
    if (isIndividualTypeOwner) {
      unregister(['institution.name', 'institution.type', 'designation']);
    }
  }, [isIndividualTypeOwner]);
  // const validateEmail=(value)=>{
  //   console.log("valueeee", value)
  //   const emailPattern=/^[a-zA-Z0-9._%+-]+@gmail\.com$/
  //   if(value===""){
  //     setErrors("");
  //   }
  //   else if(emailPattern.test(value)){
  //     console.log("condition met")
  //     setErrors("");


  //   }
  //   else{
  //     setErrors("Email shd be in correct fromat");


  //   }
  // }
  // const handleEmailChange=(e)=>{
  //   console.log("eeeooo", email)
  //   const value=e.target.value;
  //   setEmail(value);
  //   validateEmail(value);

  // }
  // useEffect(() => {
  //   if(email){
  //     validateEmail(email);
  //   }

  // }, [email])


  useEffect(() => {
    updateOwnerErrors(owner.key, errors);
  }, [errors]);

  useEffect(() => {
    const getData = setTimeout(async () => {
      const propertyDetails = await Digit.PTService.search({ tenantId, filters: { documentNumbers: uuid } });
      if (propertyDetails?.Properties.length > 0) {
        setShowToast({
          error: true,
          label: `Please enter a valid document number`,
        });
      } else {
        setShowToast({
          label: `Valid document number`,
        });
      }
    }, 1000);
    return () => clearTimeout(getData);
  }, [uuid]);

  return (
    <React.Fragment>
      <div className="pt-inline-owner-form-section">
        <div className="label-field-pair">
          <h2 className="card-label card-label-smaller pt-inline-owner-form-heading">
            {isIndividualTypeOwner
              ? `Owner ${formData?.ownershipCategory?.code?.includes("MULTIPLE") ? index + 1 : ""}`
              : "Authorised Person Details"}
          </h2>
        </div>
        <div className="pt-inline-owner-form-body">
          {allOwners?.length > 2 ? (
            <div onClick={() => removeOwner(owner)} className="pt-inline-owner-remove">
              X
            </div>
          ) : null}

          {!isIndividualTypeOwner ? (
            <React.Fragment>
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("PT_INSTITUTION_NAME")}<span className="check-page-link-button"> *</span></CardLabel>
                <div className="field">
                  <Controller
                    control={control}
                    name={"institution.name"}
                    defaultValue={isEditScreen ? (institution?.name ? institution.name : owner?.name) : null}
                    rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: {
                        pattern: (v) => (/^[a-zA-Z_@./()#&+-\s]*$/.test(v) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                      },
                    }}
                    render={({ field }) => (
                      <TextInput
                        value={field.value}
                        disable={isEditScreen}
                        name={"institution.name"}
                        autoFocus={focusIndex.index === owner?.key && focusIndex.type === "institution.name"}
                        onChange={(e) => {
                          field.onChange(e.target.value);
                          setFocusIndex({ index: owner.key, type: "institution.name" });
                        }}
                        onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          field.onBlur(e);
                        }}
                      />
                    )}
                  />
                </div>
              </LabelFieldPair>
              <CardLabelError className="pt-inline-owner-error">
                {(localFormState?.touchedFields?.institution?.name || localFormState?.touched?.institution?.name) ? errors?.institution?.name?.message : ""}
              </CardLabelError>
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("PT_INSTITUTION_TYPE")}<span className="check-page-link-button"> *</span></CardLabel>
                <Controller
                  control={control}
                  name={"institution.type"}
                  defaultValue={
                    isEditScreen
                      ? {
                        active: true,
                        code: institution?.type,
                        i18nKey: `COMMON_MASTERS_OWNERSHIPCATEGORY_${stringReplaceAll(institution?.type || "")}`,
                        name: t(`COMMON_MASTERS_OWNERSHIPCATEGORY_${stringReplaceAll(institution?.type || "")}`),
                      }
                      : null
                  }
                  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                  render={({ field }) => (
                    <Dropdown
                      className="form-field"
                      selected={field.value}
                      select={field.onChange}
                      onBlur={field.onBlur}
                      option={institutionTypeMenu}
                      optionKey="i18nKey"
                      disable={isEditScreen}
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
              <CardLabelError className="pt-inline-owner-error">
                {(localFormState?.touchedFields?.institution?.type || localFormState?.touched?.institution?.type) ? errors?.institution?.type?.message : ""}
              </CardLabelError>
            </React.Fragment>
          ) : null}

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PT_OWNER_NAME")}<span className="check-page-link-button"> *</span></CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"name"}
                defaultValue={owner?.name}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),

                }}
                render={({ field }) => (
                  <TextInput
                    value={field.value}
                    disable={isEditScreen}
                    autoFocus={focusIndex.index === owner?.key && focusIndex.type === "name"}
                    onChange={(e) => {
                      field.onChange(e.target.value);
                      setFocusIndex({ index: owner.key, type: "name" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      field.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError className="pt-inline-owner-error">{(localFormState?.touchedFields?.name || localFormState?.touched?.name) ? errors?.name?.message : ""}</CardLabelError>

          {isIndividualTypeOwner ? (
            <React.Fragment>
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("PT_FORM3_GENDER")}<span className="check-page-link-button"> *</span></CardLabel>
                <Controller
                  control={control}
                  name={"gender"}
                  defaultValue={owner?.gender}
                  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                  render={({ field }) => (
                    <Dropdown
                      className="form-field"
                      selected={field.value}
                      select={field.onChange}
                      disable={isEditScreen}
                      onBlur={field.onBlur}
                      option={menu}
                      optionKey="i18nKey"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
              <CardLabelError className="pt-inline-owner-error">{(localFormState?.touchedFields?.gender || localFormState?.touched?.gender) ? errors?.gender?.message : ""}</CardLabelError>
            </React.Fragment>
          ) : (
            <React.Fragment>
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("PT_LANDLINE_NUMBER_FLOATING_LABEL")}{isIndividualTypeOwner ? "" : <span className="check-page-link-button"> *</span>}</CardLabel>
                <div className="field">
                  <Controller
                    control={control}
                    name={"altContactNumber"}
                    defaultValue={owner?.altContactNumber}
                    rules={
                      isIndividualTypeOwner
                        ? {}
                        : {
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                          validate: { pattern: (e) => (/^[0-9]{11}$/i.test(e) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }
                    }
                    render={({ field }) => (
                      <MobileNumber
                        value={field.value}
                        hideSpan={true}
                        disable={isEditScreen}
                        maxLength={11}
                        autoFocus={focusIndex.index === owner?.key && focusIndex.type === "altContactNumber"}
                        onChange={(e) => {
                          field.onChange(e);
                          setFocusIndex({ index: owner.key, type: "altContactNumber" });
                        }}
                        labelStyle={{ marginTop: "unset" }}
                        onBlur={field.onBlur}
                      />
                    )}
                  />
                </div>
              </LabelFieldPair>
              <CardLabelError className="pt-inline-owner-error">{(localFormState?.touchedFields?.altContactNumber || localFormState?.touched?.altContactNumber) ? errors?.altContactNumber?.message : ""}</CardLabelError>
            </React.Fragment>
          )}
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PT_FORM3_MOBILE_NUMBER")} <span className="check-page-link-button"> *</span></CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"mobileNumber"}
                defaultValue={owner?.mobileNumber}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: (v) => (/^[6789]\d{9}$/.test(v) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                }}
                render={({ field }) => (
                  <MobileNumber
                    value={field.value}
                    disable={isEditScreen}
                    autoFocus={focusIndex.index === owner?.key && focusIndex.type === "mobileNumber"}
                    onChange={(e) => {
                      field.onChange(e);
                      setFocusIndex({ index: owner.key, type: "mobileNumber" });
                    }}
                    labelStyle={{ marginTop: "unset" }}
                    onBlur={field.onBlur}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError className="pt-inline-owner-error">{(localFormState?.touchedFields?.mobileNumber || localFormState?.touched?.mobileNumber) ? errors?.mobileNumber?.message : ""}</CardLabelError>
          {isIndividualTypeOwner ? (
            <React.Fragment>
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("PT_SEARCHPROPERTY_TABEL_GUARDIANNAME")} <span className="check-page-link-button"> *</span> </CardLabel>
                <div className="field">
                  <Controller
                    control={control}
                    name={"fatherOrHusbandName"}
                    defaultValue={owner?.fatherOrHusbandName}
                    rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z ]+$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={({ field }) => (
                      <TextInput
                        value={field.value}
                        disable={isEditScreen}
                        autoFocus={focusIndex.index === owner?.key && focusIndex.type === "fatherOrHusbandName"}
                        onChange={(e) => {
                          field.onChange(e.target.value);
                          setFocusIndex({ index: owner.key, type: "fatherOrHusbandName" });
                        }}
                        onBlur={field.onBlur}
                      />
                    )}
                  />
                </div>
              </LabelFieldPair>
              <CardLabelError className="pt-inline-owner-error">
                {(localFormState?.touchedFields?.fatherOrHusbandName || localFormState?.touched?.fatherOrHusbandName) ? errors?.fatherOrHusbandName?.message : ""}
              </CardLabelError>
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("PT_FORM3_RELATIONSHIP")} <span className="check-page-link-button"> *</span> </CardLabel>
                <Controller
                  control={control}
                  name={"relationship"}
                  defaultValue={owner?.relationship}
                  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                  render={({ field }) => (
                    <Dropdown
                      className="form-field"
                      selected={field.value}
                      select={field.onChange}
                      onBlur={field.onBlur}
                      disable={isEditScreen}
                      option={[
                        { i18nKey: "PT_FORM3_FATHER", code: "FATHER" },
                        { i18nKey: "PT_FORM3_HUSBAND", code: "HUSBAND" },
                      ]}
                      optionKey="i18nKey"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
              <CardLabelError className="pt-inline-owner-error">{(localFormState?.touchedFields?.relationship || localFormState?.touched?.relationship) ? errors?.relationship?.message : ""}</CardLabelError>
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("PT_FORM3_SPECIAL_CATEGORY")} <span className="check-page-link-button"> *</span> </CardLabel>
                <Controller
                  control={control}
                  name={"ownerType"}
                  defaultValue={owner?.ownerType}
                  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                  render={({ field }) => (
                    <Dropdown
                      className="form-field"
                      selected={field.value}
                      select={field.onChange}
                      onBlur={field.onBlur}
                      option={ownerTypesMenu}
                      disable={isEditScreen}
                      optionKey="i18nKey"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
              <CardLabelError className="pt-inline-owner-error">{(localFormState?.touchedFields?.ownerType || localFormState?.touched?.ownerType) ? errors?.ownerType?.message : ""}</CardLabelError>
            </React.Fragment>
          ) : (
            <React.Fragment>
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("TL_NEW_DESIG_OWNER_LABEL")}<span className="check-page-link-button"> *</span> </CardLabel>
                <div className="field">
                  <Controller
                    control={control}
                    name={"designation"}
                    defaultValue={isEditScreen ? (institution?.designation || "") : null}
                    rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                    render={({ field }) => (
                      <TextInput
                        value={field.value}
                        disable={isEditScreen}
                        autoFocus={focusIndex.index === owner?.key && focusIndex.type === "designation"}
                        onChange={(e) => {
                          field.onChange(e.target.value);
                          setFocusIndex({ index: owner.key, type: "designation" });
                        }}
                        onBlur={field.onBlur}
                      />
                    )}
                  />
                </div>
              </LabelFieldPair>
              <CardLabelError className="pt-inline-owner-error">{(localFormState?.touchedFields?.designation || localFormState?.touched?.designation) ? errors?.designation?.message : ""}</CardLabelError>
            </React.Fragment>
          )}

          {formValue.ownerType?.code && formValue.ownerType?.code !== "NONE" ? (
            <React.Fragment>
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("PT_OWNERSHIP_DOCUMENT_TYPE")} <span className="check-page-link-button"> *</span> </CardLabel>
                <Controller
                  control={control}
                  name={"documents.documentType"}
                  defaultValue={owner?.documents?.documentType}
                  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                  render={({ field }) => (
                    <Dropdown
                      className="form-field"
                      selected={field.value}
                      select={field.onChange}
                      disable={isEditScreen}
                      onBlur={field.onBlur}
                      option={specialDocsMenu}
                      optionKey="i18nKey"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
              <CardLabelError className="pt-inline-owner-error">
                {(localFormState?.touchedFields?.documents?.documentType || localFormState?.touched?.documents?.documentType) ? errors?.documents?.documentType?.message : ""}
              </CardLabelError>
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("PT_OWNERSHIP_DOCUMENT_ID")} <span className="check-page-link-button"> *</span> </CardLabel>
                <div className="field">
                  <Controller
                    control={control}
                    name={"documents.documentUid"}
                    defaultValue={owner?.documents?.documentUid}
                    rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                    render={({ field }) => (
                      <TextInput
                        value={field.value}
                        disable={isEditScreen}
                        autoFocus={focusIndex.index === owner?.key && focusIndex.type === "documents.documentUid"}
                        onChange={(e) => {
                          setUuid(e.target.value);
                          field.onChange(e);
                          setFocusIndex({ index: owner.key, type: "documents.documentUid" });
                        }}
                        labelStyle={{ marginTop: "unset" }}
                        onBlur={field.onBlur}
                      />
                    )}
                  />
                </div>
              </LabelFieldPair>
              <CardLabelError className="pt-inline-owner-error">
                {(localFormState?.touchedFields?.documents?.documentUid || localFormState?.touched?.documents?.documentUid) ? errors?.documents?.documentUid?.message : ""}
              </CardLabelError>{" "}
            </React.Fragment>
          ) : null}
          <div>
            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("PT_OWNERSHIP_INFO_EMAIL_ID")}</CardLabel>
              <div className="field">
                <Controller
                  control={control}
                  name={"emailId"}
                  defaultValue={owner?.emailId}
                  rules={{
                    validate: (e) => {
                      if (!e) return true;
                      return /^[a-zA-Z0-9._%+-]+@[a-z.-]+\.(com|org|in)$/.test(e) || t("CORE_INVALID_EMAIL_ID_PATTERN")
                    }
                  }
                  }
                  render={({ field }) => (
                    <TextInput
                      value={field.value}
                      disable={isEditScreen}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "emailId"}
                      errorStyle={localFormState.touchedFields.emailId && errors?.emailId?.message ? true : false}
                      onChange={(e) => {
                        field.onChange(e);
                        setFocusIndex({ index: owner.key, type: "emailId" });
                      }}
                      labelStyle={{ marginTop: "unset" }}
                      onBlur={field.onBlur}
                    />
                  )}
                />
              </div>
            </LabelFieldPair>
            <CardLabelError className="pt-inline-owner-error">{localFormState.touchedFields.emailId ? errors?.emailId?.message : ""}</CardLabelError>
          </div>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PT_OWNERSHIP_INFO_CORR_ADDR")}{isIndividualTypeOwner ? "" : <span className="check-page-link-button"> *</span>}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"correspondenceAddress"}
                defaultValue={owner?.correspondenceAddress}
                rules={isIndividualTypeOwner ? {} : { required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={({ field }) => (
                  <TextInput
                    value={field.value}
                    disable={isEditScreen}
                    autoFocus={focusIndex.index === owner?.key && focusIndex.type === "correspondenceAddress"}
                    onChange={(e) => {
                      field.onChange(e);
                      setFocusIndex({ index: owner.key, type: "correspondenceAddress" });
                    }}
                    onBlur={field.onBlur}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError className="pt-inline-owner-error">
            {(localFormState?.touchedFields?.correspondenceAddress || localFormState?.touched?.correspondenceAddress) ? errors?.correspondenceAddress?.message : ""}
          </CardLabelError>
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

export default PTEmployeeOwnershipDetails;
