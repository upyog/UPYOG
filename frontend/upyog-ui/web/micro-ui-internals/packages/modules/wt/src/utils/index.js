import React, { useEffect } from "react";
import { Toast } from "@nudmcdgnpm/digit-ui-react-components";

export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
    if (searcher == "") return str;
    while (str.includes(searcher)) {
      str = str.replace(searcher, replaceWith);
    }
    return str;
};
export const checkForNotNull = (value = "") => {
  return value && value != null && value != undefined && value != "" ? true : false;
};

export const checkForNA = (value = "") => {
  return checkForNotNull(value) ? value : "NA";
};

export const convertTo12HourFormat = (time) => {
  if (!time) return "NA"; // Handle empty or invalid values

  const [hours, minutes] = time.split(":").map(Number);
  const period = hours >= 12 ? "PM" : "AM";
  const formattedHours = hours % 12 || 12; // Convert 0 to 12 for 12 AM

  return `${formattedHours}:${minutes.toString().padStart(2, "0")} ${period}`;
};

export const formatDate = (dateString) => {
  if (!dateString) return "NA"; // Handle empty or invalid values

  const [year, month, day] = dateString.split("-");
  return `${day}-${month}-${year}`;
};

/**
 * Converts a 24-hour time string (HH:mm) to total minutes from midnight.
 *
 * @param {string} time - Time string in "HH:mm" format.
 * @return {number} Total minutes since 00:00.
 */
const toMinutes = (time) => {
  const [h, m] = time.split(":").map(Number);
  return h * 60 + m;
};

/**
 * Returns the minimum selectable delivery date.
 * If 3 hours lead time crosses midnight (after 21:00), today is no longer bookable and tomorrow is returned.
 *
 * @return {string} Minimum allowed delivery date in "YYYY-MM-DD" format.
 */
export const getMinDeliveryDate = () => {
  const now = new Date();
  const minDate = new Date(now.getTime() + 3 * 60 * 60 * 1000);
  if (minDate.getDate() !== now.getDate()) {
    return minDate.toLocaleDateString("en-CA");
  }
  return now.toLocaleDateString("en-CA");
};

/**
 * Calculates the minimum allowed delivery time for a selected delivery date.
 * If the selected date is today, enforces at least a 3-hour gap from the current time.
 * If 3-hour lead time rolls over to tomorrow (after 21:00), returns null indicating no slots left today.
 * For future dates, allows delivery starting from "06:00".
 *
 * @param {string} selectedDate - Selected date in "YYYY-MM-DD" format.
 * @return {string|null} Minimum allowed delivery time in "HH:mm" format, or null if no slots remain.
 */
export const getMinDeliveryTime = (selectedDate) => {
  const now = new Date();
  if (selectedDate === now.toLocaleDateString("en-CA")) {
    const minDate = new Date(now.getTime() + 3 * 60 * 60 * 1000);
    // If 3 hours lead time crosses midnight, no valid delivery slots remain today
    if (minDate.getDate() !== now.getDate()) return null;
    const minTime = minDate.toTimeString().slice(0, 5);
    return minTime > "06:00" ? minTime : "06:00";
  }
  return "06:00";
};

/**
 * Validates whether the selected delivery time is earlier than the allowed minimum time.
 * Returns true if no slots remain for selected date or selected time is before minimum delivery time.
 *
 * @param {string} selectedDate - Selected date in "YYYY-MM-DD" format.
 * @param {string} selectedTime - Selected time in "HH:mm" format.
 * @return {boolean} True if selected time is invalid, false otherwise.
 */
export const isTimeBeforeMin = (selectedDate, selectedTime) => {
  if (!selectedDate || !selectedTime) return false;
  const minTime = getMinDeliveryTime(selectedDate);
  return minTime === null || selectedTime < minTime;
};

/**
 * Returns the appropriate error message key when delivery time validation fails.
 * Distinguishes between time before 06:00 AM (WT_DELIVERY_TIME_MIN_ERROR)
 * and 3-hour lead time violation on current day (WT_DELIVERY_TIME_GAP_ERROR).
 *
 * @param {string} selectedDate - Selected date in "YYYY-MM-DD" format.
 * @param {string} selectedTime - Selected time in "HH:mm" format.
 * @return {string} Localization key for the error message.
 */
export const getTimeErrorMessage = (selectedDate, selectedTime) => {
  if (selectedTime && selectedTime < "06:00") {
    return "WT_DELIVERY_TIME_MIN_ERROR";
  }
  return "WT_DELIVERY_TIME_GAP_ERROR";
};

