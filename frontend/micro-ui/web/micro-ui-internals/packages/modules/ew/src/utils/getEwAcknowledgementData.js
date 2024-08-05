import { values } from "lodash";

const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

// const getAssessmentInfo = (application, t) => {
//   console.log("apppllllll", application);
//   let values = [];

//   return {
//     title: t("ES_TITILE_PET_DETAILS"),
//     values: values,
//   };
// };

const getEwAcknowledgementData = async (application, tenantInfo, t, response) => {
  console.log("getget", application);
  const filesArray = application?.documents?.map((value) => value?.fileStoreId);
  const res = filesArray?.length > 0 && (await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()));

  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    applicationNumber: application?.requestId,
    phoneNumber: tenantInfo?.contactNumber,
    heading: t("EW_ACKNOWLEDGEMENT"),
    details: [
      {
        title: t("EW_APPLICANT_DETAILS"),
        values: [
          { title: t("EW_APPLICANT_NAME"), value: application?.applicant?.applicantName },
          { title: t("EW_MOBILE_NUMBER"), value: application?.applicant?.mobileNumber },
          { title: t("EWASTE_EMAIL_ID"), value: application?.applicant?.emailId },
        ],
      },

      // getAssessmentInfo(application, t),
      {
        title: t("EW_ADDRESS_DETAILS"),
        values: [
          { title: t("EW_PINCODE"), value: application?.address?.pincode },
          { title: t("EW_CITY"), value: application?.address?.city },
          { title: t("EW_DOOR_NO"), value: application?.address?.doorNo },
          { title: t("EW_STREET"), value: application?.address?.street },
          { title: t("EW_ADDRESS_LINE_1"), value: application?.address?.addressLine1 },
          { title: t("EW_ADDRESS_LINE_2"), value: application?.address?.addressLine2 },
          { title: t("EW_BUILDING_NAME"), value: application?.address?.buildingName },
        ],
      }, 
 
      {
        tableData: {
          title: t("EW_PRODUCT_DETAILS"),
          rows: application.ewasteDetails,
        }
      }
    ],


  };
};

export default getEwAcknowledgementData;
