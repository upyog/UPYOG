import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons, LabelFieldPair, Dropdown, Menu, MobileNumber, Toast } from "@upyog/digit-ui-react-components";
import { cardBodyStyle } from "../utils";
import { useLocation, useRouteMatch } from "react-router-dom";
import Timeline from "../components/TLTimeline";

const SelectOwnerDetails = ({ t, config, onSelect, userType, formData, ownerIndex }) => {
  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const mutationScreen = url.includes("/property-mutation/");
  const getUserType = () => Digit.UserService.getType();
  const TYPE_OWNER_VALIDATE = { type: "owner_validate" };

  let index = mutationScreen ? ownerIndex : window.location.href.charAt(window.location.href.length - 1);
  let validation = {};
  const [showToast, setShowToast] = useState(null);
  const [isOtpSent, setIsOtpSent] = useState(false);
  const [showToastErrorMsg, setShowToastErrorMsg] = useState(null);
  const [otp, setOtp] = useState();
  const [isOtpValid, setIsOtpValid] = useState(true);
  const [disableSendOtp, setDisableSendOtp] = useState(true);
  const [disableValidateOtp, setDisableValidateOtp] = useState(true);
  const [name, setName] = useState((formData.owners && formData.owners[index] && formData.owners[index].name) || formData?.owners?.name || "");
  const [email, setEmail] = useState((formData.owners && formData.owners[index] && formData.owners[index].email) || formData?.owners?.emailId || "");
  const [gender, setGender] = useState((formData.owners && formData.owners[index] && formData.owners[index].gender) || formData?.owners?.gender);
  const [mobileNumber, setMobileNumber] = useState(
    (formData.owners && formData.owners[index] && formData.owners[index].mobileNumber) || formData?.owners?.mobileNumber || ""
  );
  const [fatherOrHusbandName, setFatherOrHusbandName] = useState(
    (formData.owners && formData.owners[index] && formData.owners[index].fatherOrHusbandName) || formData?.owners?.fatherOrHusbandName || ""
  );
  const [relationship, setRelationship] = useState(
    (formData.owners && formData.owners[index] && formData.owners[index].relationship) || formData?.owners?.relationship || {}
  );
  const isUpdateProperty = formData?.isUpdateProperty || false;
  let isEditProperty = formData?.isEditProperty || false;

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

  const { data: Menu } = Digit.Hooks.pt.useGenderMDMS(stateId, "common-masters", "GenderType");

  let menu = [];
  Menu &&
    Menu.map((genderDetails) => {
      menu.push({ i18nKey: `PT_COMMON_GENDER_${genderDetails.code}`, code: `${genderDetails.code}`, value: `${genderDetails.code}` });
    });

  function setOwnerName(e) {
    setName(e.target.value);
  }
  function setOwnerEmail(e) {
    setEmail(e.target.value);
  }
  function setGenderName(value) {
    setGender(value);
  }

  function setMobileNo(e) {
    setMobileNumber(e.target.value);
    setIsOtpValid(false);
    if(e.target.value && e.target.value.length==10) {
      setDisableSendOtp(false)
    } else {
      setDisableSendOtp(true)
    }
  }
  function setGuardiansName(e) {
    setFatherOrHusbandName(e.target.value);
  }
  function setGuardianName(value) {
    setRelationship(value);
  }
  function onChangeOtp(e) {
    setOtp(e.target.value);
  }

  const goNext = () => {
    let owner = formData.owners && formData.owners[index];
    let ownerStep;
    if (userType === "employee") {
      ownerStep = { ...owner, name, gender, mobileNumber, fatherOrHusbandName, relationship, emailId: email };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      if (mutationScreen) {
        ownerStep = { ...owner, name, gender, mobileNumber, fatherOrHusbandName, relationship };
        onSelect("", ownerStep);
        return;
      }
      ownerStep = { ...owner, name, gender, mobileNumber, fatherOrHusbandName, relationship };
      onSelect(config.key, ownerStep, false, index);
    }
  };
  // const validateOtp = (e) => {
    
  // }

  const validateOtp = async (e) => {
    try {
      e.preventDefault();
      
      const requestData = {
        identity: mobileNumber,
        otp: otp,
        tenantId: 'mn'
      };
      const res = await Digit.UserService.validateOtp({otp:{...requestData}});
      console.log("selectOtp==",res)
      if(res && res?.otp?.isValidationSuccessful) {
        setIsOtpValid(true);
        setShowToast({ key: true, label: "OTP validate successfully!" });
        setTimeout(() => {
          closeToast();
        }, 10000);
      } else {
        setShowToastErrorMsg({ key: true, label: "Invalid OTP" });
      setTimeout(() => {
        closeToastError();
      }, 10000);
        setIsOtpValid(false);
      }
    } catch (err) {
      setShowToastErrorMsg({ key: true, label: "Invalid OTP" });
      setTimeout(() => {
        closeToastError();
      }, 10000);
      setIsOtpValid(false);
    }
  };
  
  const sendOtp = async (e) => {
    e.preventDefault();
    console.log("sendOtp==",e)
    setIsOtpSent(false);
    setOtp("")
    if(!mobileNumber || mobileNumber.length<10) {
      setShowToastErrorMsg({ key: true, label: "Invalid Mobile No." });
      setTimeout(() => {
        closeToastError();
      }, 10000);
      return;
    }
    // const { mobileNumber } = params;
    const data = {
      mobileNumber,
      tenantId: 'mn',
      userType: getUserType(),
    };
    setDisableValidateOtp(true);
    try {
      const [res, err] = await sendOtpService({ otp: { ...data, ...TYPE_OWNER_VALIDATE } });
      console.log("TYPE_OWNER_VALIDATE--RES==",res)
      
      if(res && res?.isSuccessful) {
        setIsOtpSent(true);
        setShowToast({ key: true, label: "OTP sent successfully!" });
        setDisableSendOtp(true);
        setDisableValidateOtp(false);
        setTimeout(() => {
          closeToast();
          setDisableSendOtp(false)
        }, 10000);
        return;
      }
    } catch (err) {
    }
    
  }

  const sendOtpService = async (data) => {
    try {
      const res = await Digit.UserService.sendOtp(data, 'mn');
      return [res, null];
    } catch (err) {
      return [null, err];
    }
  };

  const closeToast = () => {
    setShowToast(null);
  };
  const closeToastError = ()=> {
    setShowToastErrorMsg(null)
  }
  

  

  const onSkip = () => onSelect();
  // As Ticket RAIN-2619 other option in gender and gaurdian will be enhance , dont uncomment it
  const options = [
    { name: "Female", value: "FEMALE", code: "FEMALE" },
    { name: "Male", value: "MALE", code: "MALE" },
    { name: "Transgender", value: "TRANSGENDER", code: "TRANSGENDER" },
    { name: "OTHERS", value: "OTHERS", code: "OTHERS" },
    // { name: "Other", value: "OTHER", code: "OTHER" },
  ];

  const GuardianOptions = [
    // { name: "HUSBAND", code: "HUSBAND", i18nKey: "PT_RELATION_HUSBAND" },
    { name: "Husband/Wife", code: "HUSBANDWIFE", i18nKey: "PT_RELATION_HUSBANDWIFE" },
    { name: "Father", code: "FATHER", i18nKey: "PT_RELATION_FATHER" },
    { name: "Mother", code: "MOTHER", i18nKey: "PT_RELATION_MOTHER" },
    { name: "Other", code: "OTHER", i18nKey: "PT_RELATION_OTHER" },
    
    // { name: "Other", code: "OTHER", i18nKey: "PT_RELATION_OTHER" },
  ];

  useEffect(() => {
    if (userType === "employee") {
      goNext();
    }
  }, [name, gender, mobileNumber, fatherOrHusbandName, relationship]);

  if (userType === "employee") {
    return (
      <div>
        <LabelFieldPair>
          <CardLabel style={editScreen ? { color: "#B1B4B6" } : {}}>{`${t("PT_FORM3_MOBILE_NUMBER")} *On change Mobile number, you need to verify the OTP every time.`}</CardLabel>
          <div className="field">
            <TextInput
              type={"text"}
              t={t}
              isMandatory={false}
              name="mobileNumber"
              value={mobileNumber}
              onChange={setMobileNo}
              ValidationRequired = {true}
              {...(validation = {
                isRequired: true,
                pattern: "[6-9]{1}[0-9]{9}",
                type: "tel",
                title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
              })}
              disable={editScreen}
            />
          </div>
        </LabelFieldPair>
        <LabelFieldPair>
          <CardLabel style={editScreen ? { color: "#B1B4B6" } : {}}>{`${t("PT_OWNER_NAME")}`}</CardLabel>
          <div className="field">
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              name="name"
              value={name}
              onChange={setOwnerName}
              ValidationRequired = {true}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "tel",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
              disable={editScreen}
            />
          </div>
        </LabelFieldPair>
        <LabelFieldPair>
          <CardLabel style={editScreen ? { color: "#B1B4B6" } : {}}>{`${t("PT_FORM3_GUARDIAN_NAME")}`}</CardLabel>
          <div className="field">
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              name="fatherOrHusbandName"
              value={fatherOrHusbandName}
              onChange={setGuardiansName}
              ValidationRequired = {true}
              {...(validation = {
                pattern: "^[a-zA-Z-.`' ]*$",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
              disable={editScreen}
            />
          </div>
        </LabelFieldPair>
        <LabelFieldPair>
          <CardLabel style={editScreen ? { color: "#B1B4B6" } : {}}>{`${t("PT_FORM3_RELATIONSHIP")}`}</CardLabel>
          <Dropdown
            className="form-field"
            selected={relationship?.length === 1 ? relationship[0] : relationship}
            disable={relationship?.length === 1 || editScreen}
            option={GuardianOptions}
            select={setGuardianName}
            optionKey="i18nKey"
            t={t}
            name="relationship"
          />
        </LabelFieldPair>
        <LabelFieldPair>
          <CardLabel style={editScreen ? { color: "#B1B4B6" } : {}}>{`${t("PT_FORM3_GENDER")}`}</CardLabel>
          <Dropdown
            className="form-field"
            selected={gender?.length === 1 ? gender[0] : gender}
            disable={gender?.length === 1 || editScreen}
            option={menu}
            select={setGenderName}
            optionKey="code"
            t={t}
            name="gender"
          />
        </LabelFieldPair>
        <LabelFieldPair>
          <CardLabel style={editScreen ? { color: "#B1B4B6" } : {}}>{`${t("PT_OWNER_EMAIL")}`}</CardLabel>
          <div className="field">
            <TextInput
              t={t}
              type={"email"}
              isMandatory={false}
              optionKey="i18nKey"
              name="email"
              value={email}
              onChange={setOwnerEmail}
              disable={editScreen}
            />
          </div>
        </LabelFieldPair>
      </div>
    );
  }

  return (
    <React.Fragment>
    {
      window.location.href.includes("/citizen") ?
        window.location.href.includes("/citizen/pt/property/property-mutation") ? 
          <Timeline currentStep={1} flow="PT_MUTATE" /> : <Timeline currentStep={2} />
    : null
    }

    <FormStep
      config={config}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={!name || !mobileNumber || !gender || !relationship || !fatherOrHusbandName || !isOtpValid}
    >
      <div>
        <CardLabel>{`${t("PT_OWNER_NAME")} *`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="name"
          value={name}
          onChange={setOwnerName}
          disable={isUpdateProperty || isEditProperty}
          ValidationRequired = {true}
          {...(validation = {
            isRequired: true,
            pattern: "^[a-zA-Z-.`' ]*$",
            type: "text",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
        />
        <CardLabel>{`${t("PT_FORM3_GENDER")} *`}</CardLabel>
        <RadioButtons
          t={t}
          options={menu}
          optionsKey="code"
          name="gender"
          value={gender}
          selectedOption={gender}
          onSelect={setGenderName}
          isDependent={true}
          labelKey="PT_COMMON_GENDER"
          disabled={isUpdateProperty || isEditProperty}
        />
        
        <CardLabel>{`${t("PT_FORM3_MOBILE_NUMBER")} *`}</CardLabel>
        <div>
          <MobileNumber
            value={mobileNumber}
            name="mobileNumber"
            onChange={(value) => setMobileNo({ target: { value } })}
            disable={isUpdateProperty || isEditProperty}
            {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
          />
          <small>N.B: On change mobile number, you need to verify the OTP every time.</small>
          <div>
            <button className="submit-bar" disabled={disableSendOtp} onClick={sendOtp} type="submit" style={{display: "inline", marginRight: "10px"}}><header>Send OTP</header></button>
            {isOtpSent && (
              <React.Fragment>
                <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              placeholder={"Enter OTP"}
              name="otp"
              value={otp}
              onChange={onChangeOtp}
              textInputStyle={{width: "fit-content", display: "inline-flex"}}
              disable={isUpdateProperty || isEditProperty}
              ValidationRequired = {true}
              {...(validation = {
                isRequired: true,
                pattern: "[0-9]{6}",
                type: "text",
                title: t("This field is required"),
              })}
            />
           
            <button className="submit-bar" disabled={disableValidateOtp || (!otp || otp?.length<6)} type="submit" style={{display: "inline", marginLeft: "10px"}} onClick={validateOtp}><header>Validate OTP</header></button>
              </React.Fragment>
              )}
          </div>
          {/* <div style={{position: "relative", top: "-10px"}}>          
            {isOtpValid && <small style={{color: "green"}}>OTP validate successfully.</small>}
            {!isOtpValid && otpError && <small style={{color: "red"}}>Invalid OTP.</small>}
          </div> */}

          
        </div>
        <CardLabel>{`${t("PT_FORM3_GUARDIAN_NAME")} *`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="fatherOrHusbandName"
          value={fatherOrHusbandName}
          onChange={setGuardiansName}
          disable={isUpdateProperty || isEditProperty}
          ValidationRequired = {true}
          {...(validation = {
            isRequired: true,
            pattern: "^[a-zA-Z-.`' ]*$",
            type: "text",
            title: t("PT_NAME_ERROR_MESSAGE"),
          })}
        />
        <CardLabel>{`${t("PT_FORM3_RELATIONSHIP")} *`}</CardLabel>
        <RadioButtons
          t={t}
          optionsKey="i18nKey"
          name="relationship"
          options={GuardianOptions}
          value={relationship}
          selectedOption={relationship}
          onSelect={setGuardianName}
          isDependent={true}
          labelKey="PT_RELATION"
          disabled={isUpdateProperty || isEditProperty}
        />
      </div>
    </FormStep>
    {showToast && <Toast success={showToast.key} label={t(showToast.label)} onClose={closeToast} isDleteBtn={true} />}
    {showToastErrorMsg && <Toast error={showToastErrorMsg.key} label={t(showToastErrorMsg.label)} onClose={closeToastError} isDleteBtn={true} />}

    </React.Fragment>
  );
};

export default SelectOwnerDetails;
