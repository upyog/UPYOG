import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import { CardHeader, CardLabel, Dropdown, FormStep, TextInput, Toast } from "@upyog/digit-ui-react-components";
import { cardBodyStyle } from "../utils";
import Timeline from "../components/TLTimeline";

const SelectInistitutionOwnerDetails = ({ t, config, onSelect, userType, formData }) => {
  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const isMutation = url.includes("property-mutation");
  const getUserType = () => Digit.UserService.getType();
  const TYPE_OWNER_VALIDATE = { type: "owner_validate" };
  let index = 0;
  let validation = {};
  const [showToast, setShowToast] = useState(null);
  const [isOtpSent, setIsOtpSent] = useState(false);
  const [showToastErrorMsg, setShowToastErrorMsg] = useState(null);

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [otp, setOtp] = useState();
  const [isOtpValid, setIsOtpValid] = useState(true);
  const [disableSendOtp, setDisableSendOtp] = useState(true);
  const [disableValidateOtp, setDisableValidateOtp] = useState(true);
  const [inistitutionName, setInistitutionName] = useState(formData.owners && formData.owners[index] && formData.owners[index].inistitutionName);
  const [inistitutetype, setInistitutetype] = useState(formData.owners && formData.owners[index] && formData.owners[index].inistitutetype);
  const [name, setName] = useState(formData.owners && formData.owners[index] && formData.owners[index].name);
  const [designation, setDesignation] = useState(formData.owners && formData.owners[index] && formData.owners[index].designation);
  const [mobileNumber, setMobileNumber] = useState(formData.owners && formData.owners[index] && formData.owners[index].mobileNumber);
  const [altContactNumber, setAltContactNumber] = useState(formData.owners && formData.owners[index] && formData.owners[index].altContactNumber);
  const [emailId, setEmailId] = useState(formData.owners && formData.owners[index] && formData.owners[index].emailId);
  const isUpdateProperty = formData?.isUpdateProperty || false;
  let isEditProperty = formData?.isEditProperty || false;

  function setInistitution(e) {
    setInistitutionName(e.target.value);
  }
  function setTypeOfInistituteName(inistitutetype) {
    setInistitutetype(inistitutetype);
  }
  function setInistituteName(e) {
    setName(e.target.value);
  }
  function setDesignationName(e) {
    setDesignation(e.target.value);
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
  function setAltContactNo(e) {
    setAltContactNumber(e.target.value);
  }
  function setEmail(e) {
    setEmailId(e.target.value);
  }
  function onChangeOtp(e) {
    setOtp(e.target.value);
  }

  const formDropdown = (category) => {
    const { name, code } = category;
    return {
      label: name,
      value: code,
      code: t(`PROPERTYTAX_BILLING_SLAB_${code}`),
    };
  };

  const getDropdwonForInstitution = () => {
    let SubOwnerShipCategory = {};
    let PropertyTaxPayload = JSON.parse(sessionStorage.getItem("getSubPropertyOwnerShipCategory"));
    let SubOwnerShipCategoryOb = PropertyTaxPayload?.PropertyTax?.SubOwnerShipCategory;
    SubOwnerShipCategoryOb &&
      SubOwnerShipCategoryOb.length > 0 &&
      SubOwnerShipCategoryOb.map((category) => {
        SubOwnerShipCategory[category.code] = category;
      });
    const institutedropDown = [];
    const value = formData.ownershipCategory.value || "";
    SubOwnerShipCategory &&
      Object.keys(SubOwnerShipCategory)
        .filter((subCategory) => SubOwnerShipCategory[subCategory].ownerShipCategory === value)
        .forEach((linkedCategory) => {
          institutedropDown.push(formDropdown(SubOwnerShipCategory[linkedCategory]));
        });
    institutedropDown.sort((a, b) => a.label.localeCompare(b.label));
    return institutedropDown;
  };

  const goNext = () => {
    let ownerDetails = formData.owners && formData.owners[index];
    if (ownerDetails) {
      ownerDetails["inistitutionName"] = inistitutionName;
      ownerDetails["inistitutetype"] = inistitutetype;
      ownerDetails["name"] = name;
      ownerDetails["designation"] = designation;
      ownerDetails["mobileNumber"] = mobileNumber;
      ownerDetails["altContactNumber"] = altContactNumber;
      ownerDetails["emailId"] = emailId;
      onSelect(config.key, isMutation ? [ownerDetails] : ownerDetails, false, index);
    } else {
      let ownerStep = { ...ownerDetails, inistitutionName, inistitutetype, name, designation, mobileNumber, altContactNumber, emailId };
      if (isMutation) onSelect(config.key, [ownerStep], false, index);
      else onSelect(config.key, ownerStep, false, index);
    }
  };

  const checkMutatePT = window.location.href.includes("citizen/pt/property/property-mutation/") ? (
    <Timeline currentStep={1} flow="PT_MUTATE" />
  ) : (
    <Timeline currentStep={2} />
  );

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

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? checkMutatePT : null}
      <FormStep
        config={config}
        t={t}
        onSelect={goNext}
        isDisabled={!inistitutionName || !inistitutetype || !name || !designation || !mobileNumber || !isOtpValid }
      >
        <div>
          <CardLabel>{`${t("PT_COMMON_INSTITUTION_NAME")}*`}</CardLabel>
          <TextInput
            isMandatory={false}
            optionKey="i18nKey"
            t={t}
            name="institutionName"
            onChange={setInistitution}
            value={inistitutionName}
            disable={isUpdateProperty || isEditProperty}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z_@./()#&+- ]*$",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
          />
          <CardLabel>{`${t("PT_TYPE_OF_INSTITUTION")}*`}</CardLabel>
          <Dropdown
            t={t}
            isMandatory={false}
            option={getDropdwonForInstitution() || []}
            selected={inistitutetype}
            optionKey="code"
            select={setTypeOfInistituteName}
            disabled={isUpdateProperty || isEditProperty}
          />
          <CardHeader>{t("PT_AUTH_PERSON_DETAILS")}</CardHeader>
          <CardLabel>{`${t("PT_OWNER_NAME")}*`}</CardLabel>
          <TextInput
            isMandatory={false}
            optionKey="i18nKey"
            t={t}
            name="name"
            onChange={setInistituteName}
            value={name}
            disable={isUpdateProperty || isEditProperty}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z-.`' ]*$",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
          />
          <CardLabel>{`${t("PT_COMMON_AUTHORISED_PERSON_DESIGNATION")}*`}</CardLabel>
          <TextInput
            isMandatory={false}
            optionKey="i18nKey"
            t={t}
            name="designation"
            onChange={setDesignationName}
            value={designation}
            disable={isUpdateProperty || isEditProperty}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z-.`' ]*$",
              title: t("PT_DESIGNATION_ERROR_MESSAGE"),
            })}
          />
          <CardLabel>{`${t("PT_FORM3_MOBILE_NUMBER")}*`}</CardLabel>
          
          <div>
            <TextInput
              isMandatory={false}
              optionKey="i18nKey"
              t={t}
              name="setMobileNo"
              onChange={setMobileNo}
              value={mobileNumber}
              type={"tel"}
              disable={isUpdateProperty || isEditProperty}
              {...(validation = {
                isRequired: true,
                pattern: "[6-9]{1}[0-9]{9}",
                type: "tel",
                title: t("CORE_COMMON_APPLICANT_ALT_NUMBER_INVALID"),
              })}
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
          
          <CardLabel>{`${t("PT_OWNERSHIP_INFO_TEL_PHONE_NO")}*`}</CardLabel>
          <TextInput
            isMandatory={false}
            optionKey="i18nKey"
            t={t}
            name="altContactNumber"
            onChange={setAltContactNo}
            value={altContactNumber}
            type={"tel"}
            disable={isUpdateProperty || isEditProperty}
            {...(validation = {
              isRequired: true,
              pattern: "^[0-9]{11}$",
              type: "tel",
              title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
            })}
          />
          <CardLabel>{t("PT_FORM3_EMAIL_ID")}</CardLabel>
          <TextInput
            isMandatory={false}
            optionKey="i18nKey"
            t={t}
            name="email"
            onChange={setEmail}
            type="email"
            value={emailId}
            disable={isUpdateProperty || isEditProperty}
            // {...(validation = {
            //   isRequired: true,
            //   type: "email",
            //   title: t("PT_EMAIL_ID_ERROR_MESSAGE"),
            // })}
          />
        </div>
      </FormStep>
      {showToast && <Toast success={showToast.key} label={t(showToast.label)} onClose={closeToast} isDleteBtn={true} />}
    {showToastErrorMsg && <Toast error={showToastErrorMsg.key} label={t(showToastErrorMsg.label)} onClose={closeToastError} isDleteBtn={true} />}

    </React.Fragment>
  );
};

export default SelectInistitutionOwnerDetails;
