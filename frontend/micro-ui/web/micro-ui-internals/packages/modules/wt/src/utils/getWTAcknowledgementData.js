import { convertTo12HourFormat,formatDate } from "./index";
const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

/**
 * Generates the acknowledgement data for a water tanker booking.
 * 
 * @param {Object} booking - The booking details object containing applicant and address information.
 * @param {Object} tenantInfo - The tenant information object containing details like tenant code, email, and contact number.
 * @param {Function} t - The translation function used for internationalization.
 * 
 * @returns {Object} - An object containing formatted acknowledgement data including applicant details, address details, and other metadata.
 */
const getWTAcknowledgementData = async (booking, tenantInfo, t) => {
    console.log("booking", booking);
    console.log("tenantInfo", tenantInfo);
  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    applicationNumber: booking?.bookingNo,
    isTOCRequired: false,
    heading: t("WT_ACKNOWLEDGEMENT"),
    details: [
      {
        title: t("WT_APPLICANT_DETAILS"),
        asSectionHeader: true,
        values: [
          { title: t("WT_APPLICANT_NAME"), value: booking?.applicantDetail?.name },
          { title: t("WT_MOBILE_NUMBER"), value: booking?.applicantDetail?.mobileNumber },
          { title: t("WT_ALT_MOBILE_NUMBER"), value: booking?.applicantDetail?.alternateNumber },
          { title: t("WT_EMAIL_ID"), value: booking?.applicantDetail?.emailId },
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
        title: t("WT_REQUEST_DETAILS"),
        asSectionHeader: true,
        values: [
          { title: t("WT_TANKER_TYPE"), value: booking?.tankerType },
          { title: t("WT_WATER_TYPE"), value: t(booking?.waterType) },
          { title: t("WT_TANKER_QUANTITY"), value: booking?.tankerQuantity },
          { title: t("WT_WATER_QUANTITY"), value: booking?.waterQuantity + " Ltr" },
          { 
            title: t("WT_DELIVERY_DATE_TIME"), 
            value: `${formatDate(booking?.deliveryDate)} (${convertTo12HourFormat(booking?.deliveryTime)})` 
          },
          { title: t("WT_DESCRIPTION"), value: booking?.description },
          { title: t("WT_IMMEDIATE"), value: booking?.extraCharge === "Y" ? t("YES") : t("NO") },
        ].filter(item => item.value),
      },
    ],
  };
};

export default getWTAcknowledgementData;
