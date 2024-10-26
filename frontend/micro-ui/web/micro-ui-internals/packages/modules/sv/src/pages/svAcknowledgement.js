const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");



const getAssessmentInfo = (application, t) => {

  return {
    title: t("TEST_STREET"),
    // values: values,
  };
};





const svAcknowledgement = async (application, tenantInfo, t) => {
console.log("applicationapplicationapplication",application);

  const filesArray = application?.documents?.map((value) => value?.fileStoreId);
  const res = filesArray?.length > 0 && (await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()));

  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    heading: t("STREET_VENDING_ACKNOWLEDGEMENT"),
    details: [
      
    
      getAssessmentInfo(application, t),
    
    ],
  };
};

export default svAcknowledgement;