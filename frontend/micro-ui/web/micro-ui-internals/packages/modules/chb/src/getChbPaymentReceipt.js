const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");



const getAssessmentInfo = (application, t) => {
  console.log("Payment Receipt Data:", application);

   let values = [
  //   {
  //     title: t("CHB_COMMUNITY_HALL_NAME"),
    
  //     value: application?.slots.selectslot,
  //   },

  //   { title: t("SELECT_SLOT"), value: application?.slots.selectslot},
  //   { title: t("RESIDENT_TYPE"), value: application?.slots.residentType},
  //   { title: t("SPECIAL_CATEGORY"), value: application?.slots.specialCategory },
  //   { title: t("PURPOSE"), value: application?.slots.purpose },
  ];

  // return {
  //   title: t("ES_TITILE_CHB_DETAILS"),
  //   values: values,
  // };
};





const getChbPaymentReceipt = async (application, tenantInfo, t) => {
  const filesArray = application?.documents?.map((value) => value?.fileStoreId);
  const res = filesArray?.length > 0 && (await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()));


  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    heading: t("CHB_ PAYMENT_RECEIPT"),
    details: [
      {
        title: t("Payment Details"),
        values: [
          { title: t("CHB_RECEIPT_NO"), 
          value: application?.bookingNo},
          
          {
            title: t("CHB_APPLICANT_NAME"),
            value: application?.applicantName,
          },
          {
            title: t("CHB_MOBILE_NUMBER"),
            value: application?.applicantMobileNo
          },
          {
            title: t("CHB_EMAIL_ID"),
            value: application?.applicantEmailId
          },
          {
            title: t("CHB_AMOUNT_PAID"),
            value: application?.amountPaid,
          },
          {
            title: t("CHB_PAYMENT_MODE"),
            value: application?.paymentMode,
          },
          {
            title: t("CHB_TRANSACTION_ID"),
            value: application?.transactionId,
          },

        ],
      },
    
      getAssessmentInfo(application, t),
      {
        
        title: t("CHB_BOOKING_DETAILS"),
        values: [
          { title: t("BOOKING_NO"), 
          value: application?.bookingNo
          },

          { title: t("COMMUNITY_HALL_NAME"), 
          value: application?.bookingSlotDetails[0].hallName
          },
          
          { title: t("BOOKING_DATE"),
          value: application?.bookingSlotDetails[0]?.bookingDate }
          ,
          { title: t("BOOKING_STATUS"), 
          value: application?.bookingStatus
        }
          ,
          { title: t("COMMUNITY_HALL_ID"), 
          value: application?.communityHallId
        }
          ,
          { title: t("SERVICE_TYPE"), 
          value: application?.workflow?.businessService
        }
          ,
        ],
      },
    
    ],
  };
};

export default getChbPaymentReceipt;