  const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
  const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");



  const getAssessmentInfo = (application, t) => {
  
    let values = [
      {
        title: t("CHB_COMMUNITY_HALL_NAME"),
      
        value: application?.slots.selectslot,
      },

      { title: t("SELECT_SLOT"), value: application?.slots.selectslot},
      { title: t("RESIDENT_TYPE"), value: application?.slots.residentType},
      { title: t("SPECIAL_CATEGORY"), value: application?.slots.specialCategory },
      { title: t("PURPOSE"), value: application?.slots.purpose },
    ];
  
    return {
      title: t("ES_TITILE_CHB_DETAILS"),
      values: values,
    };
  };





  const getChbAcknowledgementData = async (application, tenantInfo, t) => {
    const filesArray = application?.documents?.map((value) => value?.fileStoreId);
    const res = filesArray?.length > 0 && (await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()));


    return {
      t: t,
      tenantId: tenantInfo?.code,
      name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
      email: tenantInfo?.emailId,
      phoneNumber: tenantInfo?.contactNumber,
      heading: t("CHB_ACKNOWLEDGEMENT"),
      details: [
        {
          title: t("CS_TITLE_APPLICATION_DETAILS"),
          values: [
            { title: t("CHB_APPLICATION_NUMBER"), value: application?.applicationNumber },
            
            {
              title: t("CHB_APPLICANT_NAME"),
              value: application?.applicantName,
            },
            {
              title: t("CHB_APPLICATION_DETAILS_APPLICATION_DATE"),
              value: Digit.DateUtils.ConvertTimestampToDate(application?.auditDetails?.createdTime, "dd/MM/yyyy"),
            },
            {
              title: t("CHB_MOBILE_NUMBER"),
              value: application?.mobileNumber,
            },
            {
              title: t("CHB_EMAIL_ID"),
              value: application?.emailId,
            },
          ],
        },
      
        getAssessmentInfo(application, t),
        {
          
          title: t("CHB_BANK_DETAILS"),
          values: [
            { title: t("ACCOUNT_NUMBER"), value: application?.bankdetails?.accountNumber},
            { title: t("CONFIRM_ACCOUNT_NUMBER"), value: application?.bankdetails?.confirmAccountNumber },
            
            { title: t("IFSC_CODE"), value: application?.bankdetails?.IFSC },
            { title: t("BANK_NAME"), value: application?.bankdetails?.BankName },
            { title: t("BANK_BRANCH_NAME"), value: application?.bankdetails?.BankBranchName },
            { title: t("ACCOUNT_HOLDER_NAME"), value: application?.bankdetails?.accountHolderName },
          ],
        },
      
      ],
    };
  };

  export default getChbAcknowledgementData;