/**
 * Adds specified hours to a time string. Caps at "23:59" if it rolls over to next day.
 *
 * @param {string} timeString - Base time string in "HH:mm" format.
 * @param {number} [hoursToAdd=1] - Number of hours to add.
 * @return {string} Resulting time in "HH:mm" format.
 */
export const getTimeAfterHours = (timeString, hoursToAdd = 1) => {
  if (!timeString) return "";
  const total = toMinutes(timeString) + hoursToAdd * 60;
  if (total >= 24 * 60) return "23:59";
  const h = String(Math.floor(total / 60)).padStart(2, "0");
  const m = String(total % 60).padStart(2, "0");
  return `${h}:${m}`;
};

/**
 * Checks whether Requirement To Time violates the minimum gap requirement from Requirement From Time.
 * Validation applies only when From Date and To Date are the exact same day.
 *
 * @param {string} fromDate - Delivery from date in "YYYY-MM-DD" format.
 * @param {string} toDate - Delivery to date in "YYYY-MM-DD" format.
 * @param {string} fromTime - Requirement from time in "HH:mm" format.
 * @param {string} toTime - Requirement to time in "HH:mm" format.
 * @param {number} [minGapHours=1] - Minimum required gap in hours (default: 1).
 * @return {boolean} True if dates are identical and gap is less than minGapHours, false otherwise.
 */
export const isToTimeBeforeFromTime = (fromDate, toDate, fromTime, toTime, minGapHours = 1) => {
  return Boolean(
    fromDate &&
    toDate &&
    fromTime &&
    toTime &&
    fromDate === toDate &&
    toMinutes(toTime) - toMinutes(fromTime) < minGapHours * 60
  );
};

/**
 * Computes the minimum selectable Requirement To Time for the time picker.
 * On same day, sets the lower bound to (fromTime + minGapHours).
 *
 * @param {string} fromDate - Delivery from date in "YYYY-MM-DD" format.
 * @param {string} toDate - Delivery to date in "YYYY-MM-DD" format.
 * @param {string} fromTime - Requirement from time in "HH:mm" format.
 * @param {number} [minGapHours=1] - Minimum required gap in hours (default: 1).
 * @return {string} Minimum allowed to-time in "HH:mm" format.
 */
export const getMinToTime = (fromDate, toDate, fromTime, minGapHours = 1) => {
  const minBase = getMinDeliveryTime(toDate);
  if (fromDate === toDate && fromTime) {
    const minRequired = getTimeAfterHours(fromTime, minGapHours);
    return minRequired > minBase ? minRequired : minBase;
  }
  return minBase;
};

/**
 * Reusable Toast notification component with built-in auto-dismiss timer.
 *
 * @param {Object} props - Component properties.
 * @param {Object} props.showToast - Toast configuration object with error flag and message label.
 * @param {Function} props.setShowToast - State updater function to dismiss toast.
 * @param {Function} [props.onClose] - Optional custom close callback handler.
 * @param {number} [props.autoCloseTime=3000] - Duration in milliseconds before auto-dismiss (default: 3000).
 * @return {JSX.Element|null} Toast component if visible, null otherwise.
 */
export const CustomToast = ({ showToast, setShowToast, onClose, autoCloseTime = 3000 }) => {
  useEffect(() => {
    if (!showToast) return;
    const timer = setTimeout(() => (onClose ? onClose() : setShowToast?.(null)), autoCloseTime);
    return () => clearTimeout(timer);
  }, [showToast, autoCloseTime]);

  if (!showToast) return null;
  return <Toast error={showToast.error} label={showToast.label} onClose={onClose || (() => setShowToast?.(null))} />;
};

 // for replacing digit-ui 
export const APPLICATION_PATH = "/upyog-ui";

