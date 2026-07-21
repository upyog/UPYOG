/* 
 * NOC Owner Details Component (Step 3 of the Citizen wizard flow)
 * Collects owner details (name, gender, mobile number, DOB, father/husband relationship).
 * Supports both single/multiple individual owners and institutional private/government categories.
 */
import React, { useEffect, useState, useMemo, useReducer } from "react";
import {
  FormStep,
  TextInput,
  CardLabel,
  RadioButtons,
  Dropdown,
  LinkButton,
  CardHeader,
  Loader,
  CardLabelError,
  Toast,
} from "@nudmcdgnpm/digit-ui-react-components";
import Timeline from "../components/NocTimeline";

const NocOwnerDetails = ({ t, config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();

  // Load OwnershipCategory MDMS data
  const { data: ownershipData, isLoading: isOwnershipLoading } = Digit.Hooks.useCustomMDMS(
    stateId,
    "common-masters",
    [{ name: "OwnerShipCategory" }],
    {
      select: (data) => {
        const categoryData = data?.["common-masters"]?.["OwnerShipCategory"] || [];
        const activeCategories = categoryData.filter((cat) => cat.active && cat.code.split(".").length === 2);

        // Extract unique major categories (first part of the code)
        const uniqueMajorCodes = Array.from(new Set(activeCategories.map((cat) => cat.code.split(".")[0])));

        const major = uniqueMajorCodes.map((code) => ({
          code: code,
          i18nKey: `COMMON_MASTERS_OWNERSHIPCATEGORY_${code}`,
        }));

        const sub = activeCategories.map((cat) => ({
          code: cat.code,
          parentCode: cat.code.split(".")[0],
          i18nKey: `COMMON_MASTERS_OWNERSHIPCATEGORY_${cat.code.replaceAll(".", "_")}`,
        }));

        return { major, sub };
      },
    }
  );

  // Load OwnerType (Special Categories) MDMS data
  const { data: specialCategories, isLoading: isSpecialLoading } = Digit.Hooks.useCustomMDMS(
    stateId,
    "common-masters",
    [{ name: "OwnerType" }],
    {
      select: (data) => {
        const formattedData = data?.["common-masters"]?.["OwnerType"] || [];
        return formattedData
          .filter((cat) => cat.active)
          .map((cat) => ({
            code: cat.code,
            i18nKey: `COMMON_MASTERS_OWNERTYPE_${cat.code}`,
          }));
      },
    }
  );

  const [selectedMajorCategory, setSelectedMajorCategory] = useState(() => {
    const saved = formData?.ownershipCategory?.ownerShipMajorType;
    if (saved) return saved;
    const sub = formData?.ownershipCategory?.ownerShipType || formData?.ownershipCategory?.ownershipCategory || formData?.ownershipCategory;
    if (sub?.code) {
      const parentCode = sub.code.split(".")[0];
      return { code: parentCode, i18nKey: `COMMON_MASTERS_OWNERSHIPCATEGORY_${parentCode}` };
    }
    return null;
  });

  const [selectedSubCategory, setSelectedSubCategory] = useState(() => {
    const saved = formData?.ownershipCategory?.ownerShipType || formData?.ownershipCategory?.ownershipCategory || formData?.ownershipCategory;
    if (saved && saved.code) return saved;
    return null;
  });

  const filteredSubCategories = useMemo(() => {
    if (!selectedMajorCategory) return [];
    return (ownershipData?.sub || []).filter(
      (sub) => sub.parentCode === selectedMajorCategory.code
    );
  }, [selectedMajorCategory, ownershipData]);

  const typeOfOwner = useMemo(() => {
    if (!selectedSubCategory) return null;
    const code = selectedSubCategory.code || "";
    if (code.includes("SINGLEOWNER")) return "SINGLEOWNER";
    if (code.includes("INSTITUTIONAL")) return "INSTITUTIONAL";
    return "MULTIOWNER";
  }, [selectedSubCategory]);

  const storedOwnerData = formData?.owners?.owners;

  const initFn = (initData) => {
    if (!typeOfOwner) return [];
    switch (typeOfOwner) {
      case "SINGLEOWNER":
        return [
          {
            name: initData?.[0]?.name || "",
            gender: initData?.[0]?.gender,
            mobileNumber: initData?.[0]?.mobileNumber || "",
            fatherOrHusbandName: initData?.[0]?.fatherOrHusbandName || "",
            emailId: initData?.[0]?.emailId || "",
            relationship: initData?.[0]?.relationship,
            dob: initData?.[0]?.dob || "",
            pan: initData?.[0]?.pan || "",
            correspondenceAddress: initData?.[0]?.correspondenceAddress || "",
            ownerType: initData?.[0]?.ownerType,
          },
        ];
      case "MULTIOWNER":
        return initData?.length > 1
          ? initData.map((owner) => ({
            name: owner?.name || "",
            gender: owner?.gender,
            mobileNumber: owner?.mobileNumber || "",
            fatherOrHusbandName: owner?.fatherOrHusbandName || "",
            emailId: owner?.emailId || "",
            relationship: owner?.relationship,
            dob: owner?.dob || "",
            pan: owner?.pan || "",
            correspondenceAddress: owner?.correspondenceAddress || "",
            ownerType: owner?.ownerType,
          }))
          : [
            {
              name: "",
              gender: null,
              mobileNumber: "",
              fatherOrHusbandName: "",
              emailId: "",
              relationship: null,
              dob: "",
              pan: "",
              correspondenceAddress: "",
              ownerType: null,
            },
          ];
      case "INSTITUTIONAL":
        return [
          {
            name: initData?.[0]?.name || "",
            mobileNumber: initData?.[0]?.mobileNumber || "",
            emailId: initData?.[0]?.emailId || "",
            correspondenceAddress: initData?.[0]?.correspondenceAddress || "",
            institutionName: initData?.[0]?.institutionName || "",
            telephoneNumber: initData?.[0]?.telephoneNumber || "",
            institutionDesignation: initData?.[0]?.institutionDesignation || "",
          },
        ];
      default:
        return [];
    }
  };

  const reducer = (state, action) => {
    switch (action.type) {
      case "ADD_NEW_OWNER":
        return [
          ...state,
          {
            name: "",
            gender: null,
            mobileNumber: "",
            fatherOrHusbandName: "",
            emailId: "",
            relationship: null,
            dob: "",
            pan: "",
            correspondenceAddress: "",
            ownerType: null,
          },
        ];
      case "REMOVE_THIS_OWNER":
        return state.filter((_, i) => i !== action.payload.index);
      case "EDIT_CURRENT_OWNER_PROPERTY":
        return state.map((data, idx) => {
          if (idx === action.payload.index) {
            return {
              ...data,
              [action.payload.key]: action.payload.value,
            };
          }
          return data;
        });
      case "RESET_OWNERS":
        if (action.payload === "SINGLEOWNER") {
          return [
            {
              name: "",
              gender: null,
              mobileNumber: "",
              fatherOrHusbandName: "",
              emailId: "",
              relationship: null,
              dob: "",
              pan: "",
              correspondenceAddress: "",
              ownerType: null,
            },
          ];
        } else if (action.payload === "INSTITUTIONAL") {
          return [
            {
              name: "",
              mobileNumber: "",
              emailId: "",
              correspondenceAddress: "",
              institutionName: "",
              telephoneNumber: "",
              institutionDesignation: "",
            },
          ];
        } else {
          return [
            {
              name: "",
              gender: null,
              mobileNumber: "",
              fatherOrHusbandName: "",
              emailId: "",
              relationship: null,
              dob: "",
              pan: "",
              correspondenceAddress: "",
              ownerType: null,
            },
          ];
        }
      default:
        return state;
    }
  };

  const [formState, dispatch] = useReducer(reducer, storedOwnerData, initFn);
  const [error, setError] = useState(null);

  /* Auto-dismisses warning toast messages after 3 seconds */
  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => {
        setError(null);
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [error]);

  const genderOptions = [
    { code: "MALE", i18nKey: "NOC_GENDER_MALE_RADIOBUTTON", label: t("NOC_GENDER_MALE_RADIOBUTTON") },
    { code: "FEMALE", i18nKey: "NOC_GENDER_FEMALE_RADIOBUTTON", label: t("NOC_GENDER_FEMALE_RADIOBUTTON") },
    { code: "TRANSGENDER", i18nKey: "NOC_GENDER_TRANSGENDER_RADIOBUTTON", label: t("NOC_GENDER_TRANSGENDER_RADIOBUTTON") },
  ];

  const relationshipOptions = [
    { code: "FATHER", i18nKey: "NOC_APPLICANT_RELATIONSHIP_FATHER_RADIOBUTTON", label: t("NOC_APPLICANT_RELATIONSHIP_FATHER_RADIOBUTTON") },
    { code: "HUSBAND", i18nKey: "NOC_APPLICANT_RELATIONSHIP_HUSBAND_RADIOBUTTON", label: t("NOC_APPLICANT_RELATIONSHIP_HUSBAND_RADIOBUTTON") },
  ];

  const handleMajorCategoryChange = (val) => {
    setSelectedMajorCategory(val);
    setSelectedSubCategory(null);
    dispatch({ type: "RESET_OWNERS", payload: "SINGLEOWNER" });
    setError(null);
  };

  const handleSubCategoryChange = (val) => {
    setSelectedSubCategory(val);
    let ownerTypeType = "SINGLEOWNER";
    if (val?.code?.includes("INSTITUTIONAL")) {
      ownerTypeType = "INSTITUTIONAL";
    } else if (val?.code?.includes("MULTIPLEOWNERS")) {
      ownerTypeType = "MULTIOWNER";
    }
    dispatch({ type: "RESET_OWNERS", payload: ownerTypeType });
    setError(null);
  };

  const [fieldErrors, setFieldErrors] = useState({});

  const validateFieldRealTime = (index, key, value) => {
    let errorMsg = null;
    if (value) {
      if (key === "mobileNumber" || key === "telephoneNumber") {
        const mobileRegex = /^[6789][0-9]{9}$/;
        if (!mobileRegex.test(value)) {
          errorMsg = t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID");
        }
      } else if (key === "emailId") {
        const emailRegex = /^(?=^.{1,64}$)((([^<>()\[\]\\.,;:\s$*@'"]+(\.[^<>()\[\]\\.,;:\s@'"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,})))$/i;
        if (!emailRegex.test(value)) {
          errorMsg = t("NOC_EMAIL_ERROR_MESSAGE");
        }
      } else if (key === "pan") {
        const panRegex = /^[A-Za-z]{5}\d{4}[A-Za-z]{1}$/;
        if (!panRegex.test(value)) {
          errorMsg = t("NOC_PAN_ERROR_MESSAGE");
        }
      } else if (key === "name" || key === "fatherOrHusbandName" || key === "institutionName" || key === "institutionDesignation") {
        const nameRegex = /^[^{0-9}^\$\"<>?\\\\~!@#$%^()+={}\[\]*,/_:;“”‘’]{1,50}$/i;
        if (!nameRegex.test(value)) {
          errorMsg = (key === "institutionName") ? t("NOC_INSTITUTION_NAME_ERROR_MESSAGE") : t("NOC_NAME_ERROR_MESSAGE");
        }
      } else if (key === "correspondenceAddress") {
        const addrRegex = /^[^\$\"<>?\\\\~`!@$%^()+={}\[\]*:;“”‘’]{1,500}$/i;
        if (!addrRegex.test(value)) {
          errorMsg = t("NOC_ADDRESS_ERROR_MESSAGE");
        }
      } else if (key === "dob") {
        if (!validateAge(value)) {
          errorMsg = t("NOC_APPLICANT_AGE_VALIDATION");
        }
      }
    }

    setFieldErrors((prev) => {
      const copy = { ...prev };
      if (!copy[index]) copy[index] = {};
      if (errorMsg) {
        copy[index][key] = errorMsg;
      } else {
        delete copy[index][key];
      }
      return copy;
    });
  };

  function handleTextInputField(index, e, key) {
    const val = e.target.value;
    dispatch({
      type: "EDIT_CURRENT_OWNER_PROPERTY",
      payload: {
        index,
        key,
        value: val,
      },
    });
    validateFieldRealTime(index, key, val);
  }

  function handleSelectorInput(index, value, key) {
    dispatch({
      type: "EDIT_CURRENT_OWNER_PROPERTY",
      payload: {
        index,
        key,
        value,
      },
    });
    const val = value && typeof value === "object" ? value.code : value;
    validateFieldRealTime(index, key, val);
  }

  const validateAge = (dobString) => {
    if (!dobString) return false;
    const dob = new Date(dobString);
    const today = new Date();
    let age = today.getFullYear() - dob.getFullYear();
    const monthDiff = today.getMonth() - dob.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) {
      age--;
    }
    return age >= 18;
  };

  function validateForm(ownersData) {
    if (!selectedSubCategory) {
      setError("NOC_ERROR_SELECT_OWNERSHIP_CATEGORY");
      return false;
    }
    let hasFormatError = false;
    Object.keys(fieldErrors).forEach((idx) => {
      if (Object.keys(fieldErrors[idx]).length > 0) {
        hasFormatError = true;
      }
    });
    if (hasFormatError) {
      setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
      return false;
    }
    if (typeOfOwner === "INSTITUTIONAL") {
      const field = ownersData[0];
      if (
        !field?.institutionName ||
        !field?.telephoneNumber ||
        !field?.name ||
        !field?.institutionDesignation ||
        !field?.mobileNumber ||
        !field?.emailId ||
        !field?.correspondenceAddress
      ) {
        setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
        return false;
      }
    } else {
      for (const field of ownersData) {
        if (
          !field?.name ||
          !field?.mobileNumber ||
          !field?.dob ||
          !field?.fatherOrHusbandName ||
          !field?.relationship ||
          !field?.correspondenceAddress ||
          !field?.ownerType
        ) {
          setError("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS");
          return false;
        }
        if (field?.dob && !validateAge(field.dob)) {
          setError("NOC_APPLICANT_AGE_VALIDATION");
          return false;
        }
      }
    }
    setError(null);
    return true;
  }

  const onSkip = () => onSelect();

  function goNext() {
    if (!selectedSubCategory) {
      setError("NOC_ERROR_SELECT_OWNERSHIP_CATEGORY");
      return;
    }
    if (validateForm(formState)) {
      onSelect(config.key, {
        owners: formState,
        ownershipCategory: {
          ownerShipMajorType: selectedMajorCategory,
          ownerShipType: selectedSubCategory,
          ownershipCategory: selectedSubCategory
        }
      });
    }
  }
  const getTodayDate = () => {
    const today = new Date();
    const dd = String(today.getDate()).padStart(2, '0');
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const yyyy = today.getFullYear();
    return `${yyyy}-${mm}-${dd}`;
  };

  if (isSpecialLoading || isOwnershipLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <Timeline currentStep={3} />
      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t} isDisabled={false}>
        <CardLabel>{t("NOC_APPLICANT_TYPE_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
        <Dropdown
          selected={selectedMajorCategory}
          option={ownershipData?.major || []}
          select={handleMajorCategoryChange}
          optionKey="i18nKey"
          t={t}
          placeholder={t("NOC_APPLICANT_TYPE_PLACEHOLDER")}
          style={{ marginBottom: "20px" }}
        />

        <CardLabel>{t("NOC_APPLICANT_SUBTYPE_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
        <Dropdown
          selected={selectedSubCategory}
          option={filteredSubCategories || []}
          select={handleSubCategoryChange}
          optionKey="i18nKey"
          t={t}
          placeholder={t("NOC_APPLICANT_SUBTYPE_PLACEHOLDER")}
          style={{ marginBottom: "20px" }}
        />

        {typeOfOwner === "INSTITUTIONAL" && (
          <div>
            {formState?.map((field, index) => (
              <div key={index}>
                <CardLabel>{t("NOC_INSTITUTION_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <TextInput
                  t={t}
                  type="text"
                  name="institutionName"
                  value={field.institutionName || ""}
                  onChange={(e) => handleTextInputField(index, e, "institutionName")}
                  placeholder={t("NOC_ENTER_INSTITUTION_PLACEHOLDER")}
                  validation={{
                    isRequired: true,
                    pattern: Digit.Utils.getPattern("Name").source,
                    title: t("NOC_INSTITUTION_NAME_ERROR_MESSAGE"),
                  }}
                />
                {fieldErrors?.[index]?.institutionName && <CardLabelError>{fieldErrors[index].institutionName}</CardLabelError>}
                <CardLabel>{t("NOC_TELEPHONE_NUMBER_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <div className="field-container">
                  <span className="employee-card-input employee-card-input--front">+91</span>
                  <TextInput
                    type="text"
                    t={t}
                    name="telephoneNumber"
                    value={field.telephoneNumber || ""}
                    onChange={(e) => handleTextInputField(index, e, "telephoneNumber")}
                    placeholder={t("NOC_ENTER_TELEPHONE_NUMBER_PLACEHOLDER")}
                    validation={{
                      isRequired: true,
                      pattern: Digit.Utils.getPattern("MobileNo").source,
                      title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
                    }}
                  />
                </div>
                {fieldErrors?.[index]?.telephoneNumber && <CardLabelError>{fieldErrors[index].telephoneNumber}</CardLabelError>}
                <CardHeader style={{ marginTop: "20px" }}>{t("NOC_AUTHORIZED_PERSON_DETAILS")}</CardHeader>
                <CardLabel>{t("NOC_AUTHORIZED_PERSON_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <TextInput
                  t={t}
                  type="text"
                  name="name"
                  value={field.name || ""}
                  onChange={(e) => handleTextInputField(index, e, "name")}
                  placeholder={t("NOC_ENTER_AUTHORIZED_PERSON_PLACEHOLDER")}
                  validation={{
                    isRequired: true,
                    pattern: Digit.Utils.getPattern("Name").source,
                    title: t("NOC_NAME_ERROR_MESSAGE"),
                  }}
                />
                {fieldErrors?.[index]?.name && <CardLabelError>{fieldErrors[index].name}</CardLabelError>}
                <CardLabel>{t("NOC_INSTITUTION_DESIGNATION_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <TextInput
                  t={t}
                  type="text"
                  name="institutionDesignation"
                  value={field.institutionDesignation || ""}
                  onChange={(e) => handleTextInputField(index, e, "institutionDesignation")}
                  placeholder={t("NOC_ENTER_INSTITUTION_DESIGNATION_PLACEHOLDER")}
                  validation={{
                    isRequired: true,
                    pattern: Digit.Utils.getPattern("Name").source,
                    title: t("NOC_DESIGNATION_ERROR_MESSAGE"),
                  }}
                />
                {fieldErrors?.[index]?.institutionDesignation && <CardLabelError>{fieldErrors[index].institutionDesignation}</CardLabelError>}
                <CardLabel>{t("NOC_AUTHORIZED_PERSON_MOBILE_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <div className="field-container">
                  <span className="employee-card-input employee-card-input--front">+91</span>
                  <TextInput
                    type="text"
                    t={t}
                    name="mobileNumber"
                    value={field.mobileNumber || ""}
                    onChange={(e) => handleTextInputField(index, e, "mobileNumber")}
                    placeholder={t("NOC_AUTHORIZED_PERSON_MOBILE_PLACEHOLDER")}
                    validation={{
                      isRequired: true,
                      pattern: Digit.Utils.getPattern("MobileNo").source,
                      title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
                    }}
                  />
                </div>
                {fieldErrors?.[index]?.mobileNumber && <CardLabelError>{fieldErrors[index].mobileNumber}</CardLabelError>}
                <CardLabel>{t("NOC_AUTHORIZED_PERSON_EMAIL_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <TextInput
                  t={t}
                  type="text"
                  name="emailId"
                  value={field.emailId || ""}
                  onChange={(e) => handleTextInputField(index, e, "emailId")}
                  placeholder={t("NOC_AUTHORIZED_PERSON_EMAIL_PLACEHOLDER")}
                  validation={{
                    isRequired: true,
                    pattern: Digit.Utils.getPattern("Email").source,
                    title: t("NOC_EMAIL_ERROR_MESSAGE"),
                  }}
                />
                {fieldErrors?.[index]?.emailId && <CardLabelError>{fieldErrors[index].emailId}</CardLabelError>}
                <CardLabel>{t("NOC_OFFICIAL_CORRESPONDENCE_ADDRESS_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <TextInput
                  t={t}
                  type="text"
                  name="correspondenceAddress"
                  value={field.correspondenceAddress || ""}
                  onChange={(e) => handleTextInputField(index, e, "correspondenceAddress")}
                  placeholder={t("NOC_ENTER_OFFICIAL_CORRESPONDENCE_ADDRESS_PLACEHOLDER")}
                  validation={{
                    isRequired: true,
                    pattern: Digit.Utils.getPattern("Address").source,
                    title: t("NOC_ADDRESS_ERROR_MESSAGE"),
                  }}
                />
                {fieldErrors?.[index]?.correspondenceAddress && <CardLabelError>{fieldErrors[index].correspondenceAddress}</CardLabelError>}
              </div>
            ))}
          </div>
        )}

        {(typeOfOwner === "SINGLEOWNER" || typeOfOwner === "MULTIOWNER") && (
          <div>
            {formState?.map((field, index) => (
              <div
                key={index}
                style={
                  typeOfOwner === "MULTIOWNER"
                    ? {
                      border: "solid",
                      borderRadius: "5px",
                      padding: "15px",
                      paddingTop: "20px",
                      marginTop: "15px",
                      borderColor: "#f3f3f3",
                      background: "#FAFAFA",
                    }
                    : {}
                }
              >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <CardLabel style={{ fontWeight: "bold" }}>
                    {typeOfOwner === "MULTIOWNER" ? `${t("NOC_APPLICANT_INFORMATION_SUBHEADER")} - ${index + 1}` : t("NOC_APPLICANT_INFORMATION_SUBHEADER")}
                  </CardLabel>
                  {typeOfOwner === "MULTIOWNER" && formState.length > 1 && (
                    <LinkButton
                      label={t("NOC_REMOVE_OWNER_LABEL")}
                      onClick={() => dispatch({ type: "REMOVE_THIS_OWNER", payload: { index } })}
                      style={{ color: "#FE7A51", fontSize: "14px" }}
                    />
                  )}
                </div>

                <CardLabel>{t("NOC_APPLICANT_MOBILE_NO_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <div className="field-container">
                  <span className="employee-card-input employee-card-input--front">+91</span>
                  <TextInput
                    type="text"
                    t={t}
                    name="mobileNumber"
                    value={field.mobileNumber || ""}
                    onChange={(e) => handleTextInputField(index, e, "mobileNumber")}
                    placeholder={t("NOC_ENTER_APPLICANT_MOBILE_NO_PLACEHOLDER")}
                    validation={{
                      isRequired: true,
                      pattern: Digit.Utils.getPattern("MobileNo").source,
                      title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
                    }}
                  />
                </div>
                {fieldErrors?.[index]?.mobileNumber && <CardLabelError>{fieldErrors[index].mobileNumber}</CardLabelError>}

                <CardLabel>{t("NOC_APPLICANT_NAME_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <TextInput
                  t={t}
                  type="text"
                  name="name"
                  value={field.name || ""}
                  onChange={(e) => handleTextInputField(index, e, "name")}
                  placeholder={t("NOC_ENTER_APPLICANT_NAME_PLACEHOLDER")}
                  validation={{
                    isRequired: true,
                    pattern: Digit.Utils.getPattern("Name").source,
                    title: t("NOC_NAME_ERROR_MESSAGE"),
                  }}
                />
                {fieldErrors?.[index]?.name && <CardLabelError>{fieldErrors[index].name}</CardLabelError>}

                <CardLabel>{t("NOC_GENDER_LABEL")}</CardLabel>
                <RadioButtons
                  t={t}
                  options={genderOptions}
                  optionsKey="i18nKey"
                  name={`gender-${index}`}
                  selectedOption={field.gender}
                  onSelect={(val) => handleSelectorInput(index, val, "gender")}
                />

                <CardLabel>{t("NOC_APPLICANT_DOB_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <TextInput
                  t={t}
                  type="date"
                  name="dob"
                  value={field.dob || ""}
                  onChange={(e) => handleTextInputField(index, e, "dob")}
                  max={getTodayDate()}
                  placeholder={t("NOC_ENTER_APPLICANT_DOB_PLACEHOLDER")}
                  validation={{
                    isRequired: true,
                  }}
                />
                {fieldErrors?.[index]?.dob && <CardLabelError>{fieldErrors[index].dob}</CardLabelError>}

                <CardLabel>{t("NOC_APPLICANT_EMAIL_LABEL")}</CardLabel>
                <TextInput
                  t={t}
                  type="text"
                  name="emailId"
                  value={field.emailId || ""}
                  onChange={(e) => handleTextInputField(index, e, "emailId")}
                  placeholder={t("NOC_ENTER_APPLICANT_EMAIL_PLACEHOLDER")}
                  validation={{
                    pattern: Digit.Utils.getPattern("Email").source,
                    title: t("NOC_EMAIL_ERROR_MESSAGE"),
                  }}
                />
                {fieldErrors?.[index]?.emailId && <CardLabelError>{fieldErrors[index].emailId}</CardLabelError>}

                <CardLabel>{t("NOC_APPLICANT_FATHER_HUSBAND_NAME_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <TextInput
                  t={t}
                  type="text"
                  name="fatherOrHusbandName"
                  value={field.fatherOrHusbandName || ""}
                  onChange={(e) => handleTextInputField(index, e, "fatherOrHusbandName")}
                  placeholder={t("NOC_APPLICANT_FATHER_HUSBAND_NAME_PLACEHOLDER")}
                  validation={{
                    isRequired: true,
                    pattern: Digit.Utils.getPattern("Name").source,
                    title: t("NOC_NAME_ERROR_MESSAGE"),
                  }}
                />
                {fieldErrors?.[index]?.fatherOrHusbandName && <CardLabelError>{fieldErrors[index].fatherOrHusbandName}</CardLabelError>}

                <CardLabel>{t("NOC_APPLICANT_RELATIONSHIP_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <RadioButtons
                  t={t}
                  options={relationshipOptions}
                  optionsKey="i18nKey"
                  name={`relationship-${index}`}
                  selectedOption={field.relationship}
                  onSelect={(val) => handleSelectorInput(index, val, "relationship")}
                />

                <CardLabel>{t("NOC_APPLICANT_PAN_LABEL")}</CardLabel>
                <TextInput
                  t={t}
                  type="text"
                  name="pan"
                  value={field.pan || ""}
                  onChange={(e) => handleTextInputField(index, e, "pan")}
                  placeholder={t("NOC_ENTER_APPLICANT_PAN_PLACEHOLDER")}
                  validation={{
                    pattern: Digit.Utils.getPattern("PAN").source,
                    title: t("NOC_PAN_ERROR_MESSAGE"),
                  }}
                />
                {fieldErrors?.[index]?.pan && <CardLabelError>{fieldErrors[index].pan}</CardLabelError>}

                <CardLabel>{t("NOC_APPLICANT_CORRESPONDENCE_ADDRESS_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <TextInput
                  t={t}
                  type="text"
                  name="correspondenceAddress"
                  value={field.correspondenceAddress || ""}
                  onChange={(e) => handleTextInputField(index, e, "correspondenceAddress")}
                  placeholder={t("NOC_ENTER_CORRESPONDENCE_ADDRESS_PLACEHOLDER")}
                  validation={{
                    isRequired: true,
                    pattern: Digit.Utils.getPattern("Address").source,
                    title: t("NOC_ADDRESS_ERROR_MESSAGE"),
                  }}
                />
                {fieldErrors?.[index]?.correspondenceAddress && <CardLabelError>{fieldErrors[index].correspondenceAddress}</CardLabelError>}

                <CardLabel>{t("NOC_SPECIAL_APPLICANT_CATEGORY_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
                <Dropdown
                  t={t}
                  option={specialCategories}
                  selected={field.ownerType}
                  select={(val) => handleSelectorInput(index, val, "ownerType")}
                  optionKey="i18nKey"
                  placeholder={t("NOC_SPECIAL_APPLICANT_CATEGORY_PLACEHOLDER")}
                />
              </div>
            ))}

            {typeOfOwner === "MULTIOWNER" && (
              <div style={{ marginTop: "15px" }}>
                <button
                  type="button"
                  onClick={() => dispatch({ type: "ADD_NEW_OWNER" })}
                  style={{
                    color: "#FE7A51",
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                    fontWeight: "bold",
                    padding: "10px 0px",
                  }}
                >
                  {t("NOC_ADD_APPLICANT_LABEL")}
                </button>
              </div>
            )}
          </div>
        )}
      </FormStep>
      {error && <Toast error={true} label={t(error)} onClose={() => setError(null)} />}
    </React.Fragment>
  );
};

export default NocOwnerDetails;
