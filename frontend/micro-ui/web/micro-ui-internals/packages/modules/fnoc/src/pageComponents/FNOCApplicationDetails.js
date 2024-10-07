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

  import React, { useEffect, Fragment, useState } from "react";
  import { FormStep, TextInput, CardLabel, MobileNumber, TextArea, Dropdown, CardHeader } from "@nudmcdgnpm/digit-ui-react-components";
  import { calculateCurrentFinancialYear } from "../utils";
  const FNOCApplicationDetails = ({ t, config, onSelect, userType, formData }) => {
    console.log("formData",formData);
  let index = window.location.href.charAt(window.location.href.length - 1);
  let financialYear = calculateCurrentFinancialYear();
  let tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const user = Digit.UserService.getUser().info;
  const [applicantName, setName] = useState(
  (formData.application && formData.application[index] && formData.application[index].applicantName) || formData?.application?.applicantName || ""
  );
  const [applicantNames, setNames] = useState(
  (formData.application && formData.application[index] && formData.application[index].applicantNames) || formData?.application?.applicantNames || ""
  );
  const [applicantNamess, setNamess] = useState(
  (formData.application && formData.application[index] && formData.application[index].applicantNamess) ||
  formData?.application?.applicantNamess ||
  ""
  );
  const [applicantName1, setName1] = useState(
  (formData.application && formData.application[index] && formData.application[index].applicantName1) || formData?.application?.applicantName1 || ""
  );
  const [emailId, setEmail] = useState(
  (formData.application && formData.application[index] && formData.application[index].emailId) || formData?.application?.emailId || ""
  );
  const [emailIds, setEmails] = useState(
  (formData.application && formData.application[index] && formData.application[index].emailIds) || formData?.application?.emailIds || ""
  );
  const [mobileNumber, setMobileNumber] = useState(
  (formData.application && formData.application[index] && formData.application[index].mobileNumber) ||
  formData?.application?.mobileNumber ||
  user?.mobileNumber
  );

  const [pan, setpan] = useState(
  (formData.application && formData.application[index] && formData.application[index].pan) || formData?.application?.pan || ""
  );
  const [alternateNumber, setAltMobileNumber] = useState(
  (formData.application && formData.application[index] && formData.application[index].alternateNumber) ||
  formData?.application?.alternateNumber ||
  ""
  );
  const [birthDate, setBirthDate] = useState(
  (formData.application && formData.application[index] && formData.application[index].alternateNumber) ||
  formData?.application?.alternateNumber ||
  ""
  );

  const [alternateNumbers, setAltMobileNumbers] = useState(
  (formData.application && formData.application[index] && formData.application[index].alternateNumbers) ||
  formData?.application?.alternateNumbers ||
  ""
  );
  const [alternateNumberss, setAltMobileNumberss] = useState(
  (formData.application && formData.application[index] && formData.application[index].alternateNumbers) ||
  formData?.application?.alternateNumbers ||
  ""
  );
  const [specialCategory, setspecialcategory] = useState(
  (formData.application && formData.application[index] && formData.application[index].specialCategory) ||
  formData?.application?.specialCategory ||
  ""
  );
  const [special, setspecial] = useState(
  (formData.application && formData.application[index] && formData.application[index].special) || formData?.application?.special || ""
  );
  const [relation, setRelation] = useState(
  (formData.application && formData.application[index] && formData.application[index].relation) || formData?.application?.relation || ""
  );
  const [applicantCategory, setapplicantCategory] = useState(
  (formData.application && formData.application[index] && formData.application[index].applicantCategory) ||
  formData?.application?.applicantCategory ||
  ""
  );
  const [genderr, setgender] = useState(
  (formData.application && formData.application[index] && formData.application[index].genderr) || formData?.application?.genderr || ""
  );
  
  console.log("specialllllll",specialCategory, special);

  function setOwnerName(e) {
  const input = e.target.value.replace(/[^a-zA-Z\s]/g, "");
  setName(input);
  }
  function setOwnerNames(e) {
  const input = e.target.value.replace(/[^a-zA-Z\s]/g, "");
  setNames(input);
  }
  function setOwnerNamess(e) {
  const input = e.target.value.replace(/[^a-zA-Z\s]/g, "");
  setNamess(input);
  }
  function setOwnerName1(e) {
  const input = e.target.value.replace(/[^a-zA-Z\s]/g, "");
  setName1(input);
  }
  function setOwnerEmail(e) {
  setEmail(e.target.value);
  }
  function setOwnerEmails(e) {
  setEmails(e.target.value);
  }

  function setMobileNo(e) {
  setMobileNumber(e.target.value);
  }

  function setAltMobileNo(e) {
  setAltMobileNumber(e.target.value);
  }
  function setAltMobileNos(e) {
  setAltMobileNumbers(e.target.value);
  }
  function setAltMobileNoss(e) {
  setAltMobileNumberss(e.target.value);
  }
  function setPanCard(e) {
  setpan(e.target.value);
  }

  let upComingDetails = {}; 
  if (formData?.buildings?.units?.[0]?.noOfFloors &&
    formData?.buildings?.units?.[0]?.noOfBasements &&
    formData?.buildings?.units?.[0]?.heightOfBuilding &&
    formData?.buildings?.units?.[0]?.builtArea &&
    formData?.buildings?.units?.[0]?.plotSize){
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

 
  

  const goNext = async () => {
  let owner = formData.application && formData.application[index];
  let ownerStep;

  if (userType === "citizen") {
  ownerStep = {
  ...owner,
  applicantName,
  mobileNumber,
  alternateNumber,
  emailId,
  applicantNames,
  applicantNamess,
  applicantName1,
  emailIds,
  pan,
  alternateNumbers,
  specialCategory,
  special,
  relation,
  applicantCategory,
  genderr,
  };
  onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
  } else {
  ownerStep = {
  ...owner,
  applicantName,
  mobileNumber,
  alternateNumber,
  emailId,
  applicantNames,
  applicantNamess,
  applicantName1,
  emailIds,
  pan,
  alternateNumbers,
  specialCategory,
  special,
  relation,
  applicantCategory,
  genderr,
  };
  onSelect(config.key, ownerStep, false, index);
  }

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
                ...upComingDetails
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
              documents: []
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
  try {
    const response = await Digit.FNOCService.create(payload);
    console.log("API Response:", response);
    onSelect(config.key, { ...formData[config.key], ...propertyStep }, false);
  } catch (error) {
    console.error("Error calling FNOCService.create:", error);
  }


  };

  const onSkip = () => onSelect();

  const ABmenu = [
  {
  i18nKey: "Freedom Fighter",
  code: "Freedom Fighter",
  },
  {
  i18nKey: "Widow",
  code: "Widow",
  },
  {
  i18nKey: "Adoption",
  code: "Adoption",
  },
  {
  i18nKey: "Handicapped Person",
  code: "Handicapped Person",
  },
  {
  i18nKey: "Below Poverty Line",
  code: "Below Poverty Line",
  },
  {
  i18nKey: "Defense Personnel",
  code: "Defense Personnel",
  },
  {
  i18nKey: "None",
  code: "None",
  },
  ];
  const relationship = [
  {
  i18nKey: "Father",
  code: "FATHER",
  },
  {
  i18nKey: "Husband",
  code: "HUSBAND",
  },
  ];
  const gender = [
  {
  i18nKey: "Male",
  code: "Male",
  },
  {
  i18nKey: "Female",
  code: "Female",
  },
  {
  i18nKey: "Transgender",
  code: "Transgender",
  },
  ];
  const MASTERS_APPLICANTTYPE_LABEL = [
  {
  id: "1",
  i18nKey: "Individual",
  code: "Individual",
  },
  {
  id: "2",
  i18nKey: "Institutional-Government",
  code: "Institutional-Government",
  },
  {
  id: "3",
  i18nKey: "Institutional-Private",
  code: "Institutional-Private",
  },
  ];
  const MASTERS_APPLICANTTYPE = [
  {
  id: "1",
  i18nKey: "Single Owner",
  code: "Single Owner",
  },
  {
  id: "1",
  i18nKey: "Multiple Owner",
  code: "Multiple Owner",
  },
  {
  id: "2",
  i18nKey: "Central Government",
  code: "Central Government",
  },
  {
  id: "2",
  i18nKey: "Others - Government Institution",
  code: "Others - Government Institution",
  },
  {
  id: "2",
  i18nKey: "State Government",
  code: "State Government",
  },
  {
  id: "3",
  i18nKey: "NGO",
  code: "NGO",
  },
  {
  id: "3",
  i18nKey: "Others - Private Institution",
  code: "Others - Private Institution",
  },
  ];
  const FnocData = MASTERS_APPLICANTTYPE.map((data) => {
  if (specialCategory.id === data.id) {
  return {
  i18nKey: data.i18nKey,
  code: data.code,
  };
  }
  }).filter((item) => item !== undefined);

  useEffect(() => {
  if (userType === "citizen") {
  goNext();
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

  {specialCategory.code === "Individual" && (
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
  // isRequired: true,
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
  // isRequired: true,
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
  placeholder="Enter Father's Name"
  // value={""}
  // onChange={""}
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

  {(specialCategory.code === "Institutional-Government" || specialCategory.code === "Institutional-Private") && (
  <>
  <CardLabel>
  {`${t("NOC_INSTITUTION_LABEL")}`} <span className="check-page-link-button">*</span>
  </CardLabel>
  <TextInput
  t={t}
  type={"text"}
  isMandatory={false}
  optionKey="i18nKey"
  name="authorizedPersonName"
  placeholder={"Enter Authorized Person's Name"}
  value={applicantNames}
  style={{ width: "87%" }}
  onChange={setOwnerNames}
  // ValidationRequired={true}
  // {...(validation = {
  // pattern: "^[a-zA-Z ]+$",
  // type: "tel",
  // title: t("CHB_NAME_ERROR_MESSAGE"),
  // })}
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
  value={applicantNamess}
  style={{ width: "87%" }}
  onChange={setOwnerNamess}
  // ValidationRequired={true}
  // {...(validation = {
  // pattern: "^[a-zA-Z ]+$",
  // type: "tel",
  // title: t("CHB_NAME_ERROR_MESSAGE"),
  // })}
  />
  <CardLabel>{t("FN_ALT_MOBILE_NUMBER")}</CardLabel>
  <MobileNumber
  value={alternateNumbers}
  name="alternateNumber"
  style={{ width: "87%" }}
  placeholder="Enter Alternate Mobile Number"
  onChange={(value) => setAltMobileNos({ target: { value } })}
  validation={{
  required: false,
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
  value={applicantName1}
  style={{ width: "87%" }}
  onChange={setOwnerName1}
  // ValidationRequired={true}
  // {...(validation = {
  // pattern: "^[a-zA-Z ]+$",
  // type: "tel",
  // title: t("CHB_NAME_ERROR_MESSAGE"),
  // })}
  />
  <CardLabel>{t("FN_ALT_MOBILE_NUMBER")}</CardLabel>
  <MobileNumber
  value={alternateNumberss}
  name="alternateNumber"
  style={{ width: "87%" }}
  placeholder="Enter Alternate Mobile Number"
  onChange={(value) => setAltMobileNoss({ target: { value } })}
  validation={{
  required: false,
  pattern: "[6-9]{1}[0-9]{9}",
  type: "tel",
  title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
  }}
  />
  <CardLabel>
  {t("NOC_AUTHORIZED_PERSON_EMAIL_LABEL")} <span className="check-page-link-button">*</span>
  </CardLabel>
  <TextInput
  t={t}
  type="email"
  isMandatory={false}
  style={{ width: "87%" }}
  optionKey="i18nKey"
  name="emailId"
  value={emailIds}
  placeholder="Enter Applicant Email Id"
  onChange={setOwnerEmails}
  // validation={{
  // required: true,
  // pattern: "[A-Za-z0-9._%+-]+@[a-z0-9.-]+.[a-z]{2,4}$",
  // type: "email",
  // title: t("CHB_NAME_ERROR_MESSAGE"),
  // }}
  />
  <CardLabel>
  {`${t("NOC_OFFICIAL_CORRESPONDENCE_ADDRESS_LABEL")}`} <span className="check-page-link-button">*</span>
  </CardLabel>
  <TextArea
  t={t}
  type={"textarea"}
  isMandatory={false}
  optionKey="i18nKey"
  name="address"
  placeholder={"Enter Address "}
  style={{ width: "55%" }}
  // ValidationRequired={false}
  // {...(validation = {
  // isRequired: true,
  // pattern: "^[a-zA-Z ]+$",
  // type: "textarea",
  // title: t("PURPOSE_DESCRIPTION_ERROR_MESSAGE"),
  // })}
  />
  </>
  )}
  </div>
  </FormStep>
  </React.Fragment>
  );
  };

  export default FNOCApplicationDetails;