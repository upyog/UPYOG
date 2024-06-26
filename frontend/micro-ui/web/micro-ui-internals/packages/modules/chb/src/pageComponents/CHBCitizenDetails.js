import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons, LabelFieldPair, Dropdown, Menu, MobileNumber, Card,CardSubHeader } from "@upyog/digit-ui-react-components";
import { useLocation, useRouteMatch } from "react-router-dom";
import Timeline from "../components/CHBTimeline";
import ChbCancellationPolicy from "../components/ChbCancellationPolicy";

const CHBCitizenDetails
 = ({ t, config, onSelect, userType, formData, ownerIndex,searchParams,value=formData.slotlist}) => {
  const { pathname: url } = useLocation();

  let index =window.location.href.charAt(window.location.href.length - 1);
  
   
  let validation = {};

  const [applicantName, setName] = useState((formData.ownerss && formData.ownerss[index] && formData.ownerss[index].applicantName) || formData?.ownerss?.applicantName || "");
  const [emailId, setEmail] = useState((formData.ownerss && formData.ownerss[index] && formData.ownerss[index].emailId) || formData?.ownerss?.emailId || "");
  const [mobileNumber, setMobileNumber] = useState(
    (formData.ownerss && formData.ownerss[index] && formData.ownerss[index].mobileNumber) || formData?.ownerss?.mobileNumber || ""
  );
  const [alternateNumber, setAltMobileNumber] = useState(
    (formData.ownerss && formData.ownerss[index] && formData.ownerss[index].alternateNumber) || formData?.ownerss?.alternateNumber || ""
  );
  const [localSearchParams, setLocalSearchParams] = useState(() => ({ ...searchParams }));

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

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
  

  const goNext = () => {
    let owner = formData.ownerss && formData.ownerss[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, applicantName, mobileNumber,alternateNumber, emailId};
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      
      ownerStep = { ...owner, applicantName,  mobileNumber,alternateNumber,emailId };
      onSelect(config.key, ownerStep, false,index);
    }
    console.log(ownerStep);
  };

  const onSkip = () => onSelect();

  
  

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, []);
console.log("value----->",value);
 
const {slotlist}=value;
  return (
   
    <React.Fragment>
      
    {
      window.location.href.includes("/citizen") ?
 <Timeline currentStep={1} />
    : null
    }
    <Card>
      <CardSubHeader>{value?.bookingSlotDetails.map((slot) =>(
        <div>
           {slot.name}
        </div>
       
    // <div key={index}>
    //   {slot.name}
    //   {/* ({slot.date1}) */}
    // </div>
  ))}</CardSubHeader>
  <ChbCancellationPolicy/>
      </Card>
  
    <FormStep
      config={config}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      // isDisabled={!applicantName || !mobileNumber || !emailId}
    >
      
      <div>
        <CardLabel>{`${t("CHB_APPLICANT_NAME")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="applicantName"
          value={applicantName}
          onChange={setOwnerName}
          ValidationRequired = {true}
          {...(validation = {
            // isRequired: true,
            pattern: "^[a-zA-Z ]+$",
            type: "tel",
            title: t("CHB_NAME_ERROR_MESSAGE"),
          })}
       
         
        />
       
        <CardLabel>{`${t("CHB_MOBILE_NUMBER")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
        <MobileNumber
          value={mobileNumber}
          name="mobileNumber"
          onChange={(value) => setMobileNo({ target: { value } })}
          {...{ pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
        />

        <CardLabel>{`${t("CHB_ALT_MOBILE_NUMBER")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
          <MobileNumber
            value={alternateNumber}
            name="alternateNumber"
            onChange={(value) => setAltMobileNo({ target: { value } })}
            {...{ required: false, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
          />

        <CardLabel>{`${t("CHB_EMAIL_ID")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="emailId"
          value={emailId}
          onChange={setOwnerEmail}
          ValidationRequired = {true}
          {...(validation = {
           required: true, 
           pattern: "[A-Za-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$", 
           type: "email",
           title: t("CHB_NAME_ERROR_MESSAGE"),
          })}
        />
        
        
      </div>
    </FormStep>
    </React.Fragment>
  );
};

export default CHBCitizenDetails;