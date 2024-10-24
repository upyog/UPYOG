// Determines if the back button should be hidden based on the current URL matching screen paths in the config array.
// Returns true to hide the back button if a match is found; otherwise, returns false.
export const shouldHideBackButton = (config = []) => {
  return config.filter((key) => window.location.href.includes(key.screenPath)).length > 0 ? true : false;
};

export const checkForNotNull = (value = "") => {
  return value && value != null && value != undefined && value != "" ? true : false;
};

export const checkForNA = (value = "") => {
  return checkForNotNull(value) ? value : "CS_NA";
};

export const pdfDownloadLink = (documents = {}, fileStoreId = "", format = "") => {
  /* Need to enhance this util to return required format*/

  let downloadLink = documents[fileStoreId] || "";
  let differentFormats = downloadLink?.split(",") || [];
  let fileURL = "";
  differentFormats.length > 0 &&
    differentFormats.map((link) => {
      if (!link.includes("large") && !link.includes("medium") && !link.includes("small")) {
        fileURL = link;
      }
    });
  return fileURL;
};

/**
 * Payload create api data for the advertisement booking application.
 * 
 * setAddressDetails(data)`: Formats and sets address details.
 * setApplicantDetails(data)`: Formats and sets applicant information.
 * setDocumentDetails(data)`: Retains document details.
 * ADSDataConvert(data)`: Combines all details into a structured bookingApplication object for submission.
 */

export const setaddressDetails = (data) => {
  let { address } = data;

  let addressdetails = {
    pincode: address?.pincode,
    city: address?.city?.city?.name,
    cityCode:address?.city?.city?.code,
    locality:address?.locality?.i18nKey,
    localityCode: address?.locality?.code,
    streetName: address?.streetName,
    addressLine1:address?.addressline1,
    addressLine2:address?.addressline2,
    houseNo: address?.houseNo,
    landmark: address?.landmark,
  };

  data.address = addressdetails;
  return data;

};

export const setApplicantDetails = (data) => {
    let { applicant } = data;
  
    let propApplicant = {
      applicantName:applicant?.applicantName,
      applicantMobileNo:applicant?.mobileNumber,
      applicantAlternateMobileNo:applicant?.alternateNumber,
      applicantEmailId:applicant?.emailId,
    };
  
    data.applicant = propApplicant;
    return data;
  };

  export const setDocumentDetails = (data) => {
    let { documents } = data;
  
    let doc = {
      ...documents,
       
      
    };
  
    data.documents = doc;
    return data;
  };



export const ADSDataConvert = (data) => {
 
  data = setDocumentDetails(data);
  data = setApplicantDetails(data);
  data=setaddressDetails(data);
const formdata={
  bookingApplication: {
    tenantId: data.tenantId,
    applicantDetail:{
      ...data.applicant
    },
    address:data.address,
    ...data.documents,
    bookingStatus:"BOOKING_CREATED",
    bookingSlotDetails:data?.slotlist,
    cartDetails:[ {
                "addType": "Unipolar",
                "faceArea": "Unipole 12 X 8",
                "location": "Green Park",
                "nightLight": false,
                "bookingDate": "2024-11-30",
                "bookingFromTime": "06:00",
                "bookingToTime": "05:59",
                "status": "BOOKING_CREATED"
                },],

    workflow:null
  }
}

  return formdata;
};
