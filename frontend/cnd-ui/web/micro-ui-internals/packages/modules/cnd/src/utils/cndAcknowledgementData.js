const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

const cndAcknowledgementData = async (application, tenantInfo, t) => { 

  // function to filter out the fields which have values
  const filterEmptyValues = (values) => values.filter(item => item.value);

  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    applicationNumber: application?.applicationNumber,
    isTOCRequired: false,
    heading: t("C&D_ACKNOWLEDGEMENT"),
    details: [

      {
        title: t("COMMON_CND_DETAILS"),
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: t("CND_APPLICATION_NUMBER"), value: application?.applicationNumber },
          { title: t("CND_REQUEST_TYPE"), value: t(application?.applicationType) },
          { title: t("CND_PROPERTY_USAGE"), value: t(application?.propertyType) },
          { title: t("CND_TYPE_CONSTRUCTION"), value: t(application?.typeOfConstruction) },
          { title: t("CND_WASTE_QUANTITY"), value: application?.totalWasteQuantity + " Ton"},
          { title: t("CND_SCHEDULE_PICKUP"), value: application?.requestedPickupDate },
          { title: t("CND_TIME_CONSTRUCTION"), value: application?.constructionFromDate + " to " + application?.constructionToDate},
        ]),
      },

      {
        title: t("COMMON_PERSONAL_DETAILS"),
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: t("COMMON_APPLICANT_NAME"), value: application?.applicantDetail?.nameOfApplicant },
          { title: t("COMMON_MOBILE_NUMBER"),value: application?.applicantDetail?.mobileNumber },
          { title: t("COMMON_EMAIL_ID"),value: application?.applicantDetail?.emailId},
          { title: t("COMMON_ALT_MOBILE_NUMBER"),value: application?.applicantDetail?.alternateMobileNumber},
        ]),
      },

      {
        title: t("CND_WASTE_PICKUP_ADDRESS"),
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: t("ADDRESS_LINE1"), value: application?.addressDetail?.addressLine1 },
          { title: t("ADDRESS_LINE2"),value: application?.addressDetail?.addressLine2 },
          { title: t("CITY"),value: application?.addressDetail?.city},
          { title: t("LOCALITY"),value: application?.addressDetail?.locality},  
          { title: t("LANDMARK"),value: application?.addressDetail?.landmark},
          { title: t("PINCODE"),value: application?.addressDetail?.pinCode},  
        ]),
      },
      {
        tableData: {
          title: t("CND_WASTE_DETAILS"),
          rows: application.wasteTypeDetails,
        },
      }
    ],
  };
};

export default cndAcknowledgementData;