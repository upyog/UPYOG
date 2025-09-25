/**
 * Utility Functions for Pet Registration and Data Conversion
 *
 * This file contains various helper functions used for:
 * - Checking for null, undefined, and empty values.
 * - Converting dot-separated values to underscore-separated values.
 * - Formatting filenames and handling back button visibility.
 * - Setting address, owner, and pet details in the data object.
 * - Comparing two objects for equality.
 * - Converting between date and epoch formats.
 * - Generating PDF download links and extracting document names.
 * - Checking if an object is an array and verifying its length.
 * - Handling workflow details for pet registration.
 * - Downloading receipts using external services.
 *
 */

import { add } from "lodash";

export const checkForNotNull = (value = "") => {
  return value && value != null && value != undefined && value != "" ? true : false;
};

export const convertDotValues = (value = "") => {
  return (
    (checkForNotNull(value) && ((value.replaceAll && value.replaceAll(".", "_")) || (value.replace && stringReplaceAll(value, ".", "_")))) || "NA"
  );
};



export const getFixedFilename = (filename = "", size = 5) => {
  if (filename.length <= size) {
    return filename;
  }
  return `${filename.substr(0, size)}...`;
};

export const shouldHideBackButton = (config = []) => {
  return config.filter((key) => window.location.href.includes(key.screenPath)).length > 0 ? true : false;
};


export const setAddressDetails = (data) => {
  let { address } = data;
  let propAddress = {
    // ...address,
    pincode: address?.pincode,
    landmark: address?.landmark,
    city: address?.city?.name,
    doorNo: address?.houseNo || null,
    street: address?.streetName || null,
    locality: {
      code: address?.locality?.i18nKey || "NA",
      area: address?.locality?.name,
    },
    buildingName: address?.houseName,
    addressLine1: address?.addressline1,
    addressLine2: address?.addressline2,
    propertyId: address?.propertyId || null,
  };

  data.address = propAddress;
  return data;
};

export const setOwnerDetails = (data) => {
  let { ownerss } = data;

  let propOwners = {
    ...ownerss,

  };

  data.ownerss = propOwners;
  return data;
};

export const setPetDetails = (data) => {
  let { pets } = data;

  let petDetails = {
    // ...pets,
    petType: pets?.petType?.value,
    breedType: pets?.breedType?.value,
    petGender: pets?.petGender?.code,
    birthDate: pets?.birthDate ? convertDateToEpoch(pets?.birthDate) : null,
    adoptionDate: pets?.adoptionDate ? convertDateToEpoch(pets?.adoptionDate) : null,
    identificationMark: pets?.identificationMark,
    petColor: pets?.petColor?.colourCode,
    clinicName: pets?.clinicName,
    petName: pets?.petName,
    doctorName: pets?.doctorName,
    lastVaccineDate: pets?.lastVaccineDate,
    petAge: pets?.petAge,
    vaccinationNumber: pets?.vaccinationNumber

  };

  data.pets = petDetails;
  return data;
};

export const PetDataConvert = (data) => {
  data = setOwnerDetails(data);
  data = setAddressDetails(data);
  data = setPetDetails(data);

  const formdata = {
    PetRegistrationApplications: [{
      tenantId: data.tenantId,
      ...data?.ownerss,
      applicationType: sessionStorage.getItem("applicationType"), 
      validityDate: data?.validityDate || null,
      status: data?.status || null,
      expireflag: false ,
      petToken: sessionStorage.getItem("petToken")||"",
      previousapplicationnumber: sessionStorage.getItem("petId"),
      address: data.address,
      petDetails: data.pets,
      ...data?.documents,
      propertyId: data?.address?.propertyId,

      workflow: {
        businessService: "ptr",
        action: "APPLY",
        moduleName: "pet-services"
      }
    }],
  };


  return formdata;
};

export const CompareTwoObjects = (ob1, ob2) => {
  let comp = 0;
  Object.keys(ob1).map((key) => {
    if (typeof ob1[key] == "object") {
      if (key == "institution") {
        if ((ob1[key].name || ob2[key].name) && ob1[key]?.name !== ob2[key]?.name)
          comp = 1
        else if (ob1[key]?.type?.code !== ob2[key]?.type?.code)
          comp = 1

      }
      else if (ob1[key]?.code !== ob2[key]?.code)
        comp = 1
    }
    else {
      if ((ob1[key] || ob2[key]) && ob1[key] !== ob2[key])
        comp = 1
    }
  });
  if (comp == 1)
    return false
  else
    return true;
}

/*   method to check value  if not returns NA*/
export const checkForNA = (value = "") => {
  return checkForNotNull(value) ? value : "PTR_NA";
};

/*   method to get required format from fielstore url*/
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

/*   method to get filename  from fielstore url*/
export const pdfDocumentName = (documentLink = "", index = 0) => {
  let documentName = decodeURIComponent(documentLink.split("?")[0].split("/").pop().slice(13)) || `Document - ${index + 1}`;
  return documentName;
};

/* method to get date from epoch */
export const convertEpochToDate = (dateEpoch) => {
  // Returning null in else case because new Date(null) returns initial date from calender
  if (dateEpoch) {
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    return `${year}-${month}-${day}`;
  } else {
    return null;
  }
};

export const convertDateToEpoch = (dateString, dayStartOrEnd = "dayend") => {
  //example input format : "2018-10-02"
  try {
    const parts = dateString.match(/(\d{4})-(\d{1,2})-(\d{1,2})/);
    const DateObj = new Date(Date.UTC(parts[1], parts[2] - 1, parts[3]));
    DateObj.setMinutes(DateObj.getMinutes() + DateObj.getTimezoneOffset());
    if (dayStartOrEnd === "dayend") {
      DateObj.setHours(DateObj.getHours() + 24);
      DateObj.setSeconds(DateObj.getSeconds() - 1);
    }
    return DateObj.getTime();
  } catch (e) {
    return dateString;
  }
};

export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
  if (searcher == "") return str;
  while (str.includes(searcher)) {
    str = str.replace(searcher, replaceWith);
  }
  return str;
};

export const DownloadReceipt = async (consumerCode, tenantId, businessService, pdfKey = "consolidatedreceipt") => {
  tenantId = tenantId ? tenantId : Digit.ULBService.getCurrentTenantId();
  await Digit.Utils.downloadReceipt(consumerCode, businessService, "consolidatedreceipt", tenantId);
};

export const checkIsAnArray = (obj = []) => {
  return obj && Array.isArray(obj) ? true : false;
};
export const checkArrayLength = (obj = [], length = 0) => {
  return checkIsAnArray(obj) && obj.length > length ? true : false;
};

export const getWorkflow = (data = {}) => {
  return {

    businessService: `ptr`,
    moduleName: "pet-services",
  };
};

