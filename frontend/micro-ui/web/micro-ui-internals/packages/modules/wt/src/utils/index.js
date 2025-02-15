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


//Custom function which will return the formdata and inside formdata we are building the Payload.
export const waterTankerPayload = (data) =>{
  const formdata={
    waterTankerBookingDetail: {
        tenantId: data?.tenantId,
        tankerType: data?.requestDetails?.tankerType?.code,
        tankerQuantity: data?.requestDetails?.tankerQuantity,
        waterQuantity: data?.requestDetails?.waterQuantity?.code,
        description: data?.requestDetails?.description,
        deliveryDate: data?.requestDetails?.deliveryDate,
        deliveryTime: data?.requestDetails?.deliveryTime,
        extraCharge: (data?.requestDetails?.extraCharge) ? "Y":"N",

        applicantDetail: {
            name: data?.owner?.applicantName,
            mobileNumber: data?.owner?.mobileNumber,
            alternateNumber: data?.owner?.alternateNumber,
            emailId: data?.owner?.emailId,
            gender: data?.owner?.gender?.code.charAt(0)
        },
        address: {
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
          moduleName:"request-service",
        }
    },
  };
  return formdata;
}
