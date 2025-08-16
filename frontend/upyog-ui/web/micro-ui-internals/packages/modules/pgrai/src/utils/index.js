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
 /**
 * Fetches grievance categories based on the provided prompt.
 * Makes an API call to retrieve a list of categories and returns the data.
 * This will be used to populate the grievance categories in the application.
 */
export const fetchGrievanceCategories = async (prompt, t) => {
  try {
    const response = await fetch(`https://samar.iitk.ac.in/iitk_upyog_api/search_category/?prompt=${encodeURIComponent(prompt)}&threshold=1.5`, {
      method: "GET",
      headers: { accept: "application/json" },
    });

    if (!response.ok) throw new Error(t("PGR_AI_FETCH_CATEGORIES_ERROR"));

    const data = await response.json();
    if (data && data.length > 0) {
      // Use the code field
      return data.map(item => ({
        ...item,
        subtype: item.code 
      }));
    }
    return [];
  } catch (error) {
    console.error("API Error:", error);
    throw error;
  }
};

  export const APPLICATION_PATH = "/upyog-ui";

export const DataConvert = (data,user) => {
   
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
       code: data.newGrievance.addressDetails?.locality?.code||"JLC476",
            name: data.newGrievance.addressDetails?.locality?.name || data.newGrievance.addressDetails?.locality?.i18nKey || "JLC476",
          },
          geoLocation: data.newGrievance.geoLocation,
        },
      },
      workflow: {
        action: "APPLY",
        verificationDocuments: data.newGrievance?.verificationDocuments
      }
    };
  
    if (user.type === "EMPLOYEE") {
      formdata.service.citizen = {
          type: "CITIZEN",
          name: data.newGrievance?.name || data.newGrievance?.citizenName,
          mobileNumber: data.newGrievance?.mobileNumber || data.newGrievance?.citizenMobile,
          roles: [
            {
              id: null,
              name: "Citizen",
              code: "CITIZEN",
              tenantId: data.tenantId,
            },
          ],
          tenantId: data.tenantId,
  };
    }

  return formdata;
};
