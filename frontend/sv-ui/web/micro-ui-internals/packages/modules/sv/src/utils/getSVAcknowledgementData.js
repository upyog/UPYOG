const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

const getSVAcknowledgementData = async (application, tenantInfo, t) => {
  const filesArray = application?.documents?.map((value) => value?.fileStoreId);
  const res = filesArray?.length > 0 && (await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()));

  // function to filter out the fields which have values
  const filterEmptyValues = (values) => values.filter(item => item.value);

  // Map gender codes to human-readable strings for vendor and dependent
  let gender, dgender;
  if (application?.vendorDetail[0]?.gender) {
    gender = application?.vendorDetail[0]?.gender == "M" ? "Male" : application?.vendorDetail[0]?.gender == "F" ? "Female" : "Transgender";
  }
  if (application?.vendorDetail[0]?.dependentGender) {
    dgender = application?.vendorDetail[0]?.dependentGender == "M" ? "Male" : application?.vendorDetail[0]?.gender == "F" ? "Female" : "Transgender";
  }

  // Prepare and return the acknowledgement data object
  // - Includes translation function, tenant info, and application details
  // - Each section (personal, business, bank, address) is grouped with a header and filtered for non-empty values
  // - Additional details for social schemes are included if present
  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    applicationNumber: application?.applicationNo,
    isTOCRequired: false,
    heading: t("SV_ACKNOWLEDGEMENT"),
    details: [

      {
        title: t("SV_VENDOR_PERSONAL_DETAILS"),
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: t("SV_VENDOR_NAME"), value: application?.vendorDetail[0]?.name },
          { title: t("SV_FATHER_NAME"), value: application?.vendorDetail[0]?.fatherName },
          { title: t("SV_REGISTERED_MOB_NUMBER"), value: application?.vendorDetail[0]?.mobileNo },
          { title: t("SV_EMAIL"), value: application?.vendorDetail[0]?.emailId },
          { title: t("SV_DATE_OF_BIRTH"), value: application?.vendorDetail[0]?.dob },
          { title: t("SV_GENDER"), value: gender },
          { title: t("SV_SPOUSE_NAME"), value: application?.vendorDetail[0]?.spouseName },
          { title: t("SV_SPOUSE_DATE_OF_BIRTH"), value: application?.vendorDetail[0]?.spouseDob },
          { title: t("SV_DEPENDENT_NAME"), value: application?.vendorDetail[0]?.dependentName },
          { title: t("SV_DEPENDENT_DATE_OF_BIRTH"), value: application?.vendorDetail[0]?.dependentDob },
          { title: t("SV_DEPENDENT_GENDER"), value: dgender },
          { title: t("SV_TRADE_NUMBER"), value: application?.vendorDetail[0]?.tradeNumber },
        ]),
      },

      {
        title: t("SV_VENDOR_BUSINESS_DETAILS"),
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: t("SV_VENDING_TYPE"), value: application?.vendingActivity },
          { title: t("SV_VENDING_LOCALITY"), value: application?.localityValue },
          { title: t("SV_VENDING_ZONES"), value: application?.vendingZoneValue },
          { title: t("SV_AREA_REQUIRED"), value: application?.vendingArea },
          { title: t("SV_LOCAL_AUTHORITY_NAME"), value: application?.localAuthorityName },
          { title: t("SV_VENDING_LISCENCE"), value: application?.vendingLicenseCertificateId },
          { title: t("SV_VENDING_PAYMENT"), value: application?.vendorPaymentFrequency },
          { title: t("SV_CATEGORY"), value: application?.disabilityStatus },
        ]),
      },

      {
        title: t("SV_BANK_DETAILS"),
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: t("SV_ACCOUNT_NUMBER"), value: application?.bankDetail?.accountNumber },
          { title: t("SV_IFSC_CODE"), value: application?.bankDetail?.ifscCode },
          { title: t("SV_BANK_NAME"), value: application?.bankDetail?.bankName },
          { title: t("SV_BANK_BRANCH_NAME"), value: application?.bankDetail?.bankBranchName },
          { title: t("SV_ACCOUNT_HOLDER_NAME"), value: application?.bankDetail?.accountHolderName },

        ]),
      },

      {
        title: t("SV_ADDRESS_DETAILS"),
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: t("SV_ADDRESS_LINE1"), value: application?.addressDetails[0]?.addressLine1 },
          { title: t("SV_ADDRESS_LINE2"), value: application?.addressDetails[0]?.addressLine2 },
          { title: t("SV_CITY"), value: application?.addressDetails[0]?.city },
          { title: t("SV_LOCALITY"), value: application?.addressDetails[0]?.locality },
          { title: t("SV_ADDRESS_PINCODE"), value: application?.addressDetails[0]?.pincode },
          { title: t("SV_LANDMARK"), value: application?.addressDetails[0]?.landmark },
        ]),
      },
      // Add additional details section if beneficiary social schemes exist
      ...(application?.benificiaryOfSocialSchemes.length > 0 ? [{
        title: t("SV_ADDITIONAL_DETAILS"),
        asSectionHeader: true,
        values: application?.benificiaryOfSocialSchemes.flatMap((item, index) => [
          { title: t("SV_BENEFICIARY_SCHEMES"), value: item?.schemeName || t("SV_NOT_AVAILABLE") },
          { title: t("SV_ENROLLMENT_APPLICATION_NUMBER"), value: item?.enrollmentId || t("SV_NOT_AVAILABLE") }
        ])
      }] : []),
    ],
  };
};

export default getSVAcknowledgementData;