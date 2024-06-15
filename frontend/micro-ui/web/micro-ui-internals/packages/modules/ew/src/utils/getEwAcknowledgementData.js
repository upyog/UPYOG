import { values } from "lodash";

const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");



  const getAssessmentInfo = (application, t) => {
  console.log("apppllllll",application)
    let values = [
      // {
      //   title: t("PTR_PET_NAME"),
      
      //   value: application?.petDetails.petName,
      // },

      // { title: t("PTR_PET_TYPE"), value: application?.petDetails.petType },
      // { title: t("PTR_BREED_TYPE"), value: application?.petDetails.breedType },
      // { title: t("PTR_VACCINATED_DATE"), value: application?.petDetails.lastVaccineDate },
      // { title: t("PTR_VACCINATION_NUMBER"), value: application?.petDetails.vaccinationNumber },
      // {title: t("PTR_DOCTOR_NAME"), value: application?.petDetails.doctorName },
      // {title: t("PTR_CLINIC_NAME"), value: application?.petDetails.clinicName },
      // {title: t("PTR_PET_AGE"), value: application?.petDetails.petAge + " Months" },
      // {title: t("PTR_PET_SEX"), value: application?.petDetails.petGender },
    


    ];
  
    // return {
    //   title: t("ES_TITILE_PET_DETAILS"),
    //   values: values,
    // };
  };





  const getEwAcknowledgementData = async (application, tenantInfo, t) => {
    console.log("getget",application);
    const filesArray = application?.documents?.map((value) => value?.fileStoreId);
    const res = filesArray?.length > 0 && (await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()));

   
    return {
      t: t,
      tenantId: tenantInfo?.code,
      name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
      email: tenantInfo?.emailId,
      phoneNumber: tenantInfo?.contactNumber,
      heading: t("EW_ACKNOWLEDGEMENT"),
      details: [
        {
          title: t("EW_APPLICANT_DETAILS"),
          values: [
            { title: t("EW_APPLICATION_NUMBER"), value: application?.requestId },

            {
              title: t("EW_APPLICANT_NAME"),
              value: application?.applicant,
            },
            // {
            //   title: t("CS_APPLICATION_DETAILS_APPLICATION_DATE"),
            //   value: Digit.DateUtils.ConvertTimestampToDate(application?.auditDetails?.createdTime, "dd/MM/yyyy"),
            // },
            {
              title: t("EW_MOBILE_NUMBER"),
              value: application?.mobileNumber,
            },
            {
              title: t("EW_EMAIL_ID"),
              value: application?.emailId,
            },
          ],
        },

        getAssessmentInfo(application, t),
        {

          title: t("EW_ADDRESS_DETAILS"),
          values: [
            { title: t("EW_PINCODE"), value: application?.address?.pincode },
            { title: t("EW_CITY"), value: application?.address?.city },
            { title: t("EW_DOOR_NO"), value: application?.address?.city },

            { title: t("EW_STREET"), value: application?.address?.street },
            { title: t("EW_ADDRESS_LINE_1"), value: application?.address?.doorNo },
            { title: t("EW_ADDRESS_LINE_2"), value: application?.address?.doorNo },
            { title: t("EW_BUILDING_NAME"), value: application?.address?.doorNo },

          ],
        },
      
      ],
    };
  };

  export default getEwAcknowledgementData;