import { CardLabel, FormStep,RadioButtons, TextInput, CheckBox, LinkButton, MobileNumber,Toast ,Dropdown } from "@upyog/digit-ui-react-components";
import React, { useState,useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/Timeline";
import { calculateAge } from "../utils";



const SVApplicantDetails = ({ t, config, onSelect, userType, formData,editdata,previousData }) => {
  let validation = {};
  const user = Digit.UserService.getUser().info;
  const convertToObject = (gender) => {
    if (!gender) return null;
    switch (gender.toUpperCase()) {
      case "M":
        return { i18nKey: "MALE", code: "MALE", value: "Male" };
      case "F":
        return { i18nKey: "FEMALE", code: "FEMALE", value: "Female" };
      case "O":
        return { i18nKey: "OTHERS", code: "OTHERS", value: "Others" };
      default:
        return null;
    }
  };
  const Objectconvert = (String) => String ? { i18nKey: String, code: String, value: String } : null;
  const [vendorName, setvendorName] = useState(formData?.owner?.units?.vendorName || formData?.owner?.vendorName || "");
  const [userCategory, setownerTypeCategory] = useState(formData?.owner?.units?.userCategory||formData?.owner?.userCategory||"");
  const [vendorDateOfBirth, setvendorDateOfBirth] = useState(formData?.owner?.units?.vendorDateOfBirth || formData?.owner?.vendorDateOfBirth || "");
  const [gender, setgender] = useState(formData?.owner?.units?.gender || formData?.owner?.gender || "");
  const [fatherName, setfatherName] = useState(editdata?.vendorDetail?.[0]?.fatherName||formData?.owner?.units?.fatherName || formData?.owner?.fatherName || "");
  const [spouseName, setspouseName] = useState(formData?.owner?.units?.spouseName || formData?.owner?.spouseName || "");
  const [spousedependent, setspousedependent] = useState(false)
  const [mobileNumber, setmobileNumber] = useState(formData?.owner?.units?.mobileNumber || formData?.owner?.mobileNumber || "");
  const [spouseDateBirth, setspouseDateBirth] = useState(formData?.owner?.units?.spouseDateBirth || formData?.owner?.spouseDateBirth || "");
  const [dependentName, setdependentName] = useState(formData?.owner?.units?.dependentName || formData?.owner?.dependentName || "");
  const [dependentDateBirth, setdependentDateBirth] = useState(formData?.owner?.units?.dependentDateBirth || formData?.owner?.dependentDateBirth || "");
  const [dependentGender, setdependentGender] = useState(formData?.owner?.units?.dependentGender || formData?.owner?.dependentGender || "");
  const [email, setemail] = useState(formData?.owner?.units?.email || formData?.owner?.email || "");
  const [tradeNumber, settradeNumber] = useState(formData?.owner?.units?.tradeNumber || formData?.owner?.tradeNumber || "");
  const [error, setError] = useState(null);

  const [spouseDependentChecked, setSpouseDependentChecked] = useState(formData?.owner?.spouseDependentChecked || false);
  const [dependentNameChecked, setDependentNameChecked] = useState(formData?.owner?.dependentNameChecked || false);
  const inputStyles = user.type === "EMPLOYEE" ? "50%" : "86%";
  const [showToast, setShowToast] = useState(null);
  const { control } = useForm();

  const [fields, setFeilds] = useState((formData?.owner && formData?.owner?.units) || [{ vendorName: (previousData?.vendorDetail?.[0]?.name ||editdata?.vendorDetail?.[0]?.name ||(user?.type==="CITIZEN"?user?.name:"") || ""),userCategory:(Objectconvert(previousData?.vendorDetail?.[0]?.userCategory||editdata?.vendorDetail?.[0]?.userCategory)||""), vendorDateOfBirth:(previousData?.vendorDetail?.[0]?.dob||editdata?.vendorDetail?.[0]?.dob|| ""), gender: convertToObject(previousData?.vendorDetail?.[0]?.gender||editdata?.vendorDetail?.[0]?.gender)||"", fatherName: (previousData?.vendorDetail?.[0]?.fatherName||editdata?.vendorDetail?.[0]?.fatherName||""), spouseName: (previousData?.vendorDetail?.[1]?.name||editdata?.vendorDetail?.[1]?.name||""), mobileNumber: (previousData?.vendorDetail?.[0]?.mobileNo||editdata?.vendorDetail?.[0]?.mobileNo||(user?.type==="CITIZEN"?user?.mobileNumber:"") || ""), spouseDateBirth: (previousData?.vendorDetail?.[1]?.dob||editdata?.vendorDetail?.[1]?.dob|| ""), dependentName: (previousData?.vendorDetail?.[2]?.name||editdata?.vendorDetail?.[2]?.name||""), dependentDateBirth: (previousData?.vendorDetail?.[2]?.dob||editdata?.vendorDetail?.[2]?.dob||""), dependentGender: (convertToObject(previousData?.vendorDetail?.[2]?.gender||editdata?.vendorDetail?.[2]?.gender)||""), email:(previousData?.vendorDetail?.[0]?.emailId||editdata?.vendorDetail?.[0]?.emailId||(user?.type==="CITIZEN"?user?.emailId:"") || ""), tradeNumber:(previousData?.vendorDetail?.[0]?.tradeNumber||editdata?.vendorDetail?.[0]?.tradeNumber||"")}]);

  function handleAdd() {
    const values = [...fields];
    values.push({ vendorName: "", userCategory:"",vendorDateOfBirth: "", gender: "", fatherName: "", spouseName: "", mobileNumber: "", spouseDateBirth: "", dependentName: "", dependentDateBirth: "", dependentGender: "", email:"", tradeNumber:""});
    setFeilds(values);
  }
  const validateEmail = (value) => {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-z.-]+\.(com|org|in)$/;
    if (value === "") {
      setError("");
    }
    else if (emailPattern.test(value)) {
      setError("");
    }
    else {
      setError(t("CORE_INVALID_EMAIL_ID_PATTERN"));
    }
  }

  function handleRemove(index) {
    const values = [...fields];
    if (values.length != 1) {
      values.splice(index, 1);
      setFeilds(values);
    }
  }

  const { data: Category } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "StreetVending", [{ name: "Category" }],
    {
      select: (data) => {
        const formattedData = data?.["StreetVending"]?.["Category"]
        return formattedData;
      },
    });
  let category_Options = [];
  Category && Category.map((category) => {
    category_Options.push({ i18nKey: `${category.name}`, code: `${category.code}`, value: `${category.name}` })
  })

  /**
   * Single function to validate age for vendor, spouse, and dependent
   * Returns false if validation fails, true if passes
   */
  const validateAge = (date, type) => {
    if (!date) return true;

    const age = calculateAge(date);
    if (age < 18) {
      const errorMessages = {
        vendor: "VENDOR_AGE_ERROR",
        spouse: "SPOUSE_AGE_ERROR",
        dependent: "DEPENDENT_AGE_ERROR"
      };
      setShowToast({ error: true, label: t(errorMessages[type]) });
      return false;
    }

    return true;
  };

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 2000); // Close toast after 1 seconds

      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);


  const common = [
    {
      code: "MALE",
      i18nKey: "MALE",
      value: "Male"
    },
    {
      code: "FEMALE",
      i18nKey: "FEMALE",
      value: "Female"
    },
    {
      code: "OTHERS",
      i18nKey: "OTHERS",
      value: "Others"
    },
  ]

  const setSpouseDependentHandler = () => {
    setSpouseDependentChecked(!spouseDependentChecked);
  };

  const setDependentNameHandler = () => {
    setDependentNameChecked(!dependentNameChecked);
  };

  function selectvendorName(i, e) {
    if (/\d/.test(e.target.value)) {
      setShowToast({ error: true, label: t("SV_CITIZEN_VALIDATION_FORMAT") });
    }
    let units = [...fields];
    units[i].vendorName = e.target.value;
    setvendorName(e.target.value);
    setFeilds(units);
  }
  function selectvendorDateOfBirth(i, e) {
    let units = [...fields];
    units[i].vendorDateOfBirth = e.target.value;
    setvendorDateOfBirth(e.target.value);
    setFeilds(units);
    validateAge(e.target.value, "vendor");
  }
  function selectgender(i, value) {
    let units = [...fields];
    units[i].gender = value;
    setgender(value);
    setFeilds(units);
  }
  function selectownertype(i, value) {
    let units = [...fields];
    units[i].userCategory = value;
    setownerTypeCategory(value);
    setFeilds(units);
  }
  function selectfatherName(i, e) {
    if (/\d/.test(e.target.value)) {
      setShowToast({ error: true, label: t("SV_CITIZEN_VALIDATION_FORMAT") });
    }
    let units = [...fields];
    units[i].fatherName = e.target.value;
    setfatherName(e.target.value);
    setFeilds(units);
  }

  function selectspouseName(i, e) {
    if (/\d/.test(e.target.value)) {
      setShowToast({ error: true, label: t("SV_CITIZEN_VALIDATION_FORMAT") });
    }
    let units = [...fields];
    units[i].spouseName = e.target.value;
    setspouseName(e.target.value);
    setFeilds(units);
  }

  function selectmobileNumber(i, e) {
    let units = [...fields];
    units[i].mobileNumber = e;
    setmobileNumber(e);
    setFeilds(units);
  }

  function selectspouseDateBirth(i, e) {
    let units = [...fields];
    units[i].spouseDateBirth = e.target.value;
    setspouseDateBirth(e.target.value);
    setFeilds(units);
    validateAge(e.target.value, "spouse");
  }

  function selectdependentDateBirth(i, e) {
    let units = [...fields];
    units[i].dependentDateBirth = e.target.value;
    setdependentDateBirth(e.target.value);
    setFeilds(units);
    validateAge(e.target.value, "dependent");
  }

  function selectdependentName(i, e) {
    if (/\d/.test(e.target.value)) {
      setShowToast({ error: true, label: t("SV_CITIZEN_VALIDATION_FORMAT") });
    }
    let units = [...fields];
    units[i].dependentName = e.target.value;
    setdependentName(e.target.value);
    setFeilds(units);
  }

  function selectdependentGender(i, value) {
    let units = [...fields];
    units[i].dependentGender = value;
    setdependentGender(value);
    setFeilds(units);
  }

  function selectemail(i, e) {
    let units = [...fields];
    units[i].email = e.target.value;
    const value = e.target.value
    setemail(e.target.value);
    validateEmail(value);
    setFeilds(units);
  }
  useEffect(() => {
    if (email) {
      validateEmail(email);
    }
  }, [email])

  function selecttradeNumber(i, e) {
    let units = [...fields];
    units[i].tradeNumber = e.target.value;
    settradeNumber(e.target.value);
    setFeilds(units);
  }


  //Custom function fo rthe payload whic we can use while goint to next

  const handleSaveasDraft=()=>{
    let vendordetails = [];
    let tenantId=Digit.ULBService.getCitizenCurrentTenant(true);
  const createVendorObject = (fields) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: fields?.[0]?.vendorDateOfBirth,
    userCategory:fields?.[0]?.userCategory?.code,
    emailId: fields?.[0]?.email,
    fatherName: fields?.[0]?.fatherName,
    gender: fields?.[0]?.gender?.code.charAt(0),
    id: "",
    mobileNo: fields?.[0]?.mobileNumber,
    name: fields?.[0]?.vendorName,
    relationshipType: "VENDOR",
    vendorId: null
  });

  const createSpouseObject = (fields) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: fields?.[1]?.spouseDateBirth,
    userCategory:fields?.[1]?.userCategory?.code,
    emailId: "",
    isInvolved: fields?.spouseDependentChecked,
    fatherName: "",
    gender: "O",
    id: "",
    mobileNo: "",
    name: fields?.[1]?.spouseName,
    relationshipType: "SPOUSE",
    vendorId: null
  });

  const createDependentObject = (fields) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: fields?.[2]?.dependentDateBirth,
    userCategory:fields?.[2]?.userCategory?.code,
    emailId: "",
    isInvolved: fields?.dependentNameChecked,
    fatherName: "",
    gender: fields?.[2]?.dependentGender?.code.charAt(0),
    id: "",
    mobileNo: "",
    name: fields?.[2]?.dependentName,
    relationshipType: "DEPENDENT",
    vendorId: null
  });

  // Helper function to check if a string is empty or undefined
  const isEmpty = (str) => !str || str.trim() === '';

  // Main logic
  if (!isEmpty(fields?.[0]?.vendorName)) {
    const spouseName = fields?.[0]?.spouseName;
    const dependentName = fields?.[0]?.dependentName;

    if (isEmpty(spouseName) && isEmpty(dependentName)) {
      // Case 1: Only vendor exists
      vendordetails = [createVendorObject(fields)];
    } else if (!isEmpty(spouseName) && isEmpty(dependentName)) {
      // Case 2: Both vendor and spouse exist
      vendordetails = [
        createVendorObject(fields),
        createSpouseObject(fields)
      ];
    } else if (!isEmpty(spouseName) && !isEmpty(dependentName)) {
      // Case 3: All three exist (vendor, spouse, and dependent)
      vendordetails = [
        createVendorObject(fields),
        createSpouseObject(fields),
        createDependentObject(fields)
      ];
    }
  }

  const api_response = sessionStorage.getItem("Response");
  const response = api_response?JSON.parse(api_response):null;

    let streetVendingDetail= {
      addressDetails: [
        {
          addressId: "",
          addressLine1: "",
          addressLine2: "",
          addressType: "",
          city: "",
          cityCode: "",
          doorNo: "",
          houseNo: "",
          landmark: "",
          locality: "",
          localityCode: "",
          pincode: "",
          streetName: "",
          vendorId: ""
        },
        { // sending correspondence address here
          addressId: "",
          addressLine1: "",
          addressLine2: "",
          addressType: "",
          city: "",
          cityCode: "",
          doorNo: "",
          houseNo: "",
          landmark: "",
          locality: "",
          localityCode: "",
          pincode: "",
          streetName: "",
          vendorId: "",
          isAddressSame: ""
        }
      ],
      applicationDate: 0,
      applicationId: "",
      applicationNo: "",
      applicationStatus: "",
      approvalDate: 0,
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
      bankDetail: {
        accountHolderName: "",
        accountNumber: "",
        applicationId: "",
        bankBranchName: "",
        bankName: "",
        id: "",
        ifscCode: "",
        refundStatus: "",
        refundType: "",
        auditDetails: {
          createdBy: "",
          createdTime: 0,
          lastModifiedBy: "",
          lastModifiedTime: 0
        },
      },
      benificiaryOfSocialSchemes: "",
      enrollmentId:"",
      cartLatitude: 0,
      cartLongitude: 0,
      certificateNo: null,
      disabilityStatus: "",
      draftId: previousData?.draftId||response?.SVDetail?.draftId||"",
      documentDetails: [
        {
          applicationId: "",
          auditDetails: {
            createdBy: "",
            createdTime: 0,
            lastModifiedBy: "",
            lastModifiedTime: 0
          },
          documentDetailId: "",
          documentType: "",
          fileStoreId: ""
        }
      ],
      localAuthorityName: "",
      tenantId: tenantId,
      termsAndCondition: "Y",
      tradeLicenseNo: fields?.[0]?.tradeNumber,
      vendingActivity: "",
      vendingArea: "0",
      vendingLicenseCertificateId: "",
      vendingOperationTimeDetails: [
        {
          applicationId: "",
          auditDetails: {
            createdBy: "",
            createdTime: 0,
            lastModifiedBy: "",
            lastModifiedTime: 0
          },
          dayOfWeek: "MONDAY",  // Need to check why showuld we have to forward it
          fromTime: "09:00",
          id: "",
          toTime: "09:00"
        }
      ],
      vendingZone:  "",
      vendorDetail: [
        ...vendordetails
      ],
      workflow: {
        action: "APPLY",
        comments: "",
        businessService: "street-vending",
        moduleName: "sv-services",
        businessService: "street-vending",
        moduleName: "sv-services",
        varificationDocuments: [
          {
            additionalDetails: {},
            auditDetails: {
              createdBy: "",
              createdTime: 0,
              lastModifiedBy: "",
              lastModifiedTime: 0
            },
            documentType: "",
            documentUid: "",
            fileStoreId: "",
            id: ""
          }
        ]
      }
    };

    Digit.SVService.create({streetVendingDetail, draftApplication:true},tenantId)
    .then(response=>{
      sessionStorage.setItem("Response",JSON.stringify(response));
    })
    .catch(error=>{
      console.log("Something Went Wrong",error);
    })

  };




  const goNext = () => {

    // Validate all applicable dates before proceeding
    const isVendorAgeValid = validateAge(fields[0].vendorDateOfBirth, "vendor");
    const isSpouseAgeValid = !spouseDependentChecked || validateAge(fields[0].spouseDateBirth, "spouse");
    const isDependentAgeValid = !dependentNameChecked || validateAge(fields[0].dependentDateBirth, "dependent");

    if (!isVendorAgeValid || !isSpouseAgeValid || !isDependentAgeValid) {
      return;
    }

    let ownerDetails = formData.owner || {};
    let ownerStep = {
      ...ownerDetails,
      units: fields,
      spouseDependentChecked,
      dependentNameChecked
    };
    onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
    window.location.href.includes("edit")?null: handleSaveasDraft();

  };

  const onSkip = () => onSelect();
  return (
    <React.Fragment>
      {<Timeline currentStep={1}/>}
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!fields[0].vendorName || !fields[0].userCategory|| !fields[0].vendorDateOfBirth || !fields[0].gender || !fields[0].fatherName || (spouseDependentChecked && !fields[0].spouseName) || (dependentNameChecked && !fields[0].dependentName)}
        >
          {fields.map((field, index) => {
            return (
              <div key={`${field}-${index}`}>
                <div
                  style={{
                    border: "solid",
                    borderRadius: "5px",
                    padding: "10px",
                    paddingTop: "20px",
                    marginTop: "10px",
                    borderColor: "#f3f3f3",
                    background: "#FAFAFA",
                    width:inputStyles,
                  }}
                >
                <CardLabel>{`${t("SV_VENDOR_NAME")}`} <span className="astericColor">*</span></CardLabel>
                <LinkButton
                  label={
                    <div>
                      <span>
                        <svg
                          style={{ float: "right", position: "relative", bottom: "32px" }}
                          width="24"
                          height="24"
                          viewBox="0 0 24 24"
                          fill="none"
                          xmlns="http://www.w3.org/2000/svg"
                        >
                          <path
                            d="M1 16C1 17.1 1.9 18 3 18H11C12.1 18 13 17.1 13 16V4H1V16ZM14 1H10.5L9.5 0H4.5L3.5 1H0V3H14V1Z"
                            fill={!(fields.length == 1) ? "#494848" : "#FAFAFA"}
                          />
                        </svg>
                      </span>
                    </div>
                  }
                  style={{ width: "100px", display: "inline" }}
                  onClick={(e) => handleRemove(index)}
                />
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  type={"text"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="vendorName"
                  value={field?.vendorName}
                  onChange={(e) => selectvendorName(index, e)}
                  disable={false}
                  ValidationRequired={true}
                  {...(validation = {
                    isRequired: true,
                    pattern: "^[a-zA-Z ]+$",
                    type: "text",
                    title: t("SV_ENTER_CORRECT_NAME"),
                  })}
                />
                <CardLabel>{`${t("SV_OWNER_CATEGORY")}`} <span className="astericColor">*</span></CardLabel>
                <Controller
                  control={control}
                  name={`userCategory-${index}`}
                  defaultValue={userCategory}
                  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                  render={(props) => (
                    <Dropdown
                      className="form-field"
                      style={{width:"100%"}}
                      selected={field?.userCategory}
                      select={(e) => selectownertype(index, e)}
                      option={category_Options}
                      optionKey="i18nKey"
                      t={t}
                      placeholder={"Select"}
                    />
                  )}
                />
                
                <CardLabel>{`${t("SV_REGISTERED_MOB_NUMBER")}`} <span className="astericColor">*</span></CardLabel>
                <MobileNumber
                  style={{ background: "#FAFAFA" }}
                  value={field?.mobileNumber}
                  name="mobileNumber"
                  onChange={(e) => selectmobileNumber(index, e)}
                  {...{
                    required: true,
                    pattern: "[6-9]{1}[0-9]{9}",
                    type: "tel",
                    title: t("SV_INVALID_NUMBER")
                  }}
                />

                <CardLabel>{`${t("SV_EMAIL")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  t={t}
                  type={"text"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="email"
                  value={field?.email}
                  onChange={(e) => selectemail(index, e)}
                  disable={false}
                />

                <CardLabel>{`${t("SV_DATE_OF_BIRTH")}`} <span className="astericColor">*</span></CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  t={t}
                  type={"date"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="vendorDateOfBirth"
                  value={field?.vendorDateOfBirth}
                  onChange={(e) => selectvendorDateOfBirth(index, e)}
                  disable={false}
                  max={new Date().toISOString().split('T')[0]}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                  }}
                />
                <CardLabel>{`${t("SV_GENDER")}`} <span className="astericColor">*</span></CardLabel>
                <RadioButtons
                  t={t}
                  options={common}
                  style={{ display: "flex", flexWrap: "wrap", maxHeight: "30px" }}
                  innerStyles={{ minWidth: "24%" }}
                  optionsKey="i18nKey"
                  name={`gender-${index}`}
                  value={field?.gender}
                  selectedOption={field?.gender}
                  onSelect={(e) => selectgender(index, e)}
                  labelKey="i18nKey"
                  isPTFlow={true}
                />
                <CardLabel>{`${t("SV_FATHER_NAME")}`} <span className="astericColor">*</span></CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  t={t}
                  type={"text"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="fatherName"
                  value={field?.fatherName}
                  onChange={(e) => selectfatherName(index, e)}
                  disable={false}
                  ValidationRequired={true}
                  {...(validation = {
                    isRequired: true,
                    pattern: "^[a-zA-Z ]+$",
                    type: "text",
                    title: t("SV_ENTER_CORRECT_NAME"),
                  })}
                />

                </div>

                <div
                  style={{
                    border: "solid",
                    borderRadius: "5px",
                    padding: "10px",
                    paddingTop: "20px",
                    marginTop: "10px",
                    borderColor: "#f3f3f3",
                    background: "#FAFAFA",
                    width: inputStyles,
                  }}
                >
                  {/* <div style={{ border: "1px solid black", borderRadius: "3px", padding: "5px" }}> */}
                  <div>
                    <div style={{ display: "flex", gap: "22px" }}>
                      <CardLabel>{`${t("SV_SPOUSE_NAME")}`} {spouseDependentChecked && <span className="astericColor">*</span>}</CardLabel>
                      <CheckBox
                        label={t("SV_INVOLVED_IN_VENDING")}
                        onChange={setSpouseDependentHandler}
                        checked={spouseDependentChecked}
                      />
                    </div>
                    <TextInput
                      style={{ background: "#FAFAFA" }}
                      t={t}
                      type={"text"}
                      isMandatory={false}
                      optionKey="i18nKey"
                      name="spouseName"
                      value={field?.spouseName}
                      onChange={(e) => selectspouseName(index, e)}
                      disable={false}
                      ValidationRequired={spouseDependentChecked}
                      {...(validation = {
                        isRequired: spouseDependentChecked,
                        pattern: "^[a-zA-Z ]+$",
                        type: "text",
                        title: t("SV_ENTER_CORRECT_NAME"),
                      })}
                    />
                  </div>

                  <CardLabel>{`${t("SV_SPOUSE_DATE_OF_BIRTH")}`} {spouseDependentChecked && <span className="astericColor">*</span>}</CardLabel>
                  <TextInput
                    style={{ background: "#FAFAFA" }}
                    t={t}
                    type={"date"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="spouseDateBirth"
                    value={field?.spouseDateBirth}
                    onChange={(e) => selectspouseDateBirth(index, e)}
                    disable={false}
                    max={new Date().toISOString().split('T')[0]}
                    rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                    }}
                  />
                </div>

                {/* <div style={{ border: "1px solid black", borderRadius: "3px", padding: "5px" }}> */}
                <div
                  style={{
                    border: "solid",
                    borderRadius: "5px",
                    padding: "10px",
                    paddingTop: "20px",
                    marginTop: "10px",
                    borderColor: "#f3f3f3",
                    background: "#FAFAFA",
                    width: inputStyles,
                  }}
                >
                  <div>
                    <div style={{ display: "flex", gap: "22px" }}>
                      <CardLabel>{`${t("SV_DEPENDENT_NAME")}`} {dependentNameChecked && <span className="astericColor">*</span>}</CardLabel>
                      <CheckBox
                        label={t("SV_INVOLVED_IN_VENDING")}
                        onChange={setDependentNameHandler}
                        checked={dependentNameChecked}
                      />
                    </div>
                    <TextInput
                      style={{ background: "#FAFAFA" }}
                      t={t}
                      type={"text"}
                      isMandatory={false}
                      optionKey="i18nKey"
                      name="dependentName"
                      value={field?.dependentName}
                      onChange={(e) => selectdependentName(index, e)}
                      disable={false}
                      ValidationRequired={dependentNameChecked}
                      {...(validation = {
                        isRequired: dependentNameChecked,
                        pattern: "^[a-zA-Z ]+$",
                        type: "text",
                        title: t("SV_ENTER_CORRECT_NAME"),
                      })}
                    />

                  </div>
                  <CardLabel>{`${t("SV_DEPENDENT_DATE_OF_BIRTH")}`} {dependentNameChecked && <span className="astericColor">*</span>}</CardLabel>
                  <TextInput
                    style={{ background: "#FAFAFA" }}
                    t={t}
                    type={"date"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="dependentDateBirth"
                    value={field?.dependentDateBirth}
                    onChange={(e) => selectdependentDateBirth(index, e)}
                    disable={false}
                    max={new Date().toISOString().split('T')[0]}
                    rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                    }}
                  />
                  <CardLabel>{`${t("SV_DEPENDENT_GENDER")}`} {dependentNameChecked && <span className="astericColor">*</span>}</CardLabel>
                  <RadioButtons
                    t={t}
                    options={common}
                    style={{ display: "flex", flexWrap: "wrap", maxHeight: "30px" }}
                    innerStyles={{ minWidth: "24%" }}
                    optionsKey="i18nKey"
                    name={`dependentGender-${index}`}
                    value={field?.dependentGender}
                    selectedOption={field?.dependentGender}
                    onSelect={(e) => selectdependentGender(index, e)}
                    labelKey="i18nKey"
                    isPTFlow={true}
                  />

                </div>

                <div
                  style={{
                    border: "solid",
                    borderRadius: "5px",
                    padding: "10px",
                    paddingTop: "20px",
                    marginTop: "10px",
                    borderColor: "#f3f3f3",
                    background: "#FAFAFA",
                    width: inputStyles,
                  }}
                >
                <CardLabel>{`${t("SV_TRADE_NUMBER")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  t={t}
                  type={"text"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="tradeNumber"
                  value={field?.tradeNumber}
                  onChange={(e) => selecttradeNumber(index, e)}
                  disable={false}
                  ValidationRequired={true}
                  {...(validation = {
                    isRequired: false,
                    pattern: "^[a-zA-Z0-9-/ ]+$",
                    type: "text",
                    title: t("SV_ENTER_CORRECT_NAME"),
                  })}
                />

                <div className="astericColor" style={{ display: "flex", paddingBottom: "15px", color: "#FF8C00", marginTop: "10px" }}>
                  <button type="button" style={{ paddingTop: "10px" }} onClick={() => handleAdd()}>
                    {`${t("SV_ADD_DEPENDENT")}`}
                  </button>
                </div>
              </div>
              <br/>
            </div>
          );
        })}


      </FormStep>
      {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
    </React.Fragment>
  );
};
export default SVApplicantDetails;
