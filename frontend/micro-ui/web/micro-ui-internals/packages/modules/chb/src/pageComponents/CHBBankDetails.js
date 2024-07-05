import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Card,CardSubHeader } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation, useRouteMatch } from "react-router-dom";
import Timeline from "../components/CHBTimeline";
import ChbCancellationPolicy from "../components/ChbCancellationPolicy";

const CHBBankDetails 
 = ({ t, config, onSelect, userType, formData, ownerIndex,searchParams,value=formData.slotlist}) => {
  const { pathname: url } = useLocation();

  let index =window.location.href.charAt(window.location.href.length - 1);
  
   
  let validation = {};

  const [accountNumber , setAccountNumber ] = useState((formData.bankdetails && formData.bankdetails[index] && formData.bankdetails[index].accountNumber) || formData?.bankdetails?.accountNumber || "");
  const [confirmAccountNumber , setConfirmAccountNumber ] = useState((formData.bankdetails && formData.bankdetails[index] && formData.bankdetails[index].confirmAccountNumber) || formData?.bankdetails?.confirmAccountNumber || "");
  const [ifscCode , setIfscCode ] = useState((formData.bankdetails && formData.bankdetails[index] && formData.bankdetails[index].ifscCode) || formData?.bankdetails?.ifscCode || "");
  const [bankName , setBankName ] = useState((formData.bankdetails && formData.bankdetails[index] && formData.bankdetails[index].bankName) || formData?.bankdetails?.bankName || "");
  const [bankBranchName , setBankBranchName ] = useState((formData.bankdetails && formData.bankdetails[index] && formData.bankdetails[index].bankBranchName) || formData?.bankdetails?.bankBranchName || "");
  const [accountHolderName , setAccountHolderName ] = useState((formData.bankdetails && formData.bankdetails[index] && formData.bankdetails[index].accountHolderName) || formData?.bankdetails?.accountHolderName || "");

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

  useEffect(() => {
    if (ifscCode.length === 11) {
      fetch(`https://ifsc.razorpay.com/${ifscCode}`)
        .then(response => response.json())
        .then(data => {
          if (data) {
            setBankName(data.BANK);
            setBankBranchName(data.BRANCH);
          }
        })
        .catch(error => {
          console.error("Error fetching IFSC details:", error);
        });
    }
    else {
      setBankName("");
      setBankBranchName("");
    }
  }, [ifscCode]);

  function setApplicantAccountNumber(e) {
    if (e.target.value.length <= 16) {
    setAccountNumber(e.target.value);
    }
  }
  function setApplicantConfirmAccountNumber(e) {
    if (e.target.value.length <= 16) {
    setConfirmAccountNumber(e.target.value);
    }
  }
  function setApplicantIfscCode(e) {
    if (e.target.value.length <= 11) {
      setIfscCode(e.target.value);
    }
  }
  function setApplicantBankName(e) {
    setBankName(e.target.value);
  }
  function setApplicantBankBranchName(e) {
    setBankBranchName(e.target.value);
  }
  function setApplicantAccountHolderName(e) {
    setAccountHolderName(e.target.value);
  }
  
  

  const goNext = () => {
    if (accountNumber !== confirmAccountNumber) {
      alert(t("CHB_ACCOUNT_NUMBERS_DO_NOT_MATCH"));
      return;
    }
    let owner = formData.bankdetails && formData.bankdetails[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner,accountNumber ,confirmAccountNumber,ifscCode,bankName,bankBranchName,accountHolderName};
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      
      ownerStep = { ...owner,accountNumber ,confirmAccountNumber,ifscCode,bankName,bankBranchName,accountHolderName };
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

  return (
   
    <React.Fragment>
      
    {
      window.location.href.includes("/citizen") ?
 <Timeline currentStep={3} />
    : null
    }
    <Card>
      <CardSubHeader>{value?.bookingSlotDetails.map((slot) =>(
        <div>
        <div key={index}>
          {slot.name}
          ({slot.bookingDate})
        </div>
        </div>
  ))}</CardSubHeader>
  <ChbCancellationPolicy/>
      </Card>
  
    <FormStep
      config={config}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={!accountNumber || !confirmAccountNumber || !ifscCode || !bankName || !bankBranchName || !accountHolderName }
    >
      
      <div>
        <CardLabel>{`${t("CHB_ACCOUNT_NUMBER")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="accountNumber"
          value={accountNumber}
          onChange={setApplicantAccountNumber}
          minLength={8}
          maxLength={16}
          ValidationRequired = {true}
          {...(validation = {
            // isRequired: true,
            pattern:  "[0-9]{8,16}",
            type: "text",
            title: t("CHB_INVALID_ACCOUNT_NUMBER"),
          })}
       
         
        />
       
        <CardLabel>{`${t("CHB_CONFIRM_ACCOUNT_NUMBER")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="confirmAccountNumber"
          value={confirmAccountNumber}
          onChange={setApplicantConfirmAccountNumber}
          minLength={8}
          maxLength={16}
          ValidationRequired = {true}
          {...(validation = {
            // isRequired: true,
            pattern: "[0-9]{8,16}",
            type: "text",
            title: t("CHB_INVALID_CONFIRM_ACCOUNT_NUMBER"),
          })}
       
         
        />
       
        <CardLabel>{`${t("CHB_IFSC_CODE")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="ifscCode"
            value={ifscCode}
            onChange={setApplicantIfscCode}
            maxLength={11}
            ValidationRequired={true}
            {...(validation = {
              pattern: "[a-zA-Z0-9]{11}",
              type: "text",
              title: t("CHB_INVALID_IFSC_CODE"),
            })}
          />
       
        <CardLabel>{`${t("CHB_BANK_NAME")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="bankName"
            value={bankName}
            onChange={setApplicantBankName}
            disabled={true}
          />
       
        <CardLabel>{`${t("CHB_BANK_BRANCH_NAME")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="bankBranchName"
            value={bankBranchName}
            onChange={setApplicantBankBranchName}
            disabled={true}
          />
        <CardLabel>{`${t("CHB_ACCOUNT_HOLDER_NAME")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="accountHolderName"
          value={accountHolderName}
          onChange={setApplicantAccountHolderName}
          ValidationRequired = {true}
          {...(validation = {
            // isRequired: true,
            pattern: "^[a-zA-Z ]+$",
            type: "text",
            title: t("CHB_INVALID_ACCOUNT_HOLDER_NAME"),
          })}
       
         
        />
      </div>
    </FormStep>
    </React.Fragment>
  );
};

export default CHBBankDetails ;