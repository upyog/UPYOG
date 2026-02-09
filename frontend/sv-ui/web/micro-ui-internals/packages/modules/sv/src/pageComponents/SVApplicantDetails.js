import { CardLabel, FormStep,RadioButtons, TextInput, CheckBox, LinkButton, MobileNumber,Toast ,Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState,useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/Timeline";
import { calculateAge, formatToInputDate } from "../utils";
import { useLocation } from "react-router-dom";
import { UPYOG_CONSTANTS } from "../utils";

/**
 * Component will render Applicant Details both citizen and employee side 
 */

const SVApplicantDetails = ({ t, config, onSelect, userType, formData,editdata,previousData }) => {
  let validation = {};
  const user = Digit.UserService.getUser().info;
  const { pathname } = useLocation();

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
  const [vendorName, setvendorName] = useState(formData?.owner?.vendorDetails?.vendorName || formData?.owner?.vendorName || "");
  const [userCategory, setownerTypeCategory] = useState(formData?.owner?.vendorDetails?.userCategory||formData?.owner?.userCategory||"");
  const [vendorDateOfBirth, setvendorDateOfBirth] = useState(formData?.owner?.vendorDetails?.vendorDateOfBirth || formData?.owner?.vendorDateOfBirth || "");
  const [gender, setgender] = useState(formData?.owner?.vendorDetails?.gender || formData?.owner?.gender || "");
  const [fatherName, setfatherName] = useState(editdata?.vendorDetail?.[0]?.fatherName||formData?.owner?.vendorDetails?.fatherName || formData?.owner?.fatherName || "");
  const [spouseName, setspouseName] = useState(formData?.owner?.spouseDetails?.spouseName || formData?.owner?.spouseName || "");
  const [spousedependent, setspousedependent] = useState(false)
  const [mobileNumber, setmobileNumber] = useState(formData?.owner?.vendorDetails?.mobileNumber || formData?.owner?.mobileNumber || "");
  const [spouseDateBirth, setspouseDateBirth] = useState(formData?.owner?.spouseDetails?.spouseDateBirth || formData?.owner?.spouseDateBirth || "");
  const [dependentName, setdependentName] = useState(formData?.owner?.dependentDetails?.dependentName || formData?.owner?.dependentName || "");
  const [dependentDateBirth, setdependentDateBirth] = useState(formData?.owner?.dependentDetails?.dependentDateBirth || formData?.owner?.dependentDateBirth || "");
  const [dependentGender, setdependentGender] = useState(formData?.owner?.dependentDetails?.dependentGender || formData?.owner?.dependentGender || "");
  const [email, setemail] = useState(formData?.owner?.vendorDetails?.email || formData?.owner?.email || "");
  const [tradeNumber, settradeNumber] = useState(formData?.owner?.units?.tradeNumber || formData?.owner?.tradeNumber || "");
  const [error, setError] = useState(null);

  const [spouseDependentChecked, setSpouseDependentChecked] = useState(formData?.owner?.spouseDependentChecked || editdata?.vendorDetail?.[1]?.isInvolved || false);
  const [dependentNameChecked, setDependentNameChecked] = useState(formData?.owner?.dependentNameChecked || editdata?.vendorDetail?.[2]?.isInvolved || false);
  const inputStyles = user.type === "EMPLOYEE" ? "50%" : "86%";
  const [showToast, setShowToast] = useState(null);
  const { control } = useForm();
  
   // Extracting details based on relationship type because vendorDetail is an array of objects
    const vendorPersonalDetails = previousData?.vendorDetail?.find((item)=>item.relationshipType===UPYOG_CONSTANTS.VENDOR) || editdata?.vendorDetail?.find((item)=>item.relationshipType===UPYOG_CONSTANTS.VENDOR)|| {};
    const vendorSpouseDetails = previousData?.vendorDetail?.find((item)=>item.relationshipType===UPYOG_CONSTANTS.SPOUSE) || editdata?.vendorDetail?.find((item)=>item.relationshipType===UPYOG_CONSTANTS.SPOUSE)|| {};
    const vendorDependentDetails = previousData?.vendorDetail?.filter((item) => item.relationshipType === UPYOG_CONSTANTS.DEPENDENT) || editdata?.vendorDetail?.filter((item) => item.relationshipType === UPYOG_CONSTANTS.DEPENDENT) || [];
    const [vendorDetails, setVendorDetails] = useState(()=> 
     formData?.owner?.vendorDetails || {
      vendorName: (vendorPersonalDetails?.name ||(user?.type==="CITIZEN"?user?.name:"") || ""),
      userCategory:(Objectconvert(vendorPersonalDetails?.userCategory)||""), 
      vendorDateOfBirth:formatToInputDate(vendorPersonalDetails?.dob|| ""), 
      gender: (convertToObject(vendorPersonalDetails?.gender)||""), 
      fatherName: (vendorPersonalDetails?.fatherName||""), 
      email:(vendorPersonalDetails?.emailId||(user?.type==="CITIZEN"?user?.emailId:"") || ""), 
      mobileNumber: (vendorPersonalDetails?.mobileNo||(user?.type==="CITIZEN"?user?.mobileNumber:"") || "")
    });

    const [spouseDetails, setSpouseDetails] = useState(()=>
      formData?.owner?.spouseDetails || {
      spouseName: (vendorSpouseDetails?.name||""),
      spouseDateBirth: formatToInputDate(vendorSpouseDetails?.dob|| ""),
    });

    const [dependentDetails, setDependentDetails] = useState(() => {
      // Priority 1: already filled form data
      if (
        Array.isArray(formData?.owner?.dependentDetails) &&
        formData.owner.dependentDetails.length > 0
      ) {
        return formData.owner.dependentDetails;
      }

      // Priority 2: backend data (edit / previous)
      if (vendorDependentDetails.length > 0) {
        return vendorDependentDetails.map((dep) => ({
          dependentName: dep?.name || "",
          dependentDateBirth: formatToInputDate(dep?.dob || ""),
          dependentGender: convertToObject(dep?.gender) || "",
        }));
      }

      // Priority 3: default empty row
      return [
        {
          dependentName: "",
          dependentDateBirth: "",
          dependentGender: "",
        },
      ];
    });


  function handleAdd() {
    const values = [...dependentDetails];
    values.push({ dependentName: "", dependentDateBirth: "", dependentGender: ""});
    setDependentDetails(values);
  }

  const validateEmail = (value) => {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com|org|in)$/;
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
    const values = [...dependentDetails];
    if (values.length != 1) {
      values.splice(index, 1);
      setDependentDetails(values);
    }
  }

  const { data: Category } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "StreetVending", [{ name: "Category" }],
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

    const errorMessages = {
      vendor: "VENDOR_AGE_ERROR",
      spouse: "SPOUSE_AGE_ERROR",
      dependent: "DEPENDENT_AGE_ERROR"
    };

    if (!(type == "dependent") && age < 18) {
      setShowToast({ error: true, label: t(errorMessages[type]) });
      return false;
    }
    else if (type == "dependent" && age < 14) {
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
      code: "TRANSGENDER",
      i18nKey: "TRANSGENDER",
      value: "Transgender"
    },
  ]

  const setSpouseDependentHandler = () => {
    setSpouseDependentChecked(!spouseDependentChecked);
  };

  const setDependentNameHandler = () => {
    setDependentNameChecked(!dependentNameChecked);
  };

  function selectvendorName(e) {
    if (/\d/.test(e.target.value)) {
      setShowToast({ error: true, label: t("SV_CITIZEN_VALIDATION_FORMAT") });
    }
    let units = {...vendorDetails};
    units.vendorName = e.target.value;
    setvendorName(e.target.value);
    setVendorDetails(units);
  }
  function selectvendorDateOfBirth(e) {
    let units = {...vendorDetails};
    units.vendorDateOfBirth = e.target.value;
    setvendorDateOfBirth(e.target.value);
    setVendorDetails(units);
    validateAge(e.target.value, "vendor");
  }
  function selectgender(value) {
    let units = {...vendorDetails};
    units.gender = value;
    setgender(value);
    setVendorDetails(units);
  }
  function selectownertype(value) {
    let units = {...vendorDetails};
    units.userCategory = value;
    setownerTypeCategory(value);
    setVendorDetails(units);
  }
  function selectfatherName(e) {
    if (/\d/.test(e.target.value)) {
      setShowToast({ error: true, label: t("SV_CITIZEN_VALIDATION_FORMAT") });
    }
    let units = {...vendorDetails};
    units.fatherName = e.target.value;
    setfatherName(e.target.value);
    setVendorDetails(units);
  }

  function selectspouseName(e) {
    if (/\d/.test(e.target.value)) {
      setShowToast({ error: true, label: t("SV_CITIZEN_VALIDATION_FORMAT") });
    }
    let units = {...spouseDetails};
    units.spouseName = e.target.value;
    setspouseName(e.target.value);
    setSpouseDetails(units);
  }

  function selectmobileNumber(e) {
    let units = {...vendorDetails};
    units.mobileNumber = e;
    setmobileNumber(e);
    setVendorDetails(units);
  }

  function selectspouseDateBirth(e) {
    let units = {...spouseDetails};
    units.spouseDateBirth = e.target.value;
    setspouseDateBirth(e.target.value);
    setSpouseDetails(units);
    validateAge(e.target.value, "spouse");
  }

  function selectdependentDateBirth(i, e) {
    let units = [...dependentDetails];
    units[i].dependentDateBirth = e.target.value;
    setdependentDateBirth(e.target.value);
    setDependentDetails(units);
    validateAge(e.target.value, "dependent");
  }

  function selectdependentName(i, e) {
    if (/\d/.test(e.target.value)) {
      setShowToast({ error: true, label: t("SV_CITIZEN_VALIDATION_FORMAT") });
    }
    let units = [...dependentDetails];
    units[i].dependentName = e.target.value;
    setdependentName(e.target.value);
    setDependentDetails(units);
  }

  function selectdependentGender(i, value) {
    let units = [...dependentDetails];
    units[i].dependentGender = value;
    setdependentGender(value);
    setDependentDetails(units);
  }

  function selectemail(e) {
    let units = {...vendorDetails};
    units.email = e.target.value;
    const value = e.target.value
    setemail(e.target.value);
    validateEmail(value);
    setVendorDetails(units);
  }

  //Custom function fo rthe payload whic we can use while goint to next

  const handleSaveasDraft=()=>{
    let vendordetails = [];
    let tenantId=Digit.ULBService.getCitizenCurrentTenant(true);
  const createVendorObject = (vendorDetails) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: vendorDetails?.vendorDateOfBirth,
    userCategory:vendorDetails?.userCategory?.code,
    emailId: vendorDetails?.email,
    fatherName: vendorDetails?.fatherName,
    gender: vendorDetails?.gender?.code.charAt(0),
    id: "",
    mobileNo: vendorDetails?.mobileNumber,
    name: vendorDetails?.vendorName,
    relationshipType: "VENDOR",
    vendorId: null
  });

  const createSpouseObject = (spouseDetails) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: spouseDetails?.spouseDateBirth,
    userCategory: spouseDetails?.userCategory?.code,
    emailId: "",
    isInvolved: spouseDetails?.spouseDependentChecked,
    fatherName: "",
    gender: "O",
    id: "",
    mobileNo: "",
    name: spouseDetails?.spouseName,
    relationshipType: "SPOUSE",
    vendorId: null
  });

  const createDependentObject = (dependent) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: dependent?.dependentDateBirth,
    userCategory: dependent?.userCategory?.code,
    emailId: "",
    isInvolved: dependentNameChecked,
    fatherName: "",
    gender: dependent?.dependentGender?.code?.charAt(0),
    id: "",
    mobileNo: "",
    name: dependent?.dependentName,
    relationshipType: "DEPENDENT",
    vendorId: null
});

  // Helper function to check if a string is empty or undefined
  const isEmpty = (str) => !str || str.trim() === '';

  // Main logic
  if (!isEmpty(vendorDetails?.vendorName)) {
    const spouseName = spouseDetails?.spouseName;
    const dependentName = dependentDetails?.[0]?.dependentName;

    if (isEmpty(spouseName) && isEmpty(dependentName)) {
      // Case 1: Only vendor exists
      vendordetails = [createVendorObject(vendorDetails)];
    } else if (!isEmpty(spouseName) && isEmpty(dependentName)) {
      // Case 2: Both vendor and spouse exist
      vendordetails = [
        createVendorObject(vendorDetails),
        createSpouseObject(spouseDetails)
      ];
    } else if (!isEmpty(spouseName) && dependentDetails.length > 0) {
        const validDependents = dependentDetails.filter(
          (d) => !isEmpty(d.dependentName)
        );

        const dependentPayload = validDependents.map((dep) =>
          createDependentObject(dep)
        );

        vendordetails = [
          createVendorObject(vendorDetails),
          createSpouseObject(spouseDetails),
          ...dependentPayload
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
      benificiaryOfSocialSchemes: [],
      applicationCreatedBy: pathname.includes("citizen") ? "citizen" : "employee",
      locality: "",
          localityValue: "",
    vendingZoneValue: "",
      vendorPaymentFrequency: "", 
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
      tradeLicenseNo: vendorDetails?.[0]?.tradeNumber,
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

    if (error) {
    setShowToast({ error: true, label: error });
    return;
  }

    // Validate all applicable dates before proceeding
    const isVendorAgeValid = validateAge(vendorDetails.vendorDateOfBirth, "vendor");
    const isSpouseAgeValid = !spouseDependentChecked || validateAge(spouseDetails.spouseDateBirth, "spouse");
    const isDependentAgeValid = !dependentNameChecked || validateAge(dependentDetails[0].dependentDateBirth, "dependent");

    if (!isVendorAgeValid || !isSpouseAgeValid || !isDependentAgeValid) {
      return;
    }

    let ownerDetails = formData.owner || {};
    let ownerStep = {
      ...ownerDetails,
      vendorDetails,
      spouseDetails,
      dependentDetails,
      spouseDependentChecked,
      dependentNameChecked
    };
    onSelect(config.key, { ...formData[config.key], ...ownerStep, applicationCreatedBy: pathname.includes("citizen") ? "citizen" : "employee" }, false);
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
          isDisabled={!vendorDetails.vendorName || !vendorDetails.userCategory|| !vendorDetails.vendorDateOfBirth || !vendorDetails.gender || !vendorDetails.fatherName || (spouseDependentChecked && !spouseDetails.spouseName) || (dependentNameChecked && !dependentDetails[0].dependentName)}
        >
          <div>
             
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

                <TextInput
                  style={{ background: "#FAFAFA" }}
                  type={"text"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="vendorName"
                  value={vendorDetails?.vendorName}
                  onChange={(e) => selectvendorName(e)}
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
                  name={`userCategory`}
                  defaultValue={userCategory}
                  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                  render={(props) => (
                    <Dropdown
                      className="form-field"
                      style={{width:"100%"}}
                      selected={vendorDetails?.userCategory}
                      select={(e) => selectownertype(e)}
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
                  value={vendorDetails?.mobileNumber}
                  name="mobileNumber"
                  onChange={(e) => selectmobileNumber(e)}
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
                  value={vendorDetails?.email}
                  onChange={(e) => selectemail(e)}
                  disable={false}
                />
                {error && <p style={{ color: "red", fontSize: "12px" }}>{error}</p>}

                <CardLabel>{`${t("SV_DATE_OF_BIRTH")}`} <span className="astericColor">*</span></CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  t={t}
                  type={"date"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="vendorDateOfBirth"
                  value={vendorDetails?.vendorDateOfBirth}
                  onChange={(e) => selectvendorDateOfBirth(e)}
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
                  name={`gender`}
                  value={vendorDetails?.gender}
                  selectedOption={vendorDetails?.gender}
                  onSelect={(e) => selectgender(e)}
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
                  value={vendorDetails?.fatherName}
                  onChange={(e) => selectfatherName(e)}
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
                      value={spouseDetails?.spouseName}
                      onChange={(e) => selectspouseName(e)}
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
                    value={spouseDetails?.spouseDateBirth}
                    onChange={(e) => selectspouseDateBirth(e)}
                    disable={false}
                    max={new Date().toISOString().split('T')[0]}
                    rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                    }}
                  />
                </div>

                {/* <div style={{ border: "1px solid black", borderRadius: "3px", padding: "5px" }}> */}
                {dependentDetails.map((field, index) => {
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
                    width: inputStyles,
                  }}
                >
                  <div>
                    <div style={{ display: "flex", gap: "22px" }}>
                      <CardLabel>{`${t("SV_DEPENDENT_NAME")}`} {dependentNameChecked && <span className="astericColor">*</span>}</CardLabel>
                      {index === 0 && (
                        <CheckBox
                          label={t("SV_INVOLVED_IN_VENDING")}
                          checked={dependentNameChecked}
                          onChange={() => setDependentNameChecked(!dependentNameChecked)}
                        />
                      )}
                    </div>
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
                            fill={!(dependentDetails.length == 1) ? "#494848" : "#FAFAFA"}
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
                      t={t}
                      type={"text"}
                      isMandatory={false}
                      optionKey="i18nKey"
                      name="dependentName"
                      value={dependentDetails?.[index]?.dependentName}
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
                    value={dependentDetails?.[index]?.dependentDateBirth}
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
                    value={dependentDetails?.[index]?.dependentGender}
                    selectedOption={dependentDetails?.[index]?.dependentGender}
                    onSelect={(e) => selectdependentGender(index, e)}
                    labelKey="i18nKey"
                    isPTFlow={true}
                  />

                </div>

            </div>
          );
        })}

        {dependentDetails.length<4 &&
                <div className="astericColor" style={{ display: "flex", paddingBottom: "15px", color: "#FF8C00", marginTop: "10px" }}>
                  <button type="button" style={{ paddingTop: "10px" }} onClick={() => handleAdd()}>
                    {`${t("SV_ADD_DEPENDENT")}`}
                  </button>
                </div>
        }
              
              <br/>

        </div>


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
