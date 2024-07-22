import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Card,CardSubHeader,Toast } from "@nudmcdgnpm/digit-ui-react-components";
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
  const [showToast, setShowToast] = useState(null);

  useEffect(() => {
    if (ifscCode.length === 11) {
      fetch(`https://ifsc.razorpay.com/${ifscCode}`)
        .then(response => response.json())
        .then(data => {
          if (data && data.BANK && data.BRANCH) {
            setBankName(data.BANK);
            setBankBranchName(data.BRANCH);
            setShowToast({ error: false, label: t("CHB_IFSC_CODE_VALID") });
          } else {
            setShowToast({ error: true, label: t("CHB_IFSC_CODE_INVALID") });
          }
        })
        .catch(() => {
          setShowToast({ error: true, label: t("CHB_IFSC_CODE_INVALID") });
        });
      }
    else {
      setBankName("");
      setBankBranchName("");
    }
  }, [ifscCode]);

  useEffect(() => {
    if (accountNumber && confirmAccountNumber && accountNumber === confirmAccountNumber) {
      setShowToast({ error: false, label: t("CHB_ACCOUNT_NUMBERS_MATCH") });
    }
  }, [accountNumber, confirmAccountNumber, t]);
  function setApplicantAccountNumber(e) {
    const input = e.target.value.replace(/\D/g, ''); // Replace non-digit characters
    if (input.length <= 16) {
      setAccountNumber(input);
    }
  }
  function setApplicantConfirmAccountNumber(e) {
    const input = e.target.value.replace(/\D/g, ''); // Replace non-digit characters
    if (input.length <= 16) {
      setConfirmAccountNumber(input);
    }
  }
  function setApplicantIfscCode(e) {
    const input = e.target.value.replace(/[^a-zA-Z0-9]/g, ''); // Remove non-alphanumeric characters
    if (input.length <= 11) {
      setIfscCode(input);
    }
  }
  function setApplicantBankName(e) {
    setBankName(e.target.value);
  }
  function setApplicantBankBranchName(e) {
    setBankBranchName(e.target.value);
  }
  function setApplicantAccountHolderName(e) {
    const input = e.target.value.replace(/[^a-zA-Z\s]/g, '');
    setAccountHolderName(input);
  }
  
  

  const goNext = () => {
    if (accountNumber !== confirmAccountNumber) {
      setShowToast({ error: true, label: t("CHB_ACCOUNT_NUMBERS_DO_NOT_MATCH") });
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
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 1000); // Close toast after 1 seconds

      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);

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
 <Timeline currentStep={4} />
    : null
    }
   <Card>
        <CardSubHeader>
          {value?.bookingSlotDetails && value.bookingSlotDetails.length > 0
            ? formatSlotDetails(value.bookingSlotDetails)
            : null}
        </CardSubHeader>
        <ChbCancellationPolicy count={value?.bookingSlotDetails.length}/>
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
          placeholder={"Enter Account Number"}
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
          placeholder={"Enter Confirm Account Number"}
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
            placeholder={"Enter IFSC Code"}
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
            placeholder={"Bank Name Auto Select"}
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
            placeholder={"Bank Branch Name Auto Select"}
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
          placeholder={"Enter Account Holder Name"}
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

export default CHBBankDetails ;