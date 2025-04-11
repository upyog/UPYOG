// Utility function to check if a value is not null, undefined, or an empty string
export const checkForNotNull = (value = "") => {
  return value && value != null && value != undefined && value != "" ? true : false;
};

// Utility function to replace all dots in a string with underscores
export const convertDotValues = (value = "") => {
  return (
    (checkForNotNull(value) && ((value.replaceAll && value.replaceAll(".", "_")) || (value.replace && stringReplaceAll(value, ".", "_")))) || "NA"
  );
};

// Utility function to truncate a filename to a fixed size and append ellipsis
export const getFixedFilename = (filename = "", size = 5) => {
  if (filename.length <= size) {
    return filename;
  }
  return `${filename.substr(0, size)}...`;
};

// Utility function to determine if the back button should be hidden based on the current URL
export const shouldHideBackButton = (config = []) => {
  return config.filter((key) => window.location.href.includes(key.screenPath)).length > 0 || window.location.href.includes("acknowledgement")
    ? true
    : false;
};

// Utility function to set address details in the provided data object
export const setAddressDetails = (data) => {
  let { address } = data;

  let propAddress = {
    ...address,
    pincode: address?.pincode,
    landmark: address?.landmark,
    city: address?.city?.name,
    doorNo: address?.doorNo,
    street: address?.street,
    locality: {
      code: address?.locality?.code || "NA",
      area: address?.locality?.name,
    },
  };

  data.address = propAddress;
  return data;
};

// Utility function to set product details in the provided data object
export const setProductDetails = (data) => {
  let { ewdet } = data;

  let productDetails =
    ewdet?.prlistName.map((product, index) => {
      return {
        productId: "",
        productName: product.code,
        quantity: ewdet?.prlistQuantity[index].code,
        price: product.price * ewdet?.prlistQuantity[index].code,
      };
    }) || [];

  data.calculatedAmount = ewdet.calculatedAmount;
  data.ewdet = productDetails;
  return data;
};

// Utility function to convert E-Waste data into the required format
export const EWDataConvert = (data) => {
  data = setProductDetails(data);
  data = setAddressDetails(data);

  const formdata = {
    EwasteApplication: [
      {
        tenantId: data?.tenantId,
        requestId: data.requestId || "",
        transactionId: "",
        pickUpDate: "",
        vendorUuid: "345",
        requestStatus: "New Request",
        calculatedAmount: data?.calculatedAmount || null,
        applicant: {
          applicantName: data?.ownerKey?.applicantName,
          mobileNumber: data?.ownerKey?.mobileNumber,
          emailId: data?.ownerKey?.emailId,
          altMobileNumber: data?.ownerKey?.altMobileNumber,
        },
        ewasteDetails: data?.ewdet,
        address: {
          tenantId: data?.tenantId,
          doorNo: data.address?.doorNo,
          latitude: data.address?.latitude || null,
          longitude: data.address?.longitude || null,
          addressNumber: data.address?.addressNumber || "",
          type: data.address?.type || "RESIDENTIAL",
          addressLine1: data.address?.addressLine1 || "",
          addressLine2: data.address?.addressLine2 || "",
          landmark: data.address?.landmark || "",
          city: data.address?.city || "",
          pincode: data.address?.pincode || "",
          detail: data.address?.detail || "",
          buildingName: data.address?.buildingName || "",
          street: data.address?.street || "",
          locality: {
            code: data.address?.locality?.code || "NA",
            name: data.address?.locality?.name || "",
          },
        },
        documents: data.documents.documents || [],
        workflow: {
          businessService: "ewst",
          action: "CREATE",
          moduleName: "ewaste-services",
        },
      },
    ],
  };

  return formdata;
};

// Utility function to compare two objects and check if they are equal
export const CompareTwoObjects = (ob1, ob2) => {
  let comp = 0;
  Object.keys(ob1).map((key) => {
    if (typeof ob1[key] == "object") {
      if (key == "institution") {
        if ((ob1[key].name || ob2[key].name) && ob1[key]?.name !== ob2[key]?.name) comp = 1;
        else if (ob1[key]?.type?.code !== ob2[key]?.type?.code) comp = 1;
      } else if (ob1[key]?.code !== ob2[key]?.code) comp = 1;
    } else {
      if ((ob1[key] || ob2[key]) && ob1[key] !== ob2[key]) comp = 1;
    }
  });
  if (comp == 1) return false;
  else return true;
};

// Utility function to check if a value is not null; returns "EWASTE_NA" if null
export const checkForNA = (value = "") => {
  return checkForNotNull(value) ? value : "EWASTE_NA";
};

// Utility function to extract the download link for a PDF document
export const pdfDownloadLink = (documents = {}, fileStoreId = "", format = "") => {
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

// Utility function to extract the filename from a file store URL
export const pdfDocumentName = (documentLink = "", index = 0) => {
  let documentName = decodeURIComponent(documentLink.split("?")[0].split("/").pop().slice(13)) || `Document - ${index + 1}`;
  return documentName;
};

// Utility function to convert an epoch timestamp to a formatted date
export const convertEpochToDate = (dateEpoch, businessService) => {
  if (dateEpoch) {
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    if (businessService == "ewst") return `${day}-${month}-${year}`;
    else return `${day}/${month}/${year}`;
  } else {
    return null;
  }
};

// Utility function to replace all occurrences of a substring in a string
export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
  if (searcher == "") return str;
  while (str.includes(searcher)) {
    str = str.replace(searcher, replaceWith);
  }
  return str;
};

// Utility function to download a receipt for a given consumer code and business service
export const DownloadReceipt = async (consumerCode, tenantId, businessService, pdfKey = "consolidatedreceipt") => {
  tenantId = tenantId ? tenantId : Digit.ULBService.getCurrentTenantId();
  await Digit.Utils.downloadReceipt(consumerCode, businessService, "consolidatedreceipt", tenantId);
};

// Utility function to check if an object is an array
export const checkIsAnArray = (obj = []) => {
  return obj && Array.isArray(obj) ? true : false;
};

// Utility function to check if an array has a length greater than a specified value
export const checkArrayLength = (obj = [], length = 0) => {
  return checkIsAnArray(obj) && obj.length > length ? true : false;
};

// Utility function to get workflow details for E-Waste
export const getWorkflow = (data = {}) => {
  return {
    businessService: `ewst`,
    moduleName: "ewaste-services",
  };
};