//Custom function which will return the formdata and inside formdata we are building the Payload.
export const waterTankerPayload = (data) =>{
  const formdata={
    waterTankerBookingDetail: {
        tenantId: data?.tenantId,
        tankerType: data?.requestDetails?.tankerType?.code,
        waterType: data?.requestDetails?.waterType?.code,
        tankerQuantity: data?.requestDetails?.tankerQuantity?.code,
        waterQuantity: data?.requestDetails?.waterQuantity?.code,
        description: data?.requestDetails?.description,
        deliveryDate: data?.requestDetails?.deliveryDate,
        deliveryTime: data?.requestDetails?.deliveryTime,
        extraCharge: (data?.requestDetails?.extraCharge) ? "Y":"N",
        addressDetailId: data?.address?.addressDetailId || "",
        applicantDetail: {
            name: data?.owner?.applicantName,
            mobileNumber: data?.owner?.mobileNumber,
            alternateNumber: data?.owner?.alternateNumber,
            emailId: data?.owner?.emailId,
        },
        address: {
            addressType:data?.address?.addressType?.code,
            pincode: data?.address?.pincode,
            city: data?.address?.city?.city?.name,
            cityCode: data?.address?.city?.city?.code,
            addressLine1: data?.address?.addressLine1,
            addressLine2: data?.address?.addressLine2,
            locality: data?.address?.locality?.i18nKey,
            localityCode: data?.address?.locality?.code,
            streetName: data?.address?.streetName,
            houseNo: data?.address?.houseNo,
            landmark: data?.address?.landmark
        },
        
        bookingStatus: "BOOKING_CREATED",
        workflow:{
          action:"APPLY",
          comments:"",
          businessService:"watertanker",
          moduleName:"request-service.water_tanker",
        }
    },
  };
  return formdata;
}
  export const mobileToiletPayload = (data) =>{
  const formdata={
    mobileToiletBookingDetail: {
        tenantId:data?.tenantId,
        description: data?.toiletRequestDetails?.specialRequest,
        noOfMobileToilet : data?.toiletRequestDetails?.mobileToilet?.code,
        deliveryFromDate: data?.toiletRequestDetails?.deliveryfromDate,
        deliveryToDate: data?.toiletRequestDetails?.deliverytoDate,
        deliveryFromTime: data?.toiletRequestDetails?.deliveryfromTime,
        deliveryToTime: data?.toiletRequestDetails?.deliverytoTime,
        addressDetailId: data?.address?.addressDetailId || "",
        applicantDetail: {
            name: data?.owner?.applicantName,
            mobileNumber: data?.owner?.mobileNumber,
            alternateNumber: data?.owner?.alternateNumber,
            emailId: data?.owner?.emailId,
        },
        address: {
            addressType:data?.address?.addressType?.code,
            pincode: data?.address?.pincode,
            city: data?.address?.city?.city?.name,
            cityCode: data?.address?.city?.city?.code,
            addressLine1: data?.address?.addressLine1,
            addressLine2: data?.address?.addressLine2,
            locality: data?.address?.locality?.i18nKey,
            localityCode: data?.address?.locality?.code,
            streetName: data?.address?.streetName,
            houseNo: data?.address?.houseNo,
            landmark: data?.address?.landmark
        },
        
        bookingStatus: "BOOKING_CREATED",
        workflow:{
          action:"APPLY",
          comments:"",
          businessService:"mobileToilet",
          moduleName:"request-service.mobile_toilet",
        }
    },
  };
  return formdata;
}

  export const treePruningPayload = (data) =>{
  const formdata={
    treePruningBookingDetail: {
        tenantId:data?.tenantId,
        latitude: data?.treePruningRequestDetails?.latitude,
        longitude:data?.treePruningRequestDetails?.longitude,
        reasonForPruning: data?.treePruningRequestDetails?.reasonOfPruning?.code,
        applicantDetail: {
            name: data?.owner?.applicantName,
            mobileNumber: data?.owner?.mobileNumber,
            alternateNumber: data?.owner?.alternateNumber,
            emailId: data?.owner?.emailId,
        },
         documentDetails: [
            {
                documentType: "Site Photograph",
                fileStoreId: data?.treePruningRequestDetails?.supportingDocumentFile,
            }
        ],
        additionalDetails: {
            digipin: data?.treePruningRequestDetails?.digipin || "",
        },
        address: {
            addressType:data?.address?.addressType?.code,
            pincode: data?.address?.pincode,
            city: data?.address?.city?.city?.name,
            cityCode: data?.address?.city?.city?.code,
            addressLine1: data?.address?.addressLine1,
            addressLine2: data?.address?.addressLine2,
            locality: data?.address?.locality?.i18nKey,
            localityCode: data?.address?.locality?.code,
            streetName: data?.address?.streetName,
            houseNo: data?.address?.houseNo,
            landmark: data?.address?.landmark
        },
        
        bookingStatus: "BOOKING_CREATED",
        workflow:{
          action:"APPLY",
          comments:"",
          businessService:"treePruning",
          moduleName:"request-service.tree_pruning"
        }
    }
  };
  return formdata;
}
