/**
 * @author - Shivank Shukla 
 * The SVBankDetails component is a form step that collects and validates user bank account details.

    Key Features:
    1. Data Validation:
    - Ensures account number and confirm account number match.
    - Validates IFSC code and account holder's name for correct format.

    2. Automatic Bank Info Fetching:
    - Auto-fills bank name and branch based on a valid IFSC code using an external API.

    3. Error Handling:
    - Displays toast notifications for validation errors or invalid IFSC codes.

    4. Controlled Input Fields:
    - Manages all form fields using state to ensure data accuracy and consistency.
    
 * 
 * 
 */

import React, { useEffect, useState, useCallback } from "react";
import { FormStep, TextInput, CardLabel, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import Timeline from "../components/Timeline";

const SVBankDetails = ({ t, config, onSelect, userType, formData }) => {
  const [bankDetails, setBankDetails] = useState({
    accountNumber: formData?.bankdetails?.accountNumber || "",
    confirmAccountNumber: formData?.bankdetails?.confirmAccountNumber || "",
    ifscCode: formData?.bankdetails?.ifscCode || "",
    bankName: formData?.bankdetails?.bankName || "",
    bankBranchName: formData?.bankdetails?.bankBranchName || "",
    accountHolderName: formData?.bankdetails?.accountHolderName || "",
  });
  const [showToast, setShowToast] = useState(null);

  const user = Digit.UserService.getUser().info;

  const fetchBankDetails = useCallback(async () => {
    try {
      const response = await fetch(`https://ifsc.razorpay.com/${bankDetails.ifscCode}`);
      const data = await response.json();
      if (data?.BANK && data?.BRANCH) {
        setBankDetails((prev) => ({
          ...prev,
          bankName: data.BANK,
          bankBranchName: data.BRANCH,
        }));
      } else {
        setShowToast({ error: true, label: t("SV_IFSC_CODE_INVALID") });
      }
    } catch {
      setShowToast({ error: true, label: t("SV_IFSC_CODE_INVALID") });
    }
  }, [bankDetails.ifscCode, t]);

  useEffect(() => {
    if (bankDetails.ifscCode.length === 11) {
      fetchBankDetails();
    }
  }, [bankDetails.ifscCode, fetchBankDetails]);

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => setShowToast(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [bankDetails.accountNumber ||bankDetails.confirmAccountNumber || bankDetails.ifscCode || bankDetails.bankName || bankDetails.bankBranchName || bankDetails.accountHolderName]);

  const handleInputChange = (field, value, maxLength = null, regex = null) => {
    if (regex) value = value.replace(regex, '');
    if (maxLength && value.length > maxLength) return;
    setBankDetails((prev) => ({ ...prev, [field]: value }));
  };

  const goNext = () => {
    if (bankDetails.accountNumber !== bankDetails.confirmAccountNumber) {
        alert(t("SV_ACCOUNT_NUMBER_MISMATCH"));
        return;
      }
    onSelect(config.key, { ...formData.bankdetails, ...bankDetails }, false);
  };

  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : null };

  return (
    <React.Fragment>
      {<Timeline currentStep={4} />}

      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={() => onSelect()}
        t={t}
        // isDisabled={
        //   !bankDetails.accountNumber ||
        //   !bankDetails.confirmAccountNumber ||
        //   !bankDetails.ifscCode ||
        //   !bankDetails.bankName ||
        //   !bankDetails.bankBranchName ||
        //   !bankDetails.accountHolderName
        // }
      >
        {[
          { label: "SV_ACCOUNT_NUMBER", field: "accountNumber", maxLength: 16, regex: /\D/g },
          { label: "SV_CONFIRM_ACCOUNT_NUMBER", field: "confirmAccountNumber", maxLength: 16, regex: /\D/g },
          { label: "SV_IFSC_CODE", field: "ifscCode", maxLength: 11, regex: /[^a-zA-Z0-9]/g },
          { label: "SV_BANK_NAME", field: "bankName", disabled: true },
          { label: "SV_BANK_BRANCH_NAME", field: "bankBranchName", disabled: true },
          { label: "SV_ACCOUNT_HOLDER_NAME", field: "accountHolderName", regex: /[^a-zA-Z\s]/g },
        ].map(({ label, field, maxLength, regex, disabled }) => (
          <div key={field}>
            <CardLabel>
              {t(label)}
            </CardLabel>
            <TextInput
              t={t}
              type="text"
              name={field}
              value={bankDetails[field]}
              placeholder={t(`Enter ${label}`)}
              onChange={(e) => handleInputChange(field, e.target.value, maxLength, regex)}
              style={inputStyles}
              disabled={disabled || false}
              {...(field === "accountHolderName"
                ? { pattern: "^[a-zA-Z ]+$", title: t("SV_INVALID_ACCOUNT_HOLDER_NAME") }
                : { pattern: maxLength === 11 ? "[a-zA-Z0-9]{11}" : "[0-9]{8,16}", title: t(`SV_INVALID_${label}`) })}
            />
          </div>
        ))}
      </FormStep>
      {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          onClose={() => setShowToast(null)}
        />
      )}
    </React.Fragment>
  );
};

export default SVBankDetails;
