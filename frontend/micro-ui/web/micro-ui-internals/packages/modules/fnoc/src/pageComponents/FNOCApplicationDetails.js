  // const institutionInformation = () => {
  // return getCommonGrayCard({
  // header: getCommonSubHeader(
  // {
  // labelName: "Owner Information",
  // labelKey: "NOC_OWNER_INFO_TITLE"
  // },
  // {
  // style: {
  // marginBottom: 18
  // }
  // }
  // ),
  // applicantCard: getCommonContainer({
  // institutionName: getTextField({
  // label: {
  // labelName: "Name of Institution",
  // labelKey: "NOC_INSTITUTION_LABEL"
  // },
  // placeholder: {
  // labelName: "Enter Name of Institution",
  // labelKey: "NOC_ENTER_INSTITUTION_PLACEHOLDER"
  // },
  // pattern: getPattern("Name"),
  // errorMessage: "Invalid Name",
  // required: true,
  // jsonPath:
  // "BPA.landInfo.additionalDetail.institutionName",
  // gridDefination: {
  // xs: 12,
  // sm: 12,
  // md: 6
  // }
  // }),
  // telephoneNumber: getTextField({
  // label: {
  // labelName: "Official Telephone No.",
  // labelKey: "NOC_TELEPHONE_NUMBER_LABEL"
  // },
  // placeholder: {
  // labelName: "Enter Official Telephone No.",
  // labelKey: "NOC_ENTER_TELEPHONE_NUMBER_PLACEHOLDER"
  // },
  // required: true,
  // pattern: getPattern("MobileNo"),
  // errorMessage: "Invalid Number",
  // jsonPath:
  // "BPA.landInfo.additionalDetail.telephoneNumber",
  // gridDefination: {
  // xs: 12,
  // sm: 12,
  // md: 6
  // }
  // }),
  // authorisedPerson: getTextField({
  // label: {
  // labelName: "Name of Authorized Person",
  // labelKey: "NOC_AUTHORIZED_PERSON_LABEL"
  // },
  // placeholder: {
  // labelName: "Enter Name of Authorized Person",
  // labelKey: "NOC_ENTER_AUTHORIZED_PERSON_PLACEHOLDER"
  // },
  // required: true,
  // pattern: getPattern("Name"),
  // errorMessage: "Invalid Name",
  // jsonPath: "BPA.landInfo.owners[0].name",
  // gridDefination: {
  // xs: 12,
  // sm: 12,
  // md: 6
  // }
  // }),
  // designation: getTextField({
  // label: {
  // labelName: "Designation in Institution",
  // labelKey: "NOC_INSTITUTION_DESIGNATION_LABEL"
  // },
  // placeholder: {
  // labelName: "Enter designation of Institution",
  // labelKey: "NOC_ENTER_INSTITUTION_DESIGNATION_PLACEHOLDER"
  // },
  // required: true,
  // pattern: getPattern("Name"),
  // errorMessage: "Invalid Designation Name",
  // jsonPath:
  // "BPA.landInfo.additionalDetail.institutionDesignation",
  // gridDefination: {
  // xs: 12,
  // sm: 12,
  // md: 6
  // }
  // }),
  // authorizedPersonMobile: getTextField({
  // label: {
  // labelName: "Mobile No. of Authorized Person",
  // labelKey: "NOC_AUTHORIZED_PERSON_MOBILE_LABEL"
  // },
  // placeholder: {
  // labelName: "Enter Mobile No. of Authorized Person",
  // labelKey: "NOC_AUTHORIZED_PERSON_MOBILE_PLACEHOLDER"
  // },
  // required: true,
  // pattern: getPattern("MobileNo"),
  // errorMessage: "Invalid MobileNo.",

  // jsonPath: "BPA.landInfo.owners[0].mobileNumber",
  // gridDefination: {
  // xs: 12,
  // sm: 12,
  // md: 6
  // }
  // }),
  // authorizedPersonEmail: getTextField({
  // label: {
  // labelName: "Email of Authorized Person",
  // labelKey: "NOC_AUTHORIZED_PERSON_EMAIL_LABEL"
  // },
  // placeholder: {
  // labelName: "Enter Email of Authorized Person",
  // labelKey: "NOC_AUTHORIZED_PERSON_EMAIL_PLACEHOLDER"
  // },
  // pattern: getPattern("Email"),
  // errorMessage: "Invalid Email",
  // required: true,
  // jsonPath: "BPA.landInfo.owners[0].emailId",
  // gridDefination: {
  // xs: 12,
  // sm: 12,
  // md: 6
  // }
  // }),
  // officialCorrespondenceAddress: getTextField({
  // label: {
  // labelName: "Official Correspondence Address",
  // labelKey: "NOC_OFFICIAL_CORRESPONDENCE_ADDRESS_LABEL"
  // },
  // placeholder: {
  // labelName: "Enter Official Correspondence Address ",
  // labelKey: "NOC_ENTER_OFFICIAL_CORRESPONDENCE_ADDRESS_PLACEHOLDER"
  // },
  // required: true,
  // pattern: getPattern("Address"),
  // errorMessage: "Invalid Address",
  // jsonPath:
  // "BPA.landInfo.owners[0].correspondenceAddress",
  // gridDefination: {
  // xs: 12,
  // sm: 12,
  // md: 6
  // }
  // })
  // })
  // });
  // };

  // The institutionInformation function is a React component that returns a form card for collecting owner information, including fields for the institution's name, contact details, authorized person, and their correspondence address, all with validation and responsive design.

  // This React component renders a form for capturing applicant information, including dropdowns for applicant type and subtype, input fields for personal details like name, gender, mobile number, alternate mobile number, email, birth date, and PAN card, with validation for each field and conditional rendering based on the selected applicant type


  //This code imports necessary React components and utility functions, initializes a functional component (FNOCApplicationDetails), and retrieves form data, the current financial year, and user information for rendering the application's details.
  import React, { useEffect, Fragment, useState } from "react";
  import { FormStep, TextInput, CardLabel, MobileNumber, TextArea, Dropdown, CardHeader } from "@nudmcdgnpm/digit-ui-react-components";
  import { calculateCurrentFinancialYear } from "../utils";
  const FNOCApplicationDetails = ({ t, config, onSelect, userType, formData }) => {
    console.log("formData",formData);
  let index = window.location.href.charAt(window.location.href.length - 1);
  let financialYear = calculateCurrentFinancialYear();
  let tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const user = Digit.UserService.getUser().info;
    // Setting state variables based on formData or defaults
  const [applicantName, setName] = useState(
  formData?.application?.applicantName || "");

  const [authorizedPerson, setAuthorizedPerson] = useState(
  formData?.application?.authorizedPerson ||"");

  const [institutionaDesignation, setInstitutionaDesignation] = useState(
  formData?.application?.institutionaDesignation || "");

  const [emailId, setEmail] = useState(
  formData?.application?.emailId || "");
 
  const [mobileNumber, setMobileNumber] = useState(
  formData?.application?.mobileNumber ||user?.mobileNumber);

  const [pan, setpan] = useState(
   formData?.application?.pan || "");

  const [alternateNumber, setAltMobileNumber] = useState(
  formData?.application?.alternateNumber ||"");

  const [birthDate, setBirthDate] = useState(formData?.application?.birthDate ||"");
 
  const [specialCategory, setspecialcategory] = useState(formData?.application?.specialCategory ||"");

  const [special, setspecial] = useState(formData?.application?.special || "");

  const [relation, setRelation] = useState(formData?.application?.relation || "");

  const [applicantCategory, setapplicantCategory] = useState(formData?.application?.applicantCategory ||"");

  const [genderr, setgender] = useState( formData?.application?.genderr || "");
  
  console.log("specialllllll",specialCategory, special);

  // These functions handle user input for various fields in a form, sanitizing names to allow only alphabetic characters and spaces, while directly updating state for email and mobile number inputs.
  function setOwnerName(e) {
  const input = e.target.value.replace(/[^a-zA-Z\s]/g, "");
  setName(input);
  }
  
  function setauthorizedPerson(e) {
  const input = e.target.value.replace(/[^a-zA-Z\s]/g, "");
  setAuthorizedPerson(input);
  }
  function setinstitutionaDesignation(e) {
  const input = e.target.value.replace(/[^a-zA-Z\s]/g, "");
  setInstitutionaDesignation(input);
  }
  function setOwnerEmail(e) {
  setEmail(e.target.value);
  }
  

  function setMobileNo(e) {
  setMobileNumber(e.target.value);
  }

  function setAltMobileNo(e) {
  setAltMobileNumber(e.target.value);
  }
 

  function setPanCard(e) {
  setpan(e.target.value);
  }

  let upComingDetails = {}; 
  // Check if all specified properties are present
  if (formData?.buildings?.units?.[0]?.noOfFloors &&
    formData?.buildings?.units?.[0]?.noOfBasements &&
    formData?.buildings?.units?.[0]?.heightOfBuilding &&
    formData?.buildings?.units?.[0]?.builtArea &&
    formData?.buildings?.units?.[0]?.plotSize){
        // Set all details if all properties exist
    upComingDetails={
      NO_OF_FLOORS: formData?.buildings?.units?.[0]?.noOfFloors?.code,
      NO_OF_BASEMENTS: formData?.buildings?.units?.[0]?.noOfBasements?.code,
      HEIGHT_OF_BUILDING: formData?.buildings?.units?.[0]?.heightOfBuilding,
      BUILTUP_AREA: formData?.buildings?.units?.[0]?.builtArea,
      PLOT_size:formData?.buildings?.units?.[0]?.plotSize
    };
  }
  else if (formData?.buildings?.units?.[0]?.noOfFloors && 
          formData?.buildings?.units?.[0]?.noOfBasements &&
          formData?.buildings?.units?.[0]?.heightOfBuilding)
          {
            upComingDetails={
              NO_OF_FLOORS: formData?.buildings?.units?.[0]?.noOfFloors?.code,
              NO_OF_BASEMENTS: formData?.buildings?.units?.[0]?.noOfBasements?.code,
              HEIGHT_OF_BUILDING: formData?.buildings?.units?.[0]?.heightOfBuilding,
            };
          }
  else if (formData?.buildings?.units?.[0]?.noOfFloors && 
           formData?.buildings?.units?.[0]?.noOfBasements){
            upComingDetails={
              NO_OF_FLOORS: formData?.buildings?.units?.[0]?.noOfFloors?.code,
              NO_OF_BASEMENTS: formData?.buildings?.units?.[0]?.noOfBasements?.code};
           }

 
  
  // Function to handle the next step in the form
  const goNext = async () => {
  let owner = formData.application && formData.application[index]; // Getting the owner details
  let ownerStep;
      // If user type is citizen, update the ownerStep with details
  if (userType === "citizen") {
  ownerStep = {
  ...owner,
  applicantName,
  mobileNumber,
  alternateNumber,
  emailId,
  authorizedPerson,
  institutionaDesignation,
  pan,
  specialCategory,
  special,
  relation,
  applicantCategory,
  genderr,
  birthDate
  };
  onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index); // Call onSelect with updated form data
  } else {
          // If user type is not citizen, set ownerStep differently
  ownerStep = {
  ...owner,
  applicantName,
  mobileNumber,
  alternateNumber,
  emailId,
  authorizedPerson,
  institutionaDesignation,
  pan,
  specialCategory,
  special,
  relation,
  applicantCategory,
  genderr,
  birthDate
  };
  onSelect(config.key, ownerStep, false, index);  // Call onSelect with updated ownerStep
  }
    // Preparing payload for API call
  const payload = {
    FireNOCs: [
      {
        fireNOCDetails: {
          noOfBuildings: formData?.buildings?.units?.[0]?.noOfBuildings?.code,
          fireNOCType: formData?.commonDetails?.nocType?.code,
          buildings: [
            {
              name: formData?.buildings?.units?.[0]?.name,
              usageTypeMajor: formData?.buildings?.units?.[0]?.usageTypeMajor?.code,
              usageType: formData?.buildings?.units?.[0]?.usageType?.code,
              uomsMap: {
                ...upComingDetails  // Spread operator to include upcoming details
              },
              uoms: [
                {
                  code: "HEIGHT_OF_BUILDING",
                  value: 45,
                  isActiveUom: true,
                  active: true
                },
                {
                  code: "NO_OF_FLOORS",
                  value: 1,
                  isActiveUom: false,
                  active: true
                },
                {
                  code: "NO_OF_BASEMENTS",
                  value: 0,
                  isActiveUom: false,
                  active: true
                }
              ],
              applicationDocuments: []
            }
          ],
          propertyDetails: {
            propertyId:formData?.propertyDetails?.propertyId,
            address: {
              city: tenantId,
              doorNo:formData?.propertyDetails?.doorNo,
              buildingName:formData?.propertyDetails?.buildingName,
              street:formData?.propertyDetails?.street,
              pincode:formData?.propertyDetails?.pincode,
              locality: {
                code: formData?.propertyDetails?.locality?.code
              }
            }
          },
          firestationId: formData?.propertyDetails?.firestationId?.code,
          additionalDetail: {
            documents: []
          },
          applicantDetails: {
            ownerShipMajorType: "INDIVIDUAL",
            ownerShipType: "INDIVIDUAL.SINGLEOWNER",
            owners: [
              {
                mobileNumber:  "9999009999",
                name: "Shivank",
                gender: "MALE",
                dob: 1727720999000,
                emailId: "shuklashivank28@gmail.com",
                correspondenceAddress: "test",
                fatherOrHusbandName: "test",
                relationship: "test",
                ownerType: "NONE"
              }
            ],
            additionalDetail: {
              documents: [] // Placeholder for additional documents
            }
          },
          action: "INITIATE",
          channel: "CITIZEN",
          financialYear: financialYear
        },
        tenantId: tenantId
      }
    ]
  };
   // Making API call to create FNOC
  try {
    const response = await Digit.FNOCService.create(payload);
    console.log("API Response:", response);
    onSelect(config.key, { ...formData[config.key], ...propertyStep }, false);
  } catch (error) {
    console.error("Error calling FNOCService.create:", error);
  }


  };
  // Function to skip to the next step
  const onSkip = () => onSelect();
  // These constants provide dropdown options for a form, including applicant types, special categories, relationships, and genders, each with a key for localization.
  const ABmenu = [
  {
  i18nKey: "Freedom Fighter",
  code: "FREEDOM_FIGHTER",
  value: "Freedom Fighter",

  },
  {
  i18nKey: "Widow",
  code: "WIDOW",
  value: "Widow",

  },
  {
  i18nKey: "Adoption",
  code: "ADOPTION",
  value: "Adoption",

  },
  {
  i18nKey: "Handicapped Person",
  code: "HANDICAPPED_PERSON",
  value: "Handicapped Person",

  },
  {
  i18nKey: "Below Poverty Line",
  code: "BELOW_POVERTY_LINE",
  value: "Below Poverty Line",

  },
  {
  i18nKey: "Defense Personnel",
  code: "DEFENSE_PERSONNEL",
  value: "Defense Personnel",

  },
  {
  i18nKey: "None",
  code: "NONE",
  value: "None",

  },
  ];
  const relationship = [
  {
  i18nKey: "Father",
  code: "FATHER",
  value: "Father",

  },
  {
  i18nKey: "Husband",
  code: "HUSBAND",
  value: "Husband",

  },
  ];
  const gender = [
  {
  i18nKey: "Male",
  code: "MALE",
  value: "Male",

  },
  {
  i18nKey: "Female",
  code: "FEMALE",
  value: "Female",

  },
  {
  i18nKey: "Transgender",
  code: "TRANSGENDER",
  value: "Transgender",

  },
  ];
  const MASTERS_APPLICANTTYPE_LABEL = [
  {
  id: "1",
  i18nKey: "Individual",
  code: "INDIVIDUAL",
  value: "Individual",

  },
  {
  id: "2",
  i18nKey: "Institutional-Government",
  code: "INSTITUTIONAL-GOVERNMENT",
  value: "Institutional-Government",

  },
  {
  id: "3",
  i18nKey: "Institutional-Private",
  code: "INSTITUTIONAL-PRIVATE",
  value: "Institutional-Private",

  },
  ];
  const MASTERS_APPLICANTTYPE = [
  {
  id: "1",
  i18nKey: "Single Owner",
  code: "SINGLE_OWNER",
  value: "Single Owner",

  },
  {
  id: "1",
  i18nKey: "Multiple Owner",
  code: "MULTIPLE_OWNER",
  value: "Multiple Owner",

  },
  {
  id: "2",
  i18nKey: "Central Government",
  code: "CENTRAL_GOVERNMENT",
  value: "Central Government",

  },
  {
  id: "2",
  i18nKey: "Others - Government Institution",
  code: "Others_GOVERNMENT_INSTITUTION",
  value:"Others - Government Institution",

  },
  {
  id: "2",
  i18nKey: "State Government",
  code: "STATE_GOVERNMENT",
  value:"State Government"
  },
  {
  id: "3",
  i18nKey: "NGO",
  code: "NGO",
  value:"NGO",
  },
  {
  id: "3",
  i18nKey: "Others - Private Institution",
  code: "OTHERS_PRIVATE_INSTITUTION",
  value:"Others - Private Institution",
  },
  ];
    // Filtering applicant types based on special category
  const FnocData = MASTERS_APPLICANTTYPE.map((data) => {
  if (specialCategory.id === data.id) {
  return {
  i18nKey: data.i18nKey,
  code: data.value,
  };
  }
  }).filter((item) => item !== undefined); // Filter out undefined items

  useEffect(() => {     // Effect to trigger goNext if user type is citizen
  if (userType === "citizen") {
  goNext(); // Call goNext function
  }
  }, []);

  return (
 <React.Fragment>
<FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t}>
<div>
<CardLabel>
{`${t("FN_COMMON-MASTERS_APPLICANTTYPE_LABEL")}`} <span className="check-page-link-button">*</span>
</CardLabel>
<Dropdown
selected={specialCategory}
select={setspecialcategory}
className="form-field"
option={MASTERS_APPLICANTTYPE_LABEL}
placeholder={"COMMON-MASTERS_APPLICANTSUBTYPE_LABEL"}
optionKey="i18nKey"
t={t}
/>

