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
const formdata={
  // service: {
  //   tenantId: data.tenantId ,
  //   serviceCode: data.newGrievance.grievanceSubType ,
  //   description: data.newGrievance.description, // Added description
  //   additionalDetail: {}, // Added empty additionalDetail object
  //   source: "web", // Added source field
  //   address: {
  //     landmark: data.newGrievance.addressDetails?.landmark ,
  //     city: data.newGrievance.addressDetails?.city,
  //     district: data.newGrievance.addressDetails?.district || "New Delhi",
  //     region: data.newGrievance.addressDetails?.state_Code,
  //     state: data.newGrievance.addressDetails?.state || "Demo",
  //     pincode: data.newGrievance.addressDetails?.pincode,
  //     locality: data.newGrievance.addressDetails?.locality,
  //     geoLocation: data.newGrievance.addressDetails?.geoLocation || {}
  //     // address:data?.newGrievance.addressDetails,
  //   },
  //   serviceType: data.newGrievance.grievanceType,
  //   inputGrievance: data.newGrievance.grievance ,
  //   "workflow": {
  //     "action": "APPLY",
  //     "verificationDocuments": [
  //         {
  //             "documentType": "PHOTO",
  //             "fileStoreId": "8d0ebd2b-ac6b-40f7-9a5d-1d96b4756af2",
  //             "documentUid": "",
  //             "additionalDetails": {}
  //         }
  //     ]
  // }
  // }
  service: {
    tenantId: data.tenantId,
    serviceCode: data.newGrievance.grievanceSubType,
    serviceType: data.newGrievance.grievanceType,
    inputGrievance: data.newGrievance.grievance,
    // description: data.newGrievance.description || "dd", // Added description
    additionalDetail: {}, // As in original
    source: "web", // As in original
    address: {
      landmark: data.newGrievance.addressDetails?.landmark,
      city: data.newGrievance.addressDetails?.city || "dd",
      district: data.newGrievance.addressDetails?.district || "New Delhi",
      region: data.newGrievance.addressDetails?.state_Code,
      state: data.newGrievance.addressDetails?.state || "Demo",
      pincode: data.newGrievance.addressDetails?.pincode,
      locality: {
        code: data.newGrievance.addressDetails?.locality?.code || "dd",
        name: data.newGrievance.addressDetails?.locality?.name || "dd"
      },
      geoLocation: {
        latitude: data.newGrievance.addressDetails?.geoLocation?.latitude || null,
        longitude: data.newGrievance.addressDetails?.geoLocation?.longitude || null,
        additionalDetails: data.newGrievance.addressDetails?.geoLocation?.additionalDetails || null
      }
    },
   
  } ,
  workflow: {
    action: "APPLY",
    verificationDocuments: [
      {
        documentType: "",
        fileStoreId: "",
        documentUid: "",
        additionalDetails: {}
      }
    ]
  }


}

  return formdata;
};
