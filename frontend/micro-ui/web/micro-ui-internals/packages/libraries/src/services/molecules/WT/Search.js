import { WTService } from "../../elements/WT";
import { format } from "date-fns";
import React from "react";

/**
 * Provides methods for interacting with water tanker (WT) bookings. 
 * Includes functionality for searching all bookings, retrieving a specific application, 
 * formatting booking details, and returning application-specific data. 
 * Uses `WTService.search` to fetch data, then formats it for display in the UI. 
 */
export const WTSearch = {
  
  all: async (tenantId, filters = {}) => {
    
    const response = await WTService.search({ tenantId, filters });
    
    return response;
  },

  
  application: async (tenantId, filters = {}) => {
    const response = await WTService.search({ tenantId, filters });
    return response.waterTankerBookingDetail[0];
  },
  BookingDetails: ({ waterTankerBookingDetail: response, t }) => {

   let immediateRequired = (response?.extraCharge === "Y") ? "YES":"NO"
    return [
      {
        title: "WT_BOOKING_NO", 
        values:[{title: "WT_BOOKING_NO", value: response?.bookingNo || t("CS_NA")}]
      },
      {
        title: "WT_APPLICANT_DETAILS",

        asSectionHeader: true,
        values: [
          { title: "WT_APPLICANT_NAME", value: response?.applicantDetail?.name || t("CS_NA")},
          { title: "WT_MOBILE_NUMBER", value: response?.applicantDetail?.mobileNumber || t("CS_NA")},
          { title: "WT_ALT_MOBILE_NUMBER", value: response?.applicantDetail?.alternateNumber || t("CS_NA")}, 
          { title: "WT_EMAIL_ID", value: response?.applicantDetail?.emailId || t("CS_NA")}
        ],
      },
      {
        title: "WT_ADDRESS_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "WT_PINCODE", value: response?.address?.pincode || t("CS_NA")},
          { title: "WT_CITY", value: response?.address?.city  || t("CS_NA")},
          { title: "WT_LOCALITY", value: response?.address?.locality  || t("CS_NA")},
          { title: "WT_STREET_NAME", value: response?.address?.streetName  || t("CS_NA")},
          { title: "WT_HOUSE_NO", value: response?.address?.houseNo  || t("CS_NA")},
          { title: "WT_LANDMARK", value: response?.address?.landmark  || t("CS_NA")},
          { title: "WT_ADDRESS_LINE1", value: response?.address?.addressLine1  || t("CS_NA")},
          { title: "WT_ADDRESS_LINE2", value: response?.address?.addressLine2  || t("CS_NA")}
        ],
      },
      {
        title: "WT_REQUEST_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "WT_TANKER_TYPE", value: response?.tankerType || t("CS_NA")},
          { title: "WT_WATER_TYPE", value: response?.waterType || t("CS_NA")},
          { title: "WT_TANKER_QUANTITY", value: response?.tankerQuantity || t("CS_NA")},
          { title: "WT_WATER_QUANTITY", value: response?.waterQuantity + " Ltr" || t("CS_NA")},
          { title: "WT_DELIVERY_DATE", value:  format(new Date(response?.deliveryDate), 'dd-MM-yyyy') || t("CS_NA")},
          { title: "WT_DELIVERY_TIME", value: response?.deliveryTime || t("CS_NA"), isTimeValue: true},
          { title: "WT_DESCRIPTION", value: response?.description || t("CS_NA")},
          { title: "WT_IMMEDIATE", value:immediateRequired || t("CS_NA")},
        ],
      },
    ];
  },
  applicationDetails: async (t, tenantId, BookingNo, userType, args) => {
    const filter = { BookingNo, ...args };
    const response = await WTSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: WTSearch.BookingDetails({ waterTankerBookingDetail: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: WTSearch.BookingDetails
    };
  },
};
