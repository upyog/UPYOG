import { ADSServices } from "../../elements/ADS";

export const ADSSearch = {
  
  all: async (tenantId, filters = {}) => {
    
    const response = await ADSServices.search({ tenantId, filters });
    
    return response;
  },

  
  application: async (tenantId, filters = {}) => {
    const response = await ADSServices.search({ tenantId, filters });
    return response.bookingApplication[0];
  },
  BookingDetails: ({ bookingApplication: response, t }) => {
    console.log("response :: ", response)
    const adslistRows =
    response?.cartDetails?.map((slot) => (
      [`${t(slot.addType)}`,
      `${t(slot.faceArea)}`,
      `${t(slot.nightLight===true?"Yes":"No")}`,
      `${t(slot.bookingDate)}`,
        `${t(slot.status)}`,]
      )) || [];
    return [
      {
        title: "ADS_BOOKING_NO", 
        values:[
          {title: "ADS_BOOKING_NO", value: response?.bookingNo || t("CS_NA")}
        ],
      },
      {
        title: "ADS_APPLICANT_DETAILS",

        asSectionHeader: true,
        values: [
          { title: "ADS_APPLICANT_NAME", value: response?.applicantDetail?.applicantName || t("CS_NA")},
          { title: "ADS_MOBILE_NUMBER", value: response?.applicantDetail?.applicantMobileNo || t("CS_NA")},
          { title: "ADS_ALT_MOBILE_NUMBER", value: response?.applicantDetail?.applicantAlternateMobileNo || t("CS_NA")}, 
          { title: "ADS_EMAIL_ID", value: response?.applicantDetail?.applicantEmailId || t("CS_NA")} ,
        ],
      },
      {
        title: "ADS_ADDRESS_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "ADS_ADDRESS_LINE1", value: response?.address?.addressLine1 || t("CS_NA")},
          { title: "ADS_ADDRESS_LINE2", value: response?.address?.addressLine2 || t("CS_NA")},
          { title: "ADS_CITY",value: response?.address?.city || t("CS_NA")},
          { title: "ADS_LOCALITY",value: response?.address?.locality || t("CS_NA")},
          { title: "ADS_ADDRESS_PINCODE",value: response?.address?.pincode || t("CS_NA")},
          { title: "ADS_LANDMARK",value: response?.address?.landmark || t("CS_NA")},  
        ],
      },

      {
        title:"ADS_CART_DETAILS",
        asSectionHeader: true,
        isTable: true,
        headers: ["ADS_TYPE", "ADS_FACE_AREA","ADS_NIGHT_LIGHT" ,"ADS_BOOKING_DATE","PT_COMMON_TABLE_COL_STATUS_LABEL"],
        tableRows: adslistRows,
      },

      {
        title: "ADS_DOCUMENTS_DETAILS",
        additionalDetails: {
          
          documents: [
            {
             
              values: response?.documents
                ?.map((document) => {

                  return {
                    title: `ADS_${document?.documentType.split('.').slice(0, 4).join('_')}`,
                    documentType: document?.documentType,
                    documentUid: document?.documentUid,
                    fileStoreId: document?.fileStoreId,
                    status: document.status,
                  };
                }),
            },
          ],
        },
      },
    ];
  },
  applicationDetails: async (t, tenantId, BookingNo, userType, args) => {
    const filter = { BookingNo, ...args };
    const response = await ADSSearch.application(tenantId, filter);
    
    return {
      tenantId: response.tenantId,
      applicationDetails: ADSSearch.BookingDetails({ bookingApplication: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: ADSSearch.BookingDetails,
      
    };
  },
};
