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


  export const setDocumentDetails = (data) => {
    let { documents } = data;
  
    let doc = {
      ...documents,
       
      
    };
  
    data.documents = doc;
    return data;
  };

  export const APPLICATION_PATH = "/digit-ui";

export const DataConvert = (data) => {
   
 const formdata = {
  service: {
    tenantId: data.tenantId,
    serviceCode: data.newGrievance.grievanceSubType,
    serviceType: data.newGrievance.grievanceType,
    inputGrievance: data.newGrievance.grievance,
    additionalDetail: {},
        source: "web",
    address: {
      landmark: data.newGrievance.addressDetails?.landmark,
      city: data.newGrievance.addressDetails?.city?.name || data.newGrievance.addressDetails?.city,
      district: data.newGrievance.addressDetails?.city?.district,
      region: data.newGrievance.addressDetails?.state_Code,
      state: data.newGrievance.addressDetails?.state,
    pincode: data.newGrievance.addressDetails?.pincode,
        locality: {
       code: data.newGrievance.addressDetails?.locality?.code||"dfd",
            name: data.newGrievance.addressDetails?.locality?.name || data.newGrievance.addressDetails?.locality?.i18nKey || "dfd"
          },
          geoLocation: data.newGrievance.geoLocation,
        }
  },
    workflow: {
      action: "APPLY",
      verificationDocuments: data.newGrievance?.verificationDocuments
    }
  };

  return formdata;
};
