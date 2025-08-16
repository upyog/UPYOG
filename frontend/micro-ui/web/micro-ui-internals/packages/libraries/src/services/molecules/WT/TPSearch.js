import { TPService } from "../../elements/TP";
import format from "date-fns/format";
import React from "react";

/**
 * Provides methods for interacting with Tree Pruning (TP) bookings. 
 * Includes functionality for searching all bookings, retrieving a specific application, 
 * formatting booking details, and returning application-specific data. 
 * Uses `TPService.search` to fetch data, then formats it for display in the UI. 
 */
export const TPSearch = {
  
  all: async (tenantId, filters = {}) => {
    
    const response = await TPService.search({ tenantId, filters });
    
    return response;
  },

  
  application: async (tenantId, filters = {}) => {
    const response = await TPService.search({ tenantId, filters });
    return response.treePruningBookingDetails[0];
  },
  BookingDetails: ({ treePruningBookingDetails: response, t }) => {
     console.log("applicationDetails",response);
        return [
      {
        title: "TP_BOOKING_NO", 
        values:[{title: "TP_BOOKING_NO", value: response?.bookingNo || t("CS_NA")}]
      },
      {
        title: "TP_APPLICANT_DETAILS",

        asSectionHeader: true,
        values: [
          { title: "TP_APPLICANT_NAME", value: response?.applicantDetail?.name || t("CS_NA")},
          { title: "TP_MOBILE_NUMBER", value: response?.applicantDetail?.mobileNumber || t("CS_NA")},
          { title: "TP_ALT_MOBILE_NUMBER", value: response?.applicantDetail?.alternateNumber || t("CS_NA")}, 
          { title: "TP_EMAIL_ID", value: response?.applicantDetail?.emailId || t("CS_NA")}
        ],
      },
      {
        title: "ES_TITLE_ADDRESS_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "PINCODE", value: response?.address?.pincode || t("CS_NA")},
          { title: "CITY", value: response?.address?.city  || t("CS_NA")},
          { title: "LOCALITY", value: response?.address?.locality  || t("CS_NA")},
          { title: "STREET_NAME", value: response?.address?.streetName  || t("CS_NA")},
          { title: "HOUSE_NO", value: response?.address?.houseNo  || t("CS_NA")},
          { title: "LANDMARK", value: response?.address?.landmark  || t("CS_NA")},
          { title: "ADDRESS_LINE1", value: response?.address?.addressLine1  || t("CS_NA")},
          { title: "ADDRESS_LINE2", value: response?.address?.addressLine2  || t("CS_NA")}
        ],
      },
      {
        title: "TP_REQUEST_DETAILS",
        asSectionHeader: true,
        values: [
          { title: t("REASON_FOR_PRUNING"), value: t(response?.reasonForPruning) || t("CS_NA") },
          { title: t("LATITUDE_GEOTAG"), value: response?.latitude || t("CS_NA") },
          { title: t("LONGITUDE_GEOTAG"), value: response?.longitude  || t("CS_NA") },
        ],
      },
      {
        title: "SITE_PHOTOGRAPH",
        additionalDetails: {
          
          documents: [
            {
             
              values: response?.documentDetails
                ?.map((document) => {

                  return {
                    title: `${document?.documentType?.split('.').slice(0,2).join('_')}`,
                    documentType: document?.documentType,
                    documentUid: document?.documentDetailId,
                    fileStoreId: document?.fileStoreId
                  };
                }),
            },
          ],
        },
      },
    ];
  },
  /*  
      - Creates a filter object combining BookingNo and additional arguments.
      - Fetches booking details using `MTSearch.application`.
      - Extracts the tenantId from the response.
      - Processes and structures the booking details using `MTSearch.BookingDetails`.
      - Stores the full API response in `applicationData` for reference.
      - Provides a transformation function (`MTSearch.BookingDetails`) for employee-specific data formatting.
    */
  applicationDetails: async (t, tenantId, BookingNo, userType, args) => {
    const filter = { BookingNo, ...args };
    const response = await TPSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: TPSearch.BookingDetails({ treePruningBookingDetails: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: TPSearch.BookingDetails
    };
  },
};
