// Utility function to capitalize the first letter of a string
const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);

// Utility function to convert a ULB (Urban Local Body) name to camel case
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

// Function to generate acknowledgment data for an E-Waste application
const getEwAcknowledgementData = async (application, tenantInfo, t, response) => {
  // Fetching file details for the documents associated with the application
  const filesArray = application?.documents?.map((value) => value?.fileStoreId);
  const res = filesArray?.length > 0 && (await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()));

  // Returning the acknowledgment data object
  return {
    t: t, // Translation function
    tenantId: tenantInfo?.code, // Tenant ID
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`, // Tenant name with ULB grade
    email: tenantInfo?.emailId, // Tenant email ID
    applicationNumber: application?.requestId, // Application request ID
    phoneNumber: tenantInfo?.contactNumber, // Tenant contact number
    heading: t("ACKNOWLEDGEMENT"), // Heading for the acknowledgment
    details: [
      {
        title: t("EW_APPLICANT_DETAILS"), // Section title for applicant details
        values: [
          { title: t("EW_APPLICANT_NAME"), value: application?.applicant?.applicantName }, // Applicant name
          { title: t("EW_MOBILE_NUMBER"), value: application?.applicant?.mobileNumber }, // Applicant mobile number
          { title: t("EWASTE_EMAIL_ID"), value: application?.applicant?.emailId }, // Applicant email ID
        ],
      },
      {
        title: t("EW_ADDRESS_DETAILS"), // Section title for address details
        values: [
          { title: t("EW_PINCODE"), value: application?.address?.pincode }, // Pincode
          { title: t("EW_CITY"), value: application?.address?.city }, // City
          { title: t("EW_DOOR_NO"), value: application?.address?.doorNo }, // Door number
          { title: t("EW_STREET"), value: application?.address?.street }, // Street
          { title: t("EW_ADDRESS_LINE_1"), value: application?.address?.addressLine1 }, // Address line 1
          { title: t("EW_ADDRESS_LINE_2"), value: application?.address?.addressLine2 }, // Address line 2
          { title: t("EW_BUILDING_NAME"), value: application?.address?.buildingName }, // Building name
        ],
      },
      {
        tableData: {
          title: t("EW_PRODUCT_DETAILS"), // Section title for product details
          rows: application.ewasteDetails, // Product details rows
        },
      },
      {
        title: t("ES_EW_ACTION_FINALAMOUNT"), // Section title for final amount
        values: [
          { title: t("EWASTE_NET_PRICE"), value: "₹ " + application?.calculatedAmount }, // Net price
        ],
      },
    ],
  };
};

export default getEwAcknowledgementData; // Exporting the function