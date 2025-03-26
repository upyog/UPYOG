import { convertTo12HourFormat,formatDate } from "./index";
const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

/**
 * Generates the acknowledgement data for a mobile toilet booking.
 * 
 * @param {Object} booking - The booking details object containing applicant and address information.
 * @param {Object} tenantInfo - The tenant information object containing details like tenant code, email, and contact number.
 * @param {Function} t - The translation function used for internationalization.
 * 
 * @returns {Object} - An object containing formatted acknowledgement data including applicant details, address details, 
 *                     request details, and other metadata.
 */
const getMTAcknowledgementData = async (booking, tenantInfo, t) => {
  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    applicationNumber: booking?.bookingNo,
    isTOCRequired: false,
    heading: t("MT_ACKNOWLEDGEMENT"),
    details: [
      {
        title: t("MT_APPLICANT_DETAILS"),
        asSectionHeader: true,
        values: [
          { title: t("MT_APPLICANT_NAME"), value: booking?.applicantDetail?.name },
          { title: t("MT_MOBILE_NUMBER"), value: booking?.applicantDetail?.mobileNumber },
          { title: t("MT_ALT_MOBILE_NUMBER"), value: booking?.applicantDetail?.alternateNumber },
          { title: t("MT_EMAIL_ID"), value: booking?.applicantDetail?.emailId },
        ].filter(item => item.value),
      },
      {
        title: t("WT_ADDRESS_DETAILS"),
        asSectionHeader: true,
        values: [
          { title: t("WT_PINCODE"), value: booking?.address?.pincode },
          { title: t("WT_CITY"), value: booking?.address?.city },
          { title: t("WT_LOCALITY"), value: booking?.address?.locality },
          { title: t("WT_STREET_NAME"), value: booking?.address?.streetName },
          { title: t("WT_DOOR_NO"), value: booking?.address?.doorNo },
          { title: t("WT_HOUSE_NO"), value: booking?.address?.houseNo },
          { title: t("WT_ADDRESS_LINE1"), value: booking?.address?.addressLine1 },
          { title: t("WT_ADDRESS_LINE2"), value: booking?.address?.addressLine2 },
          { title: t("WT_LANDMARK"), value: booking?.address?.landmark },
        ].filter(item => item.value),
      },
      {
        title: t("MT_REQUEST_DETAILS"),
        asSectionHeader: true,
        values: [
          { title: t("MT_NUMBER_OF_MOBILE_TOILETS"), value: booking?.noOfMobileToilet },
          { title: t("MT_DELIVERY_FROM_DATE"), value: formatDate(booking?.deliveryFromDate) },
          { title: t("MT_DELIVERY_TO_DATE"), value: formatDate(booking?.deliveryToDate) },
          { title: t("MT_REQUIREMENT_TIME"), value: `${convertTo12HourFormat(booking?.deliveryFromTime)} - ${convertTo12HourFormat(booking?.deliveryToTime)}` },
          { title: t("MT_SPECIAL_REQUEST"), value: booking?.description }
        ].filter(item => item.value),
      },
    ],
  };
};

export default getMTAcknowledgementData;
