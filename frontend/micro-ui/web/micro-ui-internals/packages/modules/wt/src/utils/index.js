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



export const waterTankerPayload = (data) =>{
  console.log("datatatattatatta",data);
  const formdata={
    waterTankerBookingDetail: {
        tenantId: data?.tenantId,
        tankerType: data?.requestDetails?.tankerType?.code,
        tankerQuantity: data?.requestDetails?.tankerQuantity,
        waterQuantity: data?.requestDetails?.waterQuantity,
        description: data?.requestDetails?.description,
        deliveryDate: data?.requestDetails?.deliveryDate,
        deliveryTime: data?.requestDetails?.deliveryTime,
        extraCharge: "NO",

        applicantDetail: {
            applicantName: data?.owner?.applicantName,
            applicantMobileNo: data?.owner?.mobileNumber,
            applicantAlternateMobileNo: data?.owner?.alternateNumber,
            applicantEmailId: data?.owner?.emailId,
            gender: data?.owner?.gender?.code.charAt(0)
        },
        address: {
            pincode: data?.address?.pincode,
            city: data?.address?.city?.name,
            cityCode: data?.address?.city?.code,
            addressLine1: data?.address?.addressLine1,
            addressLine2: data?.address?.addressLine2,
            locality: data?.address?.locality?.i18nKey,
            localityCode: data?.address?.locality?.code,
            streetName: data?.address?.streetName,
            houseNo: data?.address?.houseNo,
            landmark: data?.address?.landmark
        },
        
        bookingStatus: "BOOKING_CREATED",
        workflow: null
    },
  };
  return formdata;
}
