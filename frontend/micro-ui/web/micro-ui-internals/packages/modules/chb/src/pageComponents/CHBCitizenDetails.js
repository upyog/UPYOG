import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, MobileNumber, Card,CardSubHeader } from "@upyog/digit-ui-react-components";
import { useLocation} from "react-router-dom";
import Timeline from "../components/CHBTimeline";
import ChbCancellationPolicy from "../components/ChbCancellationPolicy";
// import { TimerValues } from "../components/TimerValues";


/**
 * CHBCitizenDetails Component
 * 
 * This component is responsible for rendering the citizen details form for the CHB module.
 * It allows users to input and manage personal information such as name, email ID, mobile number, and alternate mobile number.
 * 
 * Props:
 * - `t`: Translation function for internationalization.
 * - `config`: Configuration object for the form step.
 * - `onSelect`: Callback function triggered when the form step is completed.
 * - `userType`: Type of the user (e.g., employee or citizen).
 * - `formData`: Existing form data to prefill the fields.
 * - `value`: Default value for the form fields.
 * 
 * State Variables:
 * - `applicantName`: State variable to manage the applicant's name.
 * - `emailId`: State variable to manage the applicant's email ID.
 * - `mobileNumber`: State variable to manage the applicant's mobile number.
 * - `alternateNumber`: State variable to manage the applicant's alternate mobile number.
 * 
 * Functions:
 * - `setOwnerName`: Updates the `applicantName` state after removing non-alphabetic characters.
 * - `setOwnerEmail`: Updates the `emailId` state with the entered email.
 * - `setMobileNo`: Updates the `mobileNumber` state with the entered mobile number.
 * 
 * Other Variables:
 * - `url`: The current URL path.
 * - `index`: The last character of the current URL, used to determine the index of the owner in the form data.
 * - `validation`: An object to store validation rules (currently empty).
 * - `user`: The current user's information fetched from the Digit User Service.
 */
const CHBCitizenDetails
 = ({ t, config, onSelect, userType, formData,value=formData.slotlist}) => {
  const { pathname: url } = useLocation();

  let index =window.location.href.charAt(window.location.href.length - 1);
  
  let validation = {};
  const user = Digit.UserService.getUser().info;
  const [applicantName, setName] = useState((formData.ownerss && formData.ownerss[index] && formData.ownerss[index].applicantName) || formData?.ownerss?.applicantName || value?.existingDataSet?.ownerss?.applicantName ||"");
  const [emailId, setEmail] = useState((formData.ownerss && formData.ownerss[index] && formData.ownerss[index].emailId) || formData?.ownerss?.emailId || value?.existingDataSet?.ownerss?.emailId || "");
  const [mobileNumber, setMobileNumber] = useState(
    (formData.ownerss && formData.ownerss[index] && formData.ownerss[index].mobileNumber) || formData?.ownerss?.mobileNumber || value?.existingDataSet?.ownerss?.mobileNumber || user?.mobileNumber
  );
  const [alternateNumber, setAltMobileNumber] = useState(
    (formData.ownerss && formData.ownerss[index] && formData.ownerss[index].alternateNumber) || formData?.ownerss?.alternateNumber || value?.existingDataSet?.ownerss?.alternateNumber || ""
  );
  function setOwnerName(e) {
    const input = e.target.value.replace(/[^a-zA-Z\s]/g, ''); // Remove non-alphabetic characters and non-space characters
  setName(input);
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

  const formatSlotDetails = (slots) => {
    const sortedSlots = slots.sort((a, b) => new Date(a.bookingDate) - new Date(b.bookingDate));
    const firstDate = sortedSlots[0]?.bookingDate;
    const lastDate = sortedSlots[sortedSlots.length - 1]?.bookingDate;
    if(firstDate===lastDate){
      return `${sortedSlots[0]?.name} (${firstDate})`;
    }
    else{
    return `${sortedSlots[0]?.name} (${firstDate} - ${lastDate})`;
    }
  };

  return (
   
    <React.Fragment>
      
    {
      window.location.href.includes("/citizen") ?
 <Timeline currentStep={1} />
    : <Timeline currentStep={1} />
    }
    <Card>
        <CardSubHeader>
          <div style={{display:"flex", justifyContent: "space-between", width: "100%" }}>
          {value?.bookingSlotDetails && value.bookingSlotDetails.length > 0
            ? formatSlotDetails(value.bookingSlotDetails)
            : null}
            {/* <TimerValues timerValues={value?.existingDataSet?.timervalue?.timervalue} SlotSearchData={value?.Searchdata} /> */}
          </div>
        </CardSubHeader>
        <ChbCancellationPolicy slotDetail={value?.bookingSlotDetails}/>
      </Card>
    <FormStep
      config={config}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={!applicantName || !mobileNumber || !emailId}
    >
      
      <div>
        <CardLabel>{`${t("CHB_APPLICANT_NAME")}`} <span className="check-page-link-button">*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="applicantName"
          placeholder={"Enter Applicant Name"}
          value={applicantName}
          onChange={setOwnerName}
          style={{width:user.type==="EMPLOYEE"?"51.6%":null}}
          ValidationRequired = {true}
          {...(validation = {
            // isRequired: true,
            pattern: "^[a-zA-Z ]+$",
            type: "tel",
            title: t("CHB_NAME_ERROR_MESSAGE"),
          })}
       
         
        />
       
        <CardLabel>{`${t("CHB_MOBILE_NUMBER")}`} <span className="check-page-link-button">*</span></CardLabel>
        <MobileNumber
          value={mobileNumber}
          name="mobileNumber"
          style={{width:user.type==="EMPLOYEE"?"50%":null}}
          placeholder={"Enter Applicant Register Mobile Number"}
          onChange={(value) => setMobileNo({ target: { value } })}
          {...{ pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
        />

        <CardLabel>{`${t("CHB_ALT_MOBILE_NUMBER")}`}</CardLabel>
          <MobileNumber
            value={alternateNumber}
            name="alternateNumber"
            style={{width:user.type==="EMPLOYEE"?"50%":null}}
            placeholder={"Enter Alternate Mobile Number"}
            onChange={(value) => setAltMobileNo({ target: { value } })}
            {...{ required: false, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
          />

        <CardLabel>{`${t("CHB_EMAIL_ID")}`} <span className="check-page-link-button">*</span></CardLabel>
        <TextInput
          t={t}
          type={"email"}
          isMandatory={false}
          optionKey="i18nKey"
          name="emailId"
          value={emailId}
          placeholder={"Enter Applicant Email Id"}
          style={{width:user.type==="EMPLOYEE"?"51.6%":null}}
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