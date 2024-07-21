import { CardLabel, DatePicker, Dropdown, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import isEqual from 'lodash.isequal';
import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import dropdownOptions from "../pagecomponents/dropdownOptions.json";
import ToggleSwitch from "./Toggle";

const PersonalDetailCard = ({ onUpdate, initialRows = {}, AllowEdit = true, tenantId }) => {
  const { t } = useTranslation();
  const [isEditable, setIsEditable] = useState(AllowEdit);
  const [castes, setCastes] = useState([]);
  const [religions, setReligions] = useState([]);
  const headerLocale = useMemo(() => Digit.Utils.locale.getTransformedLocale(tenantId), [tenantId]);

  const { control, formState: { errors, isValid }, setValue, trigger, clearErrors, watch ,getValues} = useForm({
    defaultValues: {
      firstName: initialRows.aadharname || "",
      middleName: initialRows.middleName || "",
      lastName: initialRows.lastName || "",
      dob: initialRows.aadhardob || "",
      gender: initialRows.gender || "",
      father: initialRows.aadharfathername || "",
      religion: initialRows.religion || "",
      casteCategory: initialRows.caste || "",
    },
    mode: "onChange",
  });

  const formValuesRef = useRef(getValues());
  const formValues = watch();

  const processSingleData = useCallback((item, headerLocale) => {
    if (!item) return null;

    const genderMapping = {
      male: { id: 1, name: 'Male' },
      female: { id: 2, name: 'Female' },
      transgender: { id: 3, name: 'Transgender' },
    };

    if (typeof item === 'string') {
      const gender = genderMapping[item.toLowerCase()];
      if (!gender) return null;
      return {
        ...gender,
        i18nKey: `${headerLocale}_ADMIN_${gender.name.toUpperCase()}`,
      };
    }

    if (typeof item === 'object' && item.id && item.name) {
      return {
        id: item.id,
        name: item.name,
        i18nKey: `${headerLocale}_ADMIN_${item.name}`,
      };
    }

    return null;
  }, []);

  const processCommonData = useCallback((data, headerLocale) => (
    data?.CommonDetails?.map((item) => ({
      id: item.id,
      name: item.name,
      i18nKey: `${headerLocale}_ADMIN_${item.name}`,
    })) || []
  ), []);

  const casteFunction = useCallback((data) => {
    const castesData = processCommonData(data, headerLocale);
    setCastes(castesData);
    return { castesData };
  }, [headerLocale, processCommonData]);

  const religionFunction = useCallback((data) => {
    const religionsData = processCommonData(data, headerLocale);
    setReligions(religionsData);
    return { religionsData };
  }, [headerLocale, processCommonData]);

  const getCaste = { CommonSearchCriteria: { Option: "caste" } };
  const getReligion = { CommonSearchCriteria: { Option: "religion" } };

  Digit.Hooks.bmc.useCommonGet(getCaste, { select: casteFunction });
  Digit.Hooks.bmc.useCommonGet(getReligion, { select: religionFunction });

  const stableOnUpdate = useCallback((values, valid) => {
    onUpdate(values, valid);
  }, [onUpdate]);

  useEffect(() => {
    if (!isEqual(formValuesRef.current, formValues)) {
      formValuesRef.current = formValues;
      stableOnUpdate(formValues, isValid);
    }
  }, [formValues, isValid, stableOnUpdate]);

  useEffect(() => {
    trigger();
  }, [trigger]);

  useEffect(() => {
    if (initialRows) {
      const casteData = processSingleData(initialRows?.caste, headerLocale);
      const religionData = processSingleData(initialRows?.religion, headerLocale);
      const genderData = processSingleData(initialRows?.gender, headerLocale);
      setValue("firstName", initialRows.aadharname || "");
      setValue("middleName", initialRows.middleName || "");
      setValue("lastName", initialRows.lastName || "");
      setValue("dob", initialRows.aadhardob || "");
      setValue("gender", genderData || "");
      setValue("father", initialRows.aadharfathername || "");
      setValue("religion", religionData || "");
      setValue("casteCategory", casteData || "");

      // Clear errors for fields that received initial values
      if (initialRows.aadharname) clearErrors("firstName");
      if (initialRows.middleName) clearErrors("middleName");
      if (initialRows.lastName) clearErrors("lastName");
      if (initialRows.aadhardob) clearErrors("dob");
      if (genderData) clearErrors("gender");
      if (initialRows.aadharfathername) clearErrors("father");
      if (religionData) clearErrors("religion");
      if (casteData) clearErrors("casteCategory");
    }
  }, [initialRows, setValue, headerLocale, clearErrors, processSingleData]);

  const handleToggle = () => {
    setIsEditable(!isEditable);
  };

  return (
    <React.Fragment>
      <form className="bmc-row-card-header">
        <div className="bmc-card-row">
          <div className="bmc-col-large-header">
            <div className="bmc-title">{t("PERSONAL DETAILS")}</div>
          </div>
          <div className="bmc-col-small-header" style={{ textAlign: "end" }}>
            <ToggleSwitch
              id={"PersonalToggle"}
              isOn={isEditable}
              handleToggle={handleToggle}
              onLabel="Editable"
              offLabel="Readonly"
              disabled={!AllowEdit}
            />
          </div>
        </div>
        <div className="bmc-card-row">
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_FIRST_NAME")}</CardLabel>
              <Controller
                control={control}
                name="firstName"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.firstName && <span style={{ color: "red" }}>{errors.firstName.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_MIDDLE_NAME")}</CardLabel>
              <Controller
                control={control}
                name="middleName"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.middleName && <span style={{ color: "red" }}>{errors.middleName.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_LAST_NAME")}</CardLabel>
              <Controller
                control={control}
                name="lastName"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.lastName && <span style={{ color: "red" }}>{errors.lastName.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_DATE_OF_BIRTH")}</CardLabel>
              <Controller
                control={control}
                name="dob"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <DatePicker disabled={!isEditable} date={props.value} onChange={props.onChange} onBlur={props.onBlur} />
                    {errors.dob && <span style={{ color: "red" }}>{errors.dob.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
        </div>
        <div className="bmc-card-row">
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_GENDER")}</CardLabel>
              <Controller
                control={control}
                name="gender"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    {isEditable ? (
                      <Dropdown
                        placeholder={t("SELECT GENDER")}
                        selected={props.value}
                        select={props.onChange}
                        onBlur={props.onBlur}
                        option={dropdownOptions.gender}
                        optionKey="name"
                        t={t}
                        isMandatory={true}
                      />
                    ) : (
                      <TextInput readOnly value={props.value?.name || ""} />
                    )}
                    {errors.gender && <span style={{ color: "red" }}>{errors.gender.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_FATHER")}</CardLabel>
              <Controller
                control={control}
                name="father"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.father && <span style={{ color: "red" }}>{errors.father.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_RELIGION")}*</CardLabel>
              <Controller
                control={control}
                name="religion"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    {isEditable ? (
                      <Dropdown
                        placeholder={t("SELECT RELIGION")}
                        selected={props.value}
                        select={props.onChange}
                        onBlur={props.onBlur}
                        option={religions}
                        optionKey="i18nKey"
                        t={t}
                        isMandatory={true}
                      />
                    ) : (
                      <TextInput readOnly value={props.value?.i18nKey || ""} />
                    )}
                    {errors.religion && <span style={{ color: "red" }}>{errors.religion.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_CASTECATEGORY")}*</CardLabel>
              <Controller
                control={control}
                name="casteCategory"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    {isEditable ? (
                      <Dropdown
                        placeholder={t("SELECT CASTE CATEGORY")}
                        selected={props.value}
                        select={props.onChange}
                        onBlur={props.onBlur}
                        option={castes}
                        optionKey="i18nKey"
                        t={t}
                        isMandatory={true}
                      />
                    ) : (
                      <TextInput readOnly value={t(props.value?.i18nKey) || ""} />
                    )}
                    {errors.casteCategory && <span style={{ color: "red" }}>{errors.casteCategory.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
        </div>
      </form>
    </React.Fragment>
  );
};

export default PersonalDetailCard;
