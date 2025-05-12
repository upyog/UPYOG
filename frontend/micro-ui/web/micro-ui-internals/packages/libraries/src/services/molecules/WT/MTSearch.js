import { MTService } from "../../elements/MT";
import format from "date-fns/format";
import React from "react";

/**
 * Provides methods for interacting with Mobile Toilet (MT) bookings. 
 * Includes functionality for searching all bookings, retrieving a specific application, 
 * formatting booking details, and returning application-specific data. 
 * Uses `MTService.search` to fetch data, then formats it for display in the UI. 
 */
export const MTSearch = {
  
  all: async (tenantId, filters = {}) => {
    
    const response = await MTService.search({ tenantId, filters });
    
    return response;
  },

  
  application: async (tenantId, filters = {}) => {
    const response = await MTService.search({ tenantId, filters });
    return response.mobileToiletBookingDetails[0];
  },
  BookingDetails: ({ mobileToiletBookingDetails: response, t }) => {
     console.log("applicationDetails",response);
        return [
      {
        title: "MT_BOOKING_NO", 
        values:[{title: "MT_BOOKING_NO", value: response?.bookingNo || t("CS_NA")}]
      },
      {
        title: "MT_APPLICANT_DETAILS",

        asSectionHeader: true,
        values: [
          { title: "MT_APPLICANT_NAME", value: response?.applicantDetail?.name || t("CS_NA")},
          { title: "MT_MOBILE_NUMBER", value: response?.applicantDetail?.mobileNumber || t("CS_NA")},
          { title: "MT_ALT_MOBILE_NUMBER", value: response?.applicantDetail?.alternateNumber || t("CS_NA")}, 
          { title: "MT_EMAIL_ID", value: response?.applicantDetail?.emailId || t("CS_NA")}
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
        title: "MT_REQUEST_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "MT_NUMBER_OF_MOBILE_TOILETS", value: response?.noOfMobileToilet || t("CS_NA")},
          { title: "MT_DELIVERY_FROM_DATE", value: format(new Date(response?.deliveryFromDate), 'dd-MM-yyyy')  || t("CS_NA")},
          { title: "MT_DELIVERY_TO_DATE", value: format(new Date(response?.deliveryToDate), 'dd-MM-yyyy')  || t("CS_NA")},
          { title: "MT_REQUIREMNENT_FROM_TIME", value: response?.deliveryFromTime || t("CS_NA"), isTimeValue: true },
          { title: "MT_REQUIREMNENT_TO_TIME", value: response?.deliveryToTime || t("CS_NA"), isTimeValue: true },
          { title: "MT_SPECIAL_REQUEST", value: response?.description || t("CS_NA")},
        ],
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
    const response = await MTSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: MTSearch.BookingDetails({ mobileToiletBookingDetails: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: MTSearch.BookingDetails
    };
  },
};
