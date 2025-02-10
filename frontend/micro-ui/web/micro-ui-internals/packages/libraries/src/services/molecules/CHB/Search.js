import { CHBServices } from "../../elements/CHB";

export const CHBSearch = {
  
  all: async (tenantId, filters = {}) => {
    
    const response = await CHBServices.search({ tenantId, filters });
    
    return response;
  },

  
  application: async (tenantId, filters = {}) => {
    const response = await CHBServices.search({ tenantId, filters });
    return response.hallsBookingApplication[0];
  },
  BookingDetails: ({ hallsBookingApplication: response, t }) => {
    const slotlistRows = response?.bookingSlotDetails?.map((slot) => (
      [
        response?.communityHallCode,
        slot.hallCode + " - " + slot.capacity,
        slot.bookingDate,
        // slot.bookingDate + " (" + slot.bookingFromTime + " - " + slot.bookingToTime + ")",
        `${t(slot.status)}`
      ]
    )) || [];
    return [
      {
        title: "CHB_BOOKING_NO", 
        values:[{title: "CHB_BOOKING_NO", value: response?.bookingNo || t("CS_NA")}],
      },
      {
        title: "CHB_APPLICANT_DETAILS",

        asSectionHeader: true,
        values: [
          { title: "CHB_APPLICANT_NAME", value: response?.applicantDetail?.applicantName || t("CS_NA")},
          { title: "CHB_MOBILE_NUMBER", value: response?.applicantDetail?.applicantMobileNo || t("CS_NA")},
          { title: "CHB_ALT_MOBILE_NUMBER", value: response?.applicantDetail?.applicantAlternateMobileNo || t("CS_NA")}, 
          { title: "CHB_EMAIL_ID", value: response?.applicantDetail?.applicantEmailId || t("CS_NA")} ,
        ],
      },

      {
        title: "CHB_EVENT_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "CHB_SPECIAL_CATEGORY", value: response?.specialCategory?.category || t("CS_NA")},
          { title: "CHB_PURPOSE", value: response?.purpose?.purpose || t("CS_NA")},
          { title: "CHB_PURPOSE_DESCRIPTION", value: response?.purposeDescription || t("CS_NA")},

        ],
      },
      {
        title: "CHB_ADDRESS_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "CHB_PINCODE", value: response?.address?.pincode || t("CS_NA")},
          { title: "CHB_CITY", value: response?.address?.city  || t("CS_NA")},
          { title: "CHB_LOCALITY", value: response?.address?.locality  || t("CS_NA")},
          { title: "CHB_STREET_NAME", value: response?.address?.streetName  || t("CS_NA")},
          { title: "CHB_HOUSE_NO", value: response?.address?.houseNo  || t("CS_NA")},
          { title: "CHB_LANDMARK", value: response?.address?.landmark  || t("CS_NA")},
        ],
      },
      {
        title: "CHB_BANK_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "CHB_ACCOUNT_NUMBER", value: response?.applicantDetail?.accountNumber || t("CS_NA")},
          { title: "CHB_IFSC_CODE", value: response?.applicantDetail?.ifscCode || t("CS_NA")},
          { title: "CHB_BANK_NAME",value: response?.applicantDetail?.bankName || t("CS_NA")},
          { title: "CHB_BANK_BRANCH_NAME",value: response?.applicantDetail?.bankBranchName || t("CS_NA")},
          { title: "CHB_ACCOUNT_HOLDER_NAME",value: response?.applicantDetail?.accountHolderName || t("CS_NA")},        ],
      },
      {
        title:"SLOT_DETAILS",
        asSectionHeader: true,
        isTable: true,
        headers: [`${t("CHB_HALL_NAME")}` + "/" + `${t("CHB_PARK")}`, "CHB_HALL_CODE", "CHB_BOOKING_DATE", "PT_COMMON_TABLE_COL_STATUS_LABEL"],
        tableRows: slotlistRows,
      },

      {
        title: "CHB_DOCUMENTS_DETAILS",
        additionalDetails: {
          
          documents: [
            {
             
              values: response?.documents
                ?.map((document) => {

                  return {
                    title: `CHB_${document?.documentType?.split('.').slice(0,2).join('_')}`,
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
    const response = await CHBSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: CHBSearch.BookingDetails({ hallsBookingApplication: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: CHBSearch.BookingDetails,
      
    };
  },
};