<CardLabel>
{`${t("FNCOMMON-MASTERS_APPLICANTSUBTYPE_LABEL")}`} <span className="check-page-link-button">*</span>
</CardLabel>
<Dropdown
selected={special}
select={setspecial}
className="form-field"
option={FnocData}
placeholder={"MASTERS_APPLICANTTYPE"}
optionKey="i18nKey"
t={t}
/>

{/* Conditional Field for only individual to show */}
{specialCategory.value === "Individual" && (
<>
<CardLabel>
  {t("FN_APPLICANT_NAME")} <span className="check-page-link-button">*</span>
</CardLabel>
<TextInput
  t={t}
  type="text"
  isMandatory={false}
  optionKey="i18nKey"
  name="applicantName"
  placeholder="Enter Applicant Name"
  value={applicantName}
  style={{ width: "87%" }}
  onChange={setOwnerName}
  validation={{
    pattern: "^[a-zA-Z ]+$",
    type: "text",
    title: t("CHB_NAME_ERROR_MESSAGE"),
  }}
/>

<CardLabel>
  {t("FN_GENDER")} <span className="check-page-link-button">*</span>
</CardLabel>
<Dropdown
  selected={genderr}
  select={setgender}
  className="form-field"
  option={gender}
  placeholder="Select Gender"
  optionKey="i18nKey"
  t={t}
/>

<CardLabel>
  {t("FN_MOBILE_NUMBER")} <span className="check-page-link-button">*</span>
</CardLabel>
<MobileNumber
  value={mobileNumber}
  name="mobileNumber"
  style={{ width: "87%" }}
  placeholder="Enter Applicant Registered Mobile Number"
  onChange={(value) => setMobileNo({ target: { value } })}
  validation={{
    pattern: "[6-9]{1}[0-9]{9}",
    type: "tel",
    title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
  }}
/>

<CardLabel>{t("FN_ALT_MOBILE_NUMBER")}</CardLabel>
<MobileNumber
  value={alternateNumber}
  name="alternateNumber"
  style={{ width: "87%" }}
  placeholder="Enter Alternate Mobile Number"
  onChange={(value) => setAltMobileNo({ target: { value } })}
  validation={{
    required: false,
    pattern: "[6-9]{1}[0-9]{9}",
    type: "tel",
    title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
  }}
/>

<CardLabel>
  {t("FN_EMAIL_ID")} <span className="check-page-link-button">*</span>
</CardLabel>
<TextInput
  t={t}
  type="email"
  isMandatory={false}
  style={{ width: "87%" }}
  optionKey="i18nKey"
  name="emailId"
  value={emailId}
  placeholder="Enter Applicant Email Id"
  onChange={setOwnerEmail}
  validation={{
    required: true,
    pattern: "[A-Za-z0-9._%+-]+@[a-z0-9.-]+.[a-z]{2,4}$",
    type: "email",
    title: t("CHB_NAME_ERROR_MESSAGE"),
  }}
/>

<CardLabel className="card-label-smaller">
  {t("FN_BIRTH_DATE")} <span className="check-page-link-button">*</span>
</CardLabel>
<div className="field">
  <TextInput
    type="date"
    value={birthDate}
    onChange={(e) => setBirthDate(e.target.value)}
    style={{ width: "87%" }}
    max={new Date().toISOString().split("T")[0]}
  />
</div>

<CardLabel>
  {t("FN_PAN_CARD")} <span className="check-page-link-button">*</span>
</CardLabel>
<TextInput
  t={t}
  type="text"
  isMandatory={false}
  optionKey="i18nKey"
  name="panCard"
  placeholder="Enter PAN Name"
  value={pan}
  style={{ width: "87%" }}
  onChange={setPanCard}
  validation={{
    pattern: "^[a-zA-Z ]+$",
    type: "text",
    title: t("CHB_NAME_ERROR_MESSAGE"),
  }}
/>

<CardLabel>
  {t("FN_FATHER_NAME")} <span className="check-page-link-button">*</span>
</CardLabel>
<TextInput
  t={t}
  type="text"
  isMandatory={false}
  optionKey="i18nKey"
  style={{ width: "87%" }}
  name="fatherName"
  placeholder="Enter Father's Name or Husband's Name"
  validation={{
    pattern: "^[a-zA-Z ]+$",
    type: "text",
    title: t("CHB_NAME_ERROR_MESSAGE"),
  }}
/>

<CardLabel>
  {t("FN_RELATIONSHIP")} <span className="check-page-link-button">*</span>
</CardLabel>
<Dropdown
  selected={relation}
  select={setRelation}
  style={{ width: "50%" }}
  className="form-field"
  option={relationship}
  placeholder="Select Special Category"
  optionKey="i18nKey"
  t={t}
/>

<CardLabel>
  {t("FN_ADDRESS")} <span className="check-page-link-button">*</span>
</CardLabel>
<TextArea
  t={t}
  type="textarea"
  isMandatory={false}
  optionKey="i18nKey"
  name="address"
  placeholder="Enter Address"
  style={{ width: "55%" }}
  validation={{
    required: false,
  }}
/>

<CardLabel>
  {t("FN_SPECIAL_APPLICANT_CATEGORY")} <span className="check-page-link-button">*</span>
</CardLabel>
<Dropdown
  selected={applicantCategory}
  select={setapplicantCategory}
  className="form-field"
  option={ABmenu}
  placeholder="Select Special Category"
  optionKey="i18nKey"
  t={t}
/>
</>
)}

