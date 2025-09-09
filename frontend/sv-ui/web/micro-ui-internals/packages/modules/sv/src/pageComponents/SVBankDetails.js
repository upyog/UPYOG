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

const SVBankDetails = ({ t, config, onSelect, userType, formData, editdata,previousData }) => {
  const [bankDetails, setBankDetails] = useState({
    accountNumber: formData?.bankdetails?.accountNumber || previousData?.bankDetail?.accountNumber||editdata?.bankDetail?.accountNumber|| "",
    confirmAccountNumber: formData?.bankdetails?.confirmAccountNumber || previousData?.bankDetail?.accountNumber||editdata?.bankDetail?.accountNumber|| "",
    ifscCode: formData?.bankdetails?.ifscCode || previousData?.bankDetail?.ifscCode||editdata?.bankDetail?.ifscCode|| "",
    bankName: formData?.bankdetails?.bankName || previousData?.bankDetail?.bankName||editdata?.bankDetail?.bankName|| "",
    bankBranchName: formData?.bankdetails?.bankBranchName || previousData?.bankDetail?.bankBranchName||editdata?.bankDetail?.bankBranchName|| "",
    accountHolderName: formData?.bankdetails?.accountHolderName || previousData?.bankDetail?.accountHolderName || editdata?.bankDetail?.accountHolderName || "",
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
      if (bankDetails.accountNumber && bankDetails.confirmAccountNumber && bankDetails.accountNumber === bankDetails.confirmAccountNumber) {
        setShowToast({ error: false, label: t("SV_ACCOUNT_NUMBER_MATCHES") });
      }
    }, [bankDetails.accountNumber, bankDetails.confirmAccountNumber, t]);

  useEffect(() => {
    if (bankDetails.ifscCode.length === 11) {
      fetchBankDetails();
    }
  }, [bankDetails.ifscCode, fetchBankDetails]);

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => setShowToast(null), 2000);
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

  //Custom function fo rthe payload whic we can use while goint to next

  const handleSaveasDraft=()=>{
    let vendordetails = [];
    let tenantId=Digit.ULBService.getCitizenCurrentTenant(true);
  const createVendorObject = (formData) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: formData?.owner?.units?.[0]?.vendorDateOfBirth,
    userCategory:formData?.owner?.units?.[0]?.userCategory?.code,
    emailId: formData?.owner?.units?.[0]?.email,
    fatherName: formData?.owner?.units?.[0]?.fatherName,
    gender: formData?.owner?.units?.[0]?.gender?.code.charAt(0),
    id: "",
    mobileNo: formData?.owner?.units?.[0]?.mobileNumber,
    name: formData?.owner?.units?.[0]?.vendorName,
    relationshipType: "VENDOR",
    vendorId: null
  });

  const createSpouseObject = (formData) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: formData?.owner?.units?.[0]?.spouseDateBirth,
    userCategory:formData?.owner?.units?.[0]?.userCategory?.code,
    emailId: "",
    isInvolved: formData?.owner?.spouseDependentChecked,
    fatherName: "",
    gender: "O",
    id: "",
    mobileNo: "",
    name: formData?.owner?.units?.[0]?.spouseName,
    relationshipType: "SPOUSE",
    vendorId: null
  });

  const createDependentObject = (formData) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: formData?.owner?.units?.[0]?.dependentDateBirth,
    userCategory:formData?.owner?.units?.[0]?.userCategory?.code,
    emailId: "",
    isInvolved: formData?.owner?.dependentNameChecked,
    fatherName: "",
    gender: formData?.owner?.units?.[0]?.dependentGender?.code.charAt(0),
    id: "",
    mobileNo: "",
    name: formData?.owner?.units?.[0]?.dependentName,
    relationshipType: "DEPENDENT",
    vendorId: null
  });

  // Helper function to check if a string is empty or undefined
  const isEmpty = (str) => !str || str.trim() === '';

  // Main logic
  if (!isEmpty(formData?.owner?.units?.[0]?.vendorName)) {
    const spouseName = formData?.owner?.units?.[0]?.spouseName;
    const dependentName = formData?.owner?.units?.[0]?.dependentName;

    if (isEmpty(spouseName) && isEmpty(dependentName)) {
      // Case 1: Only vendor exists
      vendordetails = [createVendorObject(formData)];
    } else if (!isEmpty(spouseName) && isEmpty(dependentName)) {
      // Case 2: Both vendor and spouse exist
      vendordetails = [
        createVendorObject(formData),
        createSpouseObject(formData)
      ];
    } else if (!isEmpty(spouseName) && !isEmpty(dependentName)) {
      // Case 3: All three exist (vendor, spouse, and dependent)
      vendordetails = [
        createVendorObject(formData),
        createSpouseObject(formData),
        createDependentObject(formData)
      ];
    }
  }

  const daysOfOperations = formData?.businessDetails?.daysOfOperation;
  const vendingOperationTimeDetails = daysOfOperations
  .filter(day => day.isSelected) // Filter only selected days
  .map(day => ({
    applicationId: "", // Add actual applicationId if available
    auditDetails: {
      createdBy: "", // Adjust these fields based on your data
      createdTime: 0, 
      lastModifiedBy: "",
      lastModifiedTime: 0,
    },
    dayOfWeek: day.name.toUpperCase(),
    fromTime: day.startTime,
    toTime: day.endTime,
    id: ""
  }));

  const api_response = sessionStorage.getItem("Response");
  const response = JSON.parse(api_response);

    let streetVendingDetail= {
      addressDetails: [
        {
          addressId: "",
          addressLine1: formData?.address?.addressline1,
          addressLine2: formData?.address?.addressline2,
          addressType: "",
          city: formData?.address?.city?.name,
          cityCode: formData?.address?.city?.code,
          doorNo: "",
          houseNo: formData?.address?.houseNo,
          landmark: formData?.address?.landmark,
          locality: formData?.address?.locality?.i18nKey,
          localityCode: formData?.address?.locality?.code,
          pincode: formData?.address?.pincode,
          streetName: "",
          vendorId: ""
        },
        { // sending correspondence address here
          addressId: "",
          addressLine1: formData?.correspondenceAddress?.caddressline1,
          addressLine2: formData?.correspondenceAddress?.caddressline2,
          addressType: "",
          city: formData?.correspondenceAddress?.ccity?.name,
          cityCode: formData?.correspondenceAddress?.ccity?.code,
          doorNo: "",
          houseNo: formData?.correspondenceAddress?.chouseNo,
          landmark: formData?.correspondenceAddress?.clandmark,
          locality: formData?.correspondenceAddress?.clocality?.i18nKey,
          localityCode: formData?.correspondenceAddress?.clocality?.code,
          pincode: formData?.correspondenceAddress?.cpincode,
          streetName: "",
          vendorId: "",
          isAddressSame: formData?.correspondenceAddress?.isAddressSame
        }
      ],
      applicationDate: 0,
      applicationId: "",
      applicationNo: "",
      applicationStatus: "",
      approvalDate: 0,
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
      bankDetail: {
        accountHolderName: bankDetails?.accountHolderName,
        accountNumber: bankDetails?.accountNumber,
        applicationId: "",
        bankBranchName: bankDetails?.bankBranchName,
        bankName: bankDetails?.bankName,
        id: "",
        ifscCode: bankDetails?.ifscCode,
        refundStatus: "",
        refundType: "",
        auditDetails: {
          createdBy: "",
          createdTime: 0,
          lastModifiedBy: "",
          lastModifiedTime: 0
        },
      },
      benificiaryOfSocialSchemes: [],
      applicationCreatedBy: formData?.owner?.applicationCreatedBy,
      locality: formData?.businessDetails?.vendorLocality?.code || "",
      localityValue: "",
      vendingZoneValue: "",
      vendorPaymentFrequency: formData?.businessDetails?.vendingPayment?.code,
      enrollmentId:"",
      cartLatitude: 0,
      cartLongitude: 0,
      certificateNo: null,
      disabilityStatus: "",
      draftId: previousData?.draftId||response?.SVDetail?.draftId,
      documentDetails: [
        {
          applicationId: "",
          auditDetails: {
            createdBy: "",
            createdTime: 0,
            lastModifiedBy: "",
            lastModifiedTime: 0
          },
          documentDetailId: "",
          documentType: "",
          fileStoreId: ""
        }
      ],
      localAuthorityName: formData?.businessDetails?.nameOfAuthority,
      tenantId: tenantId,
      termsAndCondition: "Y",
      tradeLicenseNo: formData?.owner?.units?.[0]?.tradeNumber,
      vendingActivity: formData?.businessDetails?.vendingType?.code,
      vendingArea: formData?.businessDetails?.areaRequired||"0",
      vendingLicenseCertificateId: "",
      vendingOperationTimeDetails,
      vendingZone:  formData?.businessDetails?.vendingZones?.code,
      vendorDetail: [
        ...vendordetails
      ],
      workflow: {
        action: "APPLY",
        comments: "",
        businessService: "street-vending",
        moduleName: "sv-services",
        businessService: "street-vending",
        moduleName: "sv-services",
        varificationDocuments: [
          {
            additionalDetails: {},
            auditDetails: {
              createdBy: "",
              createdTime: 0,
              lastModifiedBy: "",
              lastModifiedTime: 0
            },
            documentType: "",
            documentUid: "",
            fileStoreId: "",
            id: ""
          }
        ]
      }
    };

    Digit.SVService.create({streetVendingDetail, draftApplication:true},tenantId)
    .then(response=>{
      sessionStorage.setItem("Response",JSON.stringify(response));
    })
    .catch(error=>{
      console.log("Something Went Wrong",error);
    })

  };

  const goNext = () => {
    if (bankDetails.accountNumber !== bankDetails.confirmAccountNumber) {
        setShowToast({ error: true, label: t("SV_INVALID_ACCOUNTNUMBER") });
        return;
      }
    onSelect(config.key, { ...formData.bankdetails, ...bankDetails }, false);
    window.location.href.includes("edit")?null: handleSaveasDraft();
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
