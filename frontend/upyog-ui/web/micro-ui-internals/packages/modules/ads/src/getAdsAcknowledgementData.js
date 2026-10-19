import { formatDate } from "./utils";

const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

/**
 * getAdsAcknowledgementData Function
 * 
 * Generates the structured acknowledgement data for an Advertisement booking application.
 * Formats applicant details, advertisement cart details in a tabular format, and address details.
 * 
 * Parameters:
 * - application: The advertisement booking application object
 * - tenantInfo: ULB / tenant information object
 * - t: Translation function
 */
const getAdsAcknowledgementData = async (application, tenantInfo, t) => {
  // Format cart details into tabular rows: [Type, Location, Face Area, Night Light, Booking Date, Status]
  const cartRows = application?.cartDetails?.map((slot) => [
    t(slot?.addType) || t("CS_NA"),
    t(slot?.location) || t("CS_NA"),
    t(slot?.faceArea) || t("CS_NA"),
    slot?.nightLight ? t("CS_COMMON_YES") : t("CS_COMMON_NO"),
    formatDate(slot?.bookingDate) || t("CS_NA"),
    t(slot?.status) || t("CS_NA")
  ]) || [];

  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${tenantInfo?.i18nKey ? t(tenantInfo?.i18nKey) : (tenantInfo?.name || tenantInfo?.code || "")} ${tenantInfo?.city?.ulbGrade ? ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade?.toUpperCase().replace(" ", "_").replace(".", "_")}`)) : ""}`.trim(),
    email: tenantInfo?.emailId,
    applicationNumber: application?.bookingNo,
    phoneNumber: tenantInfo?.contactNumber,
    heading: t("ADS_ACKNOWLEDGEMENT"),
    details: [
      {
        title: t("ADS_APPLICANT_DETAILS"),
        values: [
          { title: t("ADS_APPLICANT_NAME"), value: application?.applicantDetail?.applicantName },
          { title: t("ADS_MOBILE_NUMBER"), value: application?.applicantDetail?.applicantMobileNo },
          { title: t("ADS_ALT_MOBILE_NUMBER"), value: application?.applicantDetail?.applicantAlternateMobileNo || t("CS_NA") },
          { title: t("ADS_EMAIL_ID"), value: application?.applicantDetail?.applicantEmailId },
        ],
      },
      {
        title: t("ADS_CART_DETAILS"),
        isTable: true,
        headers: [
          t("ADS_TYPE"),
          t("ADS_LOCATION"),
          t("ADS_FACE_AREA"),
          t("ADS_NIGHT_LIGHT"),
          t("ADS_BOOKING_DATE"),
          t("PT_COMMON_TABLE_COL_STATUS_LABEL")
        ],
        widths: ['16%', '18%', '20%', '14%', '16%', '16%'],
        tableRows: cartRows,
      },
      {
        title: t("ADS_ADDRESS_DETAILS"),
        values: [
          { title: t("ADS_HOUSE_NO"), value: application?.address?.houseNo || t("CS_NA") },
          { title: t("ADS_STREET_NAME"), value: application?.address?.streetName || t("CS_NA") },
          { title: t("ADS_ADDRESS_LINE1"), value: application?.address?.addressLine1 || t("CS_NA") },
          { title: t("ADS_ADDRESS_LINE2"), value: application?.address?.addressLine2 || t("CS_NA") },
          { title: t("ADS_LANDMARK"), value: application?.address?.landmark  || t("CS_NA") },
          { title: t("ADS_CITY"), value: application?.address?.city || t("CS_NA") },
          { title: t("ADS_LOCALITY"), value: application?.address?.locality || t("CS_NA") },
          { title: t("ADS_ADDRESS_PINCODE"), value: application?.address?.pincode || t("CS_NA") },
        ],
      },
    ],
  };
};

export default getAdsAcknowledgementData;
