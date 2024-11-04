const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");



const getAssessmentInfo = (application, t) => {
  console.log("applicationData in the getsvacknowlegdmentdat page :: ", application)
  let values = [
    // {
    //   title: t("SV_NAME"),
    //   value: application?.petDetails.petName,
    // },
    // { title: t("SV_PET_TYPE"), value: application?.petDetails.petType },
    // { title: t("SV_BREED_TYPE"), value: application?.petDetails.breedType },
    // { title: t("SV_VACCINATED_DATE"), value: application?.petDetails.lastVaccineDate },
    // { title: t("SV_VACCINATION_NUMBER"), value: application?.petDetails.vaccinationNumber },
    // { title: t("SV_DOCTOR_NAME"), value: application?.petDetails.doctorName },
    // { title: t("SV_CLINIC_NAME"), value: application?.petDetails.clinicName },
    // { title: t("SV_PET_AGE"), value: application?.petDetails.petAge + " Months" },
    // { title: t("SV_PET_SEX"), value: application?.petDetails.petGender },
  ];

  return {
    title: t("ES_TITILE_PET_DETAILS"),
    values: values,
  };
};

const getSVAcknowledgementData = async (application, tenantInfo, t) => {
  const filesArray = application?.documents?.map((value) => value?.fileStoreId);
  const res = filesArray?.length > 0 && (await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()));

  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    heading: t("SV_ACKNOWLEDGEMENT"),
    details: [
      // {
      //   title: t("CS_TITLE_APPLICATION_DETAILS"),
      //   values: [
      //     { title: t("SV_APPLICATION_NUMBER"), value: application?.applicationNumber },

      //     {
      //       title: t("SV_APPLICANT_NAME"),
      //       value: application?.applicantName,
      //     },
      //     {
      //       title: t("CS_APPLICATION_DETAILS_APPLICATION_DATE"),
      //       value: Digit.DateUtils.ConvertTimestampToDate(application?.auditDetails?.createdTime, "dd/MM/yyyy"),
      //     },
      //     {
      //       title: t("SV_MOBILE_NUMBER"),
      //       value: application?.mobileNumber,
      //     },
      //     {
      //       title: t("SV_EMAIL_ID"),
      //       value: application?.emailId,
      //     },
      //   ],
      // },

      getAssessmentInfo(application, t),
      // {

      //   title: t("SV_LOCATION_DETAILS"),
      //   values: [
      //     { title: t("SV_PINCODE"), value: application?.address?.pincode },
      //     { title: t("SV_CITY"), value: application?.address?.city },

      //     { title: t("SV_STREET_NAME"), value: application?.address?.street },
      //     { title: t("SV_HOUSE_NO"), value: application?.address?.doorNo },
      //   ],
      // },

    ],
  };
};

export default getSVAcknowledgementData;