const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

const getChbAcknowledgementData = async (application, tenantInfo, t) => {
  const filesArray = application?.documents?.map((value) => value?.fileStoreId);
  const res = filesArray?.length > 0 && (await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()));
    const getBookingDateRange = (bookingSlotDetails) => {
      if (!bookingSlotDetails || bookingSlotDetails.length === 0) {
        return t("CS_NA");
      }
      const startDate = bookingSlotDetails[0]?.bookingDate;
      const endDate = bookingSlotDetails[bookingSlotDetails.length - 1]?.bookingDate;
      if (startDate === endDate) {
        return startDate; // Return only the start date
      } else {
        // Format date range as needed, for example: "startDate - endDate"
        return startDate && endDate ? `${endDate} - ${startDate} ` : t("CS_NA");
      }
    };
   const getBookingTimeRange = (bookingSlotDetails) => {
      if (!bookingSlotDetails || bookingSlotDetails.length === 0) {
        return "10:00 - 11:59"; // Default time range if details are not present
      }
      const startTime = bookingSlotDetails[0]?.bookingFromTime;
      // const endTime = bookingSlotDetails[bookingSlotDetails.length - 1]?.bookingToTime;
      const length = bookingSlotDetails.length;
      let defaultEndTime = "23:59"; // Default end time for length 1
      if (length === 2) {
        defaultEndTime = "47:59"; // End time for length 2
      } else if (length === 3) {
        defaultEndTime = "71:59"; // End time for length 3
      }
      // Return formatted time range
      return startTime ? `${startTime} - ${defaultEndTime}` : t("CS_NA");
    };
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
          { title: t("CHB_ALT_MOBILE_NUMBER"), value: application?.applicantDetail?.applicantAlternateMobileNo},
          { title: t("CHB_EMAIL_ID"), value: application?.applicantDetail?.applicantEmailId },
        ],
      },
      {
        title: t("SLOT_DETAILS"),
        values: [
          { title: t("CHB_COMMUNITY_HALL_NAME"), value: application?.communityHallCode},
          { title: t("CHB_BOOKING_DATE"), value: getBookingDateRange(application?.bookingSlotDetails) },
          { title: t("CHB_BOOKING_TIME"), value: getBookingTimeRange(application?.bookingSlotDetails)}
        ],
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
          { title: t("CHB_PINCODE"), value: application?.address?.pincode },
          { title: t("CHB_CITY"), value: application?.address?.city },
          { title: t("CHB_LOCALITY"), value: application?.address?.locality },
          { title: t("CHB_STREET_NAME"), value: application?.address?.streetName },
          { title: t("CHB_HOUSE_NO"), value: application?.address?.houseNo },
          { title: t("CHB_LANDMARK"), value: application?.address?.landmark },
        ],
      }, 
    ],
  };
};

export default getChbAcknowledgementData;