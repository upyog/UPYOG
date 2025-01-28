import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, MobileNumber, Card, CardSubHeader } from "@upyog/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import Timeline from "../components/ADSTimeline";
import ADSCartAndCancellationPolicyDetails from "../components/ADSCartAndCancellationPolicyDetails";
import {TimerValues} from "../components/TimerValues";

/*
 * The ADSCitizenDetails component is responsible for gathering and validating 
 * applicant information, including name, mobile number, alternate number, 
 * and email address. It provides a structured form step for user input.
 */

const ADSCitizenDetails = ({ t, config, onSelect, userType, formData,value=formData.adslist}) => {
  const { pathname: url } = useLocation();

  let index = window.location.href.charAt(window.location.href.length - 1);

  let validation = {};
  const user = Digit.UserService.getUser().info;
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.ads.useADSCreateAPI();
  const [applicantName, setName] = useState(
    (formData.applicant && formData.applicant[index] && formData.applicant[index].applicantName) || formData?.applicant?.applicantName || value?.existingDataSet?.applicant?.applicantName || ""
  );
  const [emailId, setEmail] = useState(
    (formData.applicant && formData.applicant[index] && formData.applicant[index].emailId) || formData?.applicant?.emailId || value?.existingDataSet?.applicant?.emailId  ||""
  );
  const [mobileNumber, setMobileNumber] = useState(
    (formData.applicant && formData.applicant[index] && formData.applicant[index].mobileNumber) ||  value?.existingDataSet?.applicant?.mobileNumber  ||
      formData?.applicant?.mobileNumber ||
      user?.mobileNumber
  );
  const [alternateNumber, setAltMobileNumber] = useState(
    (formData.applicant && formData.applicant[index] && formData.applicant[index].alternateNumber) || formData?.applicant?.alternateNumber ||  value?.existingDataSet?.applicant?.alternateNumber || ""
  );
  function setApplicantName(e) {
    const input = e.target.value.replace(/[^a-zA-Z\s]/g, ""); // Remove non-alphabetic characters and non-space characters
    setName(input);
  }
  function setApplicantEmail(e) {
    setEmail(e.target.value);
  }

  function setMobileNo(e) {
    setMobileNumber(e.target.value);
  }

  function setAltMobileNo(e) {
    setAltMobileNumber(e.target.value);
  }

  const goNext = () => {
    let applicantData = formData.applicant && formData.applicant[index];
    // Create the formdata object
    let cartDetails = value?.cartDetails.map((slot) => {
      return { 
        addType:slot.addTypeCode,
        faceArea:slot.faceAreaCode,
        location:slot.locationCode,
        nightLight:slot.nightLight==="Yes"? true : false,
        bookingDate:slot.bookingDate,
        bookingFromTime: "06:00",
        bookingToTime: "05:59",
        status:"BOOKING_CREATED"
      };
    });
    const formdata = {
      bookingApplication: {
        tenantId: tenantId,
        applicantDetail: {
          applicantName: applicantName,
          applicantMobileNo: mobileNumber,
          applicantAlternateMobileNo: alternateNumber,
          applicantEmailId: emailId,
        },
        cartDetails: cartDetails,
        bookingStatus: "BOOKING_CREATED",
      },
      isDraftApplication: true,
    };
  
    // Trigger the mutation
    mutation.mutate(formdata, {
      onSuccess: (data) => {
        const newDraftId = data?.bookingApplication[0]?.draftId;
        // Now, only execute the logic you want after the mutation is successful
        let applicantStep;
        if (userType === "citizen") {
          applicantStep = { ...applicantData, applicantName, mobileNumber, alternateNumber, emailId,draftId: newDraftId};
          onSelect(config.key, { ...formData[config.key], ...applicantStep }, false, index);
        } else {
          applicantStep = { ...applicantData, applicantName, mobileNumber, alternateNumber, emailId,draftId: newDraftId };
          onSelect(config.key, applicantStep, false, index);
        }
      },
    });
  };
  
  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, []);

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}
      <Card>
      <div style={{ position: "relative" }}>
        <CardSubHeader style={{ position: "absolute",right:0}}>
        <TimerValues 
          timerValues={value?.existingDataSet?.timervalue?.timervalue} 
          SlotSearchData={value?.cartDetails} draftId={value?.existingDataSet?.draftId}
        />
        </CardSubHeader>
        <ADSCartAndCancellationPolicyDetails/>
      </div>
      </Card>
      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t} isDisabled={!applicantName || !mobileNumber || !emailId}>
        <div>
          <CardLabel>
            {`${t("ADS_APPLICANT_NAME")}`} <span className="check-page-link-button">*</span>
          </CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="applicantName"
            placeholder={"Enter Applicant Name"}
            value={applicantName}
            onChange={setApplicantName}
            style={{ width: user.type === "EMPLOYEE" ? "51.6%" : null }}
            ValidationRequired={true}
            {...(validation = {
              // isRequired: true,
              pattern: "^[a-zA-Z ]+$",
              type: "tel",
              title: t("CHB_NAME_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>
            {`${t("ADS_MOBILE_NUMBER")}`} <span className="check-page-link-button">*</span>
          </CardLabel>
          <MobileNumber
            value={mobileNumber}
            name="mobileNumber"
            style={{ width: user.type === "EMPLOYEE" ? "50%" : null }}
            placeholder={"Enter Applicant Register Mobile Number"}
            onChange={(value) => setMobileNo({ target: { value } })}
            {...{ pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
          />

          <CardLabel>{`${t("ADS_ALT_MOBILE_NUMBER")}`}</CardLabel>
          <MobileNumber
            value={alternateNumber}
            name="alternateNumber"
            style={{ width: user.type === "EMPLOYEE" ? "50%" : null }}
            placeholder={"Enter Alternate Mobile Number"}
            onChange={(value) => setAltMobileNo({ target: { value } })}
            {...{ required: false, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
          />

          <CardLabel>
            {`${t("ADS_EMAIL_ID")}`} <span className="check-page-link-button">*</span>
          </CardLabel>
          <TextInput
            t={t}
            type={"email"}
            isMandatory={false}
            optionKey="i18nKey"
            name="emailId"
            value={emailId}
            placeholder={"Enter Applicant Email Id"}
            style={{ width: user.type === "EMPLOYEE" ? "51.6%" : null }}
            onChange={setApplicantEmail}
            ValidationRequired={true}
            {...(validation = {
              required: true,
              pattern: "[A-Za-z0-9._%+-]+@[a-z0-9.-]+.[a-z]{2,4}$",
              type: "email",
              title: t("CHB_NAME_ERROR_MESSAGE"),
            })}
          />
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default ADSCitizenDetails;
