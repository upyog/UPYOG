  // Capitalizes the first letter of a string
  const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
  // Converts ULB (Urban Local Body) name to camel case format
  const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");


/**
 * Generates pet assessment information details
 * @param {Object} application - The application object containing pet details
 * @param {Function} t - Translation function for localization
 * @returns {Object} - Assessment info object with title and values
 */
  const getAssessmentInfo = (application, t) => {
  
    let values = [
      {
        title: t("PTR_PET_NAME"),
      
        value: application?.petDetails.petName,
      },

      { title: t("PTR_PET_TYPE"), value: application?.petDetails.petType },
      { title: t("PTR_BREED_TYPE"), value: application?.petDetails.breedType },
      { title: t("PTR_VACCINATED_DATE"), value: application?.petDetails.lastVaccineDate },
      { title: t("PTR_VACCINATION_NUMBER"), value: application?.petDetails.vaccinationNumber },
      {title: t("PTR_DOCTOR_NAME"), value: application?.petDetails.doctorName },
      {title: t("PTR_CLINIC_NAME"), value: application?.petDetails.clinicName },
      {title: t("PTR_PET_AGE"), value: application?.petDetails.petAge + " Months" },
      {title: t("PTR_PET_SEX"), value: application?.petDetails.petGender },
    


    ];
  
    return {
      title: t("ES_TITILE_PET_DETAILS"),
      values: values,
    };
  };


/**
 * Generates pet acknowledgement data for displaying on the UI
 * @param {Object} application - The application object containing all pet-related info
 * @param {Object} tenantInfo - The tenant details (ULB info)
 * @param {Function} t - Translation function for localization
 * @returns {Object} - Acknowledgement data object
 */


  const getPetAcknowledgementData = async (application, tenantInfo, t) => {

    // Fetches file store IDs for uploaded documents
    const filesArray = application?.documents?.map((value) => value?.fileStoreId);
    const res = filesArray?.length > 0 && (await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()));
    // Helper function to convert epoch date to 'DD/MM/YYYY' format
    const getDate = (epochdate) => {
      return epochdate
        ? new Date(epochdate * 1000).getDate() + 
          "/" + 
          (new Date(epochdate * 1000).getMonth() + 1) + 
          "/" + 
          new Date(epochdate * 1000).getFullYear().toString()
        : "NA";
    };


    return {
      t: t,
      tenantId: tenantInfo?.code,
      name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
      email: tenantInfo?.emailId,
      phoneNumber: tenantInfo?.contactNumber,
      heading: t("PTR_ACKNOWLEDGEMENT"),
      details: [
        {
          // Application details section
          title: t("CS_TITLE_APPLICATION_DETAILS"),
          values: [
            { title: t("PTR_APPLICATION_NUMBER"), value: application?.applicationNumber },
            { title: t("PTR_VALIDITY_DATE"), value: getDate(application?.validityDate)},
            
            {
              title: t("PTR_APPLICANT_NAME"),
              value: application?.applicantName,
            },
            {
              title: t("CS_APPLICATION_DETAILS_APPLICATION_DATE"),
              value: Digit.DateUtils.ConvertTimestampToDate(application?.auditDetails?.createdTime, "dd/MM/yyyy"),
            },
            {
              title: t("PTR_MOBILE_NUMBER"),
              value: application?.mobileNumber,
            },
            {
              title: t("PTR_EMAIL_ID"),
              value: application?.emailId,
            },
          ],
        },
       // Pet assessment details section
        getAssessmentInfo(application, t),
        {
          // Location details section
          title: t("PTR_LOCATION_DETAILS"),
          values: [
            { title: t("PTR_PINCODE"), value: application?.address?.pincode },
            { title: t("PTR_CITY"), value: application?.address?.city },
            
            { title: t("PTR_STREET_NAME"), value: application?.address?.street },
            { title: t("PTR_HOUSE_NO"), value: application?.address?.doorNo },
          ],
        },
      
      ],
    };
  };

  export default getPetAcknowledgementData;