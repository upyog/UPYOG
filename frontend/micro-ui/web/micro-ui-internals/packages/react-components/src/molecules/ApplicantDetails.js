/**
 * @author - Shivank - NIUA 
 * This component is the Common Component used for Applicant Details in both Citizen and Employee Side.
 * Key of this Component is "owner", use this in your config file inside Key
 * 
 * This Component have some additional Field such as Gender, Gaurdian as well as relatioshipType which will render 
 * according to the flag if true then render otherwise not.
 * 
 * To Set the Flag you need to make an object in your Config or you can dirctly copy and paste this object in your Config - 
 * 
 *              "additionaFields":{
                    "gender":true,
                    "dateofBirth":false,
                    "guardianName":false,
                    "relationShipType":false,
                }
 *  
 * TODO:- 1. Need to Check how the Timeline will get used here for all the Modules.
 *        2. What inCase of Edit, Renew & Draft, how to pass data here using Props.  
 */



import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, MobileNumber, RadioButtons, Dropdown } from "@upyog/digit-ui-react-components";

const ApplicantDetails = ({ t, config, onSelect, formData }) => {
  const user = Digit.UserService.getUser().info;
  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "86%" };
  let validation = {};
  const [applicantName, setName] = useState((user.type === "EMPLOYEE" ?"":user?.name) || formData?.owner?.applicantName || formData?.infodetails?.existingDataSet?.owner?.applicantName || "");
  const [mobileNumber, setMobileNumber] = useState((user.type === "EMPLOYEE" ?"":user?.mobileNumber) ||formData?.owner?.mobileNumber || formData?.infodetails?.existingDataSet?.owner?.mobileNumber || "");
  const [gender, setGender]=useState(formData?.owner?.gender||"");
  const [dateOfBirth, setDateofBirth] = useState(formData?.owner?.dateOfBirth|| formData?.owner?.requestDetails?.applicantName || "");
  const [emailId, setEmail] = useState((user.type === "EMPLOYEE" ?"":user?.emailId)||formData?.owner?.emailId ||  formData?.infodetails?.existingDataSet?.owner?.emailId || "");
  const [alternateNumber, setAltMobileNumber] = useState(formData?.owner?.alternateNumber ||  formData?.infodetails?.existingDataSet?.owner?.alternateNumber || "");
  const [guardianName, setGuardian] = useState(formData?.owner?.guardianName || "");
  const [relationShipType, setRelationShipType] = useState(formData?.owner?.relationShipType || "");


  function setOwnerName(e) {
    setName(e.target.value);
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
  function setGuardiansName(e) {
    setGuardian(e.target.value);
  }

  function setBirthDate(e){
    setDateofBirth(e.target.value);
  }
  
  const { data: applicantGender } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "common-masters", [{ name: "GenderType" }],
    {
      select: (data) => {
        const formattedData = data?.["common-masters"]?.["GenderType"]
        return formattedData;
      },
    });

    let genderOptions = [];  

    applicantGender &&
      applicantGender.map((genderoption) => {
        if (genderoption.code !== "TRANSGENDER")
          genderOptions.push({ i18nKey: `${genderoption.code}`, code: `${genderoption.code}`, name: `${genderoption.code}` });
      });


  const GuardianOptions = [
    { name: "Husband", code: "HUSBAND", i18nKey: "COMMON_HUSBAND" },
    { name: "Father", code: "FATHER", i18nKey: "COMMON_FATHER" },
  ];
  
  const goNext = () => {
    let owner = formData.owner;
    let applicantDetails = { ...owner, applicantName, mobileNumber, gender,dateOfBirth,alternateNumber,relationShipType, guardianName, emailId};
    onSelect(config.key, applicantDetails, false);
  };

  return (
    <React.Fragment>
    <FormStep
      config={config}
      onSelect={goNext}
      t={t}
      isDisabled={!applicantName || 
        !mobileNumber || 
        (config?.additionaFields?.guardianName)?!guardianName:null  || 
        (config?.additionaFields?.dateofBirth) ? !dateOfBirth:null  || 
        (config?.additionaFields?.gender) ? !gender:null}
    >
      <div>
        <CardLabel>{`${t("COMMON_APPLICANT_NAME")}`} <span className="astericColor">*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="applicantName"
          value={applicantName}
          style={inputStyles}
          onChange={setOwnerName}
          ValidationRequired = {true}
          {...(validation = {
            isRequired: true,
            pattern: "^[a-zA-Z ]+$",
            type: "tel",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
        />
        {(config?.additionaFields?.gender) ? 
        <React.Fragment>       
        <CardLabel>{`${t("COMMON_GENDER")}`} <span className="astericColor">*</span></CardLabel>
        <RadioButtons
            t={t}
            options={genderOptions}
            style={{ display: "flex", flexWrap: "wrap", maxHeight: "30px" }}
            innerStyles={{ minWidth: "24%" }}
            optionsKey="i18nKey"
            name={`gender`}
            value={gender}
            selectedOption={gender}
            onSelect={setGender}
            labelKey="i18nKey"
            isPTFlow={true}
        />
        </React.Fragment>:null}
      
        <CardLabel>{`${t("COMMON_MOBILE_NUMBER")}`} <span className="astericColor">*</span></CardLabel>
        <MobileNumber
          value={mobileNumber}
          name="mobileNumber"
          onChange={(value) => setMobileNo({ target: { value } })}
          style={ {width: user.type === "EMPLOYEE" ? "49%" : "86%"}}
          {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
        />

        <CardLabel>{`${t("COMMON_ALT_MOBILE_NUMBER")}`}</CardLabel>
          <MobileNumber
            value={alternateNumber}
            name="alternateNumber"
            onChange={(value) => setAltMobileNo({ target: { value } })}
            style={ {width: user.type === "EMPLOYEE" ? "49%" : "86%"}}
            {...{ required: false, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
          />
        {(config?.additionaFields?.dateofBirth)?
        <React.Fragment>
        <CardLabel>{`${t("COMMON_BIRTH_DATE")}`} <span className="astericColor">*</span></CardLabel>
        <TextInput
            t={t}
            type={"date"}
            isMandatory={false}
            optionKey="i18nKey"
            name="dateOfBirth"
            value={dateOfBirth}
            onChange={setBirthDate}
            style={inputStyles}
            max={new Date().toISOString().split('T')[0]}
            rules={{
            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
            validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
            }}
        />
        </React.Fragment>:null}  
        
        {(config?.additionaFields?.guardianName)?
        <React.Fragment>
        <CardLabel>{`${t("COMMON_GUARDIAN")}`} <span className="astericColor">*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="guardianName"
          style={inputStyles}
          value={guardianName}
          onChange={setGuardiansName}
          ValidationRequired = {true}
          {...(validation = {
            isRequired: true,
            pattern: "^[a-zA-Z ]+$",
            type: "tel",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
        />
        </React.Fragment>:null}
      
      {(config?.additionaFields?.guardianName)?
      <React.Fragment>
        <CardLabel>{`${t("COMMON_RELATIONTYPE")}`} <span className="astericColor">*</span></CardLabel>
        <Dropdown
        className="form-field"
        selected={relationShipType}
        option={GuardianOptions}
        select={setRelationShipType}
        optionKey="i18nKey"
        t={t}
        name="relationShipType"
        placeholder={"Select"}
        />
      </React.Fragment>:null }
        
      <CardLabel>{`${t("COMMON_EMAIL_ID")}`} <span className="astericColor">*</span></CardLabel>
      <TextInput
        t={t}
        type={"email"}
        isMandatory={true}
        optionKey="i18nKey"
        name="emailId"
        value={emailId}
        style={inputStyles}
        onChange={setOwnerEmail}
        ValidationRequired={true}
        {...(validation = {
          isRequired: true,
          pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z]+\\.[a-zA-Z]{3,4}$",
          type: "email",
          title: t("PT_NAME_ERROR_MESSAGE"),
        })}
      />
        
      </div>
    </FormStep>
    </React.Fragment>
  );
};

export default ApplicantDetails;