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

export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
  if (searcher == "") return str;
  while (str.includes(searcher)) {
    str = str.replace(searcher, replaceWith);
  }
  return str;
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
export const setCartDetails = (data) => {
  let { adslist } = data;
  
  let cartDetails = adslist?.cartDetails.map((slot) => {
    return { 
      addType:slot.addTypeCode,
      faceArea:slot.faceAreaCode,
      location:slot.locationCode,
      nightLight:slot.nightLight,
      bookingDate:slot.bookingDate,
      bookingFromTime: "06:00",
      bookingToTime: "05:59",
      status:"BOOKING_CREATED"
    };

  }) || [];
  let draftId=adslist?.existingDataSet?.draftId;

  data.adslist = {cartDetails,draftId};
  return data;
}
export const setApplicantDetails = (data) => {
    let { applicant } = data;
  
    let Applicant = {
      applicantName:applicant?.applicantName,
      applicantMobileNo:applicant?.mobileNumber,
      applicantAlternateMobileNo:applicant?.alternateNumber,
      applicantEmailId:applicant?.emailId,
    };
    let draftId=applicant?.draftId;
    data.applicant = {Applicant,draftId};
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


// ADSDataConvert(data)`: Combines all details into a structured bookingApplication object for submission.
export const ADSDataConvert = (data) => {
 
  data = setDocumentDetails(data);
  data = setApplicantDetails(data);
  data=setaddressDetails(data);
  data=setCartDetails(data);
const formdata={
  bookingApplication: {
    tenantId: data.tenantId,
    draftId:data.applicant.draftId,
    applicantDetail:{
      ...data.applicant.Applicant
    },
    address:data.address,
    ...data.documents,
    bookingStatus:"BOOKING_CREATED",
    cartDetails:data.adslist.cartDetails,

    workflow:null
  }
}

  return formdata;
};
