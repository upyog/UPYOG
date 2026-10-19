import { formatDate } from "./utils";

const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

/**
 * getChbAcknowledgementData Function
 * 
 * This function is responsible for generating the acknowledgement data for a CHB application.
 * It processes the application details, tenant information, and booking slot details to create a structured object for the acknowledgement screen.
 * 
 * Parameters:
 * - `application`: The application object containing details such as documents, booking slots, and other metadata.
 * - `tenantInfo`: The tenant information object containing details like tenant code.
 * - `t`: The translation function used for internationalization.
 * 
 * Logic:
 * - Formats slot details into a structured table (Venue Name, Venue Codes, Booking Date, Booking Time, Status) with dates formatted as DD-MM-YYYY.
 * 
 * Returns:
 * - An object containing:
 *    - `t`: The translation function.
 *    - `tenantId`: The tenant code from the `tenantInfo` object.
 *    - Structured sections (applicant details, slot details table, event details, bank details, address details) for the acknowledgement PDF.
 */
const getChbAcknowledgementData = async (application, tenantInfo, t) => {
  // Format slot details into tabular rows: [Venue Name, Venue Code / Hall Code, Booking Date, Booking Time, Status]
  const venueName = application?.venueCode;
  const slotlistRows = application?.bookingSlotDetails?.map((slot) => [
    t(venueName),
    t(slot?.unitCode),
    formatDate(slot?.bookingDate),
    slot?.bookingFromTime && slot?.bookingToTime ? `${slot.bookingFromTime} - ${slot.bookingToTime}` : t("CS_NA"),
    t(slot?.status)
  ]) || [];

  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    applicationNumber: application?.bookingNo,
    phoneNumber: tenantInfo?.contactNumber,
    heading: t("CHB_ACKNOWLEDGEMENT"),
    details: [
      {
        title: t("CHB_APPLICANT_DETAILS"),
        values: [
          { title: t("CHB_APPLICANT_NAME"), value: application?.applicantDetail?.applicantName },
          { title: t("CHB_MOBILE_NUMBER"), value: application?.applicantDetail?.applicantMobileNo },
          { title: t("CHB_ALT_MOBILE_NUMBER"), value: application?.applicantDetail?.applicantAlternateMobileNo || t("CS_NA") },
          { title: t("CHB_EMAIL_ID"), value: application?.applicantDetail?.applicantEmailId },
        ],
      },
      {
        title: t("SLOT_DETAILS"),
        isTable: true,
        headers: [
          t("CHB_VENUE_NAME_LABEL"),
          t("CHB_HALL_CODE_LABEL"),
          t("CHB_BOOKING_DATE"),
          t("CHB_BOOKING_TIME"),
          t("PT_COMMON_TABLE_COL_STATUS_LABEL")
        ],
        widths: ['22%', '22%', '18%', '18%', '20%'],
        tableRows: slotlistRows,
      }, 
      {
        title: t("CHB_EVENT_DETAILS"),
        values: [
          { title: t("CHB_SPECIAL_CATEGORY"), value: application?.specialCategory?.category },
          { title: t("CHB_PURPOSE"), value: application?.purpose?.purpose },
          { title: t("CHB_PURPOSE_DESCRIPTION"), value: application?.purposeDescription},
        ],
      },
      {
        title: t("CHB_BANK_DETAILS"),
        values: [
          { title: t("CHB_ACCOUNT_NUMBER"), value: application?.applicantDetail?.accountNumber },
          { title: t("CHB_IFSC_CODE"), value: application?.applicantDetail?.ifscCode},
          { title: t("CHB_BANK_NAME"), value: application?.applicantDetail?.bankName},
          { title: t("CHB_BANK_BRANCH_NAME"), value: application?.applicantDetail?.bankBranchName},
          { title: t("CHB_ACCOUNT_HOLDER_NAME"), value: application?.applicantDetail?.accountHolderName},
        ],
      },
      {
        title: t("CHB_ADDRESS_DETAILS"),
        values: [
          { title: t("CHB_PINCODE"), value: application?.address?.pincode || t("CS_NA") },
          { title: t("CHB_CITY"), value: application?.address?.city || t("CS_NA") },
          { title: t("CHB_LOCALITY"), value: application?.address?.locality || t("CS_NA") },
          { title: t("CHB_STREET_NAME"), value: application?.address?.streetName || t("CS_NA") },
          { title: t("CHB_HOUSE_NO"), value: application?.address?.houseNo || t("CS_NA") },
          { title: t("CHB_LANDMARK"), value: application?.address?.landmark || t("CS_NA") },
        ],
      }, 
    ],
  };
};

export default getChbAcknowledgementData;