{/* Conditional Fields for Institutional Applicants */}
{(specialCategory.value === "Institutional-Government" || specialCategory.value === "Institutional-Private") && (
<>
<CardLabel>
  {`${t("NOC_INSTITUTION_LABEL")}`} <span className="check-page-link-button">*</span>
  </CardLabel>
<TextInput
  t={t}
  type="text"
  isMandatory={false}
  optionKey="i18nKey"
  name="applicantName"
  placeholder="Enter Institutional Name"
  value={applicantName}
  style={{ width: "87%" }}
  onChange={setOwnerName}
  validation={{
    pattern: "^[a-zA-Z ]+$",
    type: "text",
    title: t("CHB_NAME_ERROR_MESSAGE"),
  }}
/>


<CardLabel>
  {`${t("NOC_AUTHORIZED_PERSON_LABEL")}`} <span className="check-page-link-button">*</span>
</CardLabel>
<TextInput
  t={t}
  type={"text"}
  isMandatory={false}
  optionKey="i18nKey"
  name="authorizedPersonName"
  placeholder={"Enter Authorized Person's Name"}
  value={authorizedPerson}
  style={{ width: "87%" }}
  onChange={setauthorizedPerson}
/>

<CardLabel>{t("FN_MOBILE_NUMBER")} <span className="check-page-link-button">*</span>
</CardLabel>
<MobileNumber
  value={mobileNumber}
  name="mobileNumber"
  style={{ width: "87%" }}
  placeholder="Enter Applicant Registered Mobile Number"
  onChange={(value) => setMobileNo({ target: { value } })}
  validation={{
    pattern: "[6-9]{1}[0-9]{9}",
    type: "tel",
    title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
  }}
/>

<CardLabel>
  {`${t("NOC_INSTITUTION_DESIGNATION_LABEL")}`} <span className="check-page-link-button">*</span>
</CardLabel>
<TextInput
  t={t}
  type={"text"}
  isMandatory={false}
  optionKey="i18nKey"
  name="authorizedPersonName"
  placeholder={"Enter Authorized Person's Name"}
  value={institutionaDesignation}
  style={{ width: "87%" }}
  onChange={setinstitutionaDesignation}
/>

<CardLabel>{t("FN_ALT_MOBILE_NUMBER")}</CardLabel>
<MobileNumber
  value={alternateNumber}
  name="alternateNumber"
  style={{ width: "87%" }}
  placeholder="Enter Alternate Mobile Number"
  onChange={(value) => setAltMobileNo({ target: { value } })}
  validation={{
    required: false,
    pattern: "[6-9]{1}[0-9]{9}",
    type: "tel",
    title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
  }}
/>

<CardLabel>
  {t("FN_E")} <span className="check-page-link-button">*</span>
</CardLabel>
<TextInput
  t={t}
  type="email"
  isMandatory={false}
  style={{ width: "87%" }}
  optionKey="i18nKey"
  name="emailId"
  value={emailId}
  placeholder="Enter Applicant Email Id"
  onChange={setOwnerEmail}
  validation={{
    required: true,
    pattern: "[A-Za-z0-9._%+-]+@[a-z0-9.-]+.[a-z]{2,4}$",
    type: "email",
    title: t("CHB_NAME_ERROR_MESSAGE"),
  }}
/>

<CardLabel>
  {`${t("NOC_OFFICIAL_CORRESPONDENCE_ADDRESS_LABEL")}`} <span className="check-page-link-button">*</span>
</CardLabel>
<TextArea
  t={t}
  type="textarea"
  isMandatory={false}
  optionKey="i18nKey"
  name="address"
  placeholder="Enter Address"
  style={{ width: "55%" }}
  validation={{
    required: false,
  }}
/>
</>
)}
</div>
</FormStep>
</React.Fragment>
);

};

  export default FNOCApplicationDetails;