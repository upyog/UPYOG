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

/*   method to check value  if not returns NA*/
export const checkForNA = (value = "") => {
  return checkForNotNull(value) ? value : "NA";
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

/* methid to get date from epoch */
export const convertDateToEpoch = (dateString) => {
  // Parse the date string into a Date object
  const date = new Date(dateString);

  // Check if the date is valid
  if (!isNaN(date)) {
    // Return the epoch time in seconds (divide by 1000 to convert from milliseconds to seconds)
    return Math.floor(date.getTime() / 1000);
  } else {
    return null; // Return null if the input date is invalid
  }
};

export const convertStringToFloat = (amountString) => {
  // Remove commas if present and convert to float
  const cleanedString = amountString.replace(/,/g, "");

  // Convert to float and return
  const floatValue = parseFloat(cleanedString);

  // Return the float value, or NaN if conversion fails
  return isNaN(floatValue) ? null : floatValue;
};

//prepare payload for UP
export const assetDataUP = (data) => {
  const keysToRemove = [
    "acquisitionCost",
    "acquisitionDate",
    "assetDescription",
    "assetName",
    "assetUniqueNo",
    "assetclassification",
    "assetsubtype",
    "assettype",
    "otherCategory"
  ];
  const assetDetails = data?.assetDetails || {};
  const filteredAdditionalDetails = Object.keys(assetDetails)
    .filter((key) => !keysToRemove.includes(key)) // Remove unwanted keys
    .reduce((acc, key) => {
      acc[key] = assetDetails[key]; // Add remaining keys to the accumulator
      return acc;
    }, {});
  const payLoadData = {
    Asset: {
      id: "",
      tenantId: "pg.citya",
      assetName: data?.assetDetails?.assetName,
      description: data?.assetDetails?.assetDescription,
      assetClassification: data?.assetDetails?.assetclassification?.code,
      assetParentCategory: data?.assetDetails?.assettype?.code,
      assetCategory: data?.assetDetails?.assetsubtype?.code,
      assetSubCategory: data?.assetDetails?.otherCategory,
      acquisitionCost: convertStringToFloat(data?.assetDetails?.acquisitionCost),
      purchaseDate: convertDateToEpoch(data?.assetDetails?.acquisitionDate),
      assetUniqueNo: data?.assetDetails?.assetUniqueNo,
      status:"ACTIVE",
      additionalDetails: filteredAdditionalDetails
    },
    
  };

  return payLoadData;
};


export const Assetdata = (data) => {
  // Define the keys to exclude from additionalDetails
  const keysToRemove = [
    "acquisitionCost",
    "assetAge",
    "bookValue",
    "invoiceDate",
    "invoiceNumber",
    "purchaseDate",
    "purchaseOrderNumber",
    "purchaseCost",
    "location",
    "lifeOfAsset",
  ];

  // Ensure data?.assetDetails exists before attempting to filter
  const assetDetails = data?.assetDetails || {}; // Default to empty object if undefined or null

  // Filter out the keys to remove from assetDetails
  const filteredAdditionalDetails = Object.keys(assetDetails)
    .filter((key) => !keysToRemove.includes(key)) // Remove unwanted keys
    .reduce((acc, key) => {
      acc[key] = assetDetails[key]; // Add remaining keys to the accumulator
      return acc;
    }, {});
  const formdata = {
    Asset: {
      accountId: "",
      tenantId: data?.address?.city?.code,
      assetBookRefNo: data?.asset?.BookPagereference,
      assetName: data?.asset?.AssetName,
      description: data?.asset?.Assetdescription,
      assetClassification: data?.asset?.assetclassification?.code,
      vendorDetail: data?.asset?.vendorDetail?.code,
      assetParentCategory: data?.asset?.assettype?.code,
      assetCategory: data?.asset?.assetsubtype?.code,
      assetSubCategory: data?.asset?.assetparentsubCategory?.code,
      department: data?.asset?.Department?.code,
      // assetType: data?.asset?.assetsOfType?.code,
      assetUsage: data?.asset?.assetsUsage?.code,
      assetIsAssigned: data?.asset?.assetAssignable?.code,
      financialYear: data?.asset?.financialYear?.code,
      sourceOfFinance: data?.asset?.sourceOfFinance?.code,
      applicationNo: "",
      approvalDate: "",
      applicationDate: "",
      status: "",
      modeOfPossessionOrAcquisition: data?.assetDetails?.modeOfPossessionOrAcquisition?.code,
      invoiceDate: convertDateToEpoch(data?.assetDetails?.invoiceDate),
      invoiceNumber: data?.assetDetails?.invoiceNumber,
      purchaseDate: convertDateToEpoch(data?.assetDetails?.purchaseDate),
      purchaseOrderNumber: data?.assetDetails?.purchaseOrderNumber,
      assetAge: data?.assetDetails?.assetAge,
      location: data?.assetDetails?.location,
      lifeOfAsset: data?.assetDetails?.lifeOfAsset,
      purchaseCost: convertStringToFloat(data?.assetDetails?.purchaseCost),
      acquisitionCost: convertStringToFloat(data?.assetDetails?.acquisitionCost),
      bookValue: data?.assetDetails?.bookValue,
      originalBookValue: data?.assetDetails?.bookValue,
      islegacyData: false,
      minimumValue: 0,
      assetStatus: "1",
      action: "",
      businessService: "asset-create",

      addressDetails: {
        addressLine1: data?.address?.addressLine1,
        addressLine2: data?.address?.addressLine2,
        buildingName: data?.address?.buildingName,
        doorNo: data?.address?.doorNo,
        street: data?.address?.street,
        pincode: data?.address?.pincode,
        city: data?.address?.city?.name,
        locality: {
          code: data?.address?.locality?.code,
          area: data?.address?.locality?.area,
          latitude: data?.address?.latitude,
          longitude: data?.address?.longitude,
        },
      },
      documents: data?.documents?.documents,
      workflow: {
        action: "INITIATE",
        businessService: "asset-create",
        moduleName: "asset-services",
      },

      additionalDetails: filteredAdditionalDetails,
    },
  };

  return formdata;
};

export const InventoryVendorData = (data) => {
  
  const payLoadInventoryVendor = {
    Vendor: {
      vendorId: "",
      tenantId: "pg.citya",
      vendorName: data?.inventoryVendor?.vendorName,
      contactPerson: data?.inventoryVendor?.contactPerson,
      vendorNumber: data?.inventoryVendor?.vendorNumber,
      contactNumber: data?.inventoryVendor?.contactNumber,
      contactEmail: data?.inventoryVendor?.contactEmail,
      gstin: data?.inventoryVendor?.gstin,
      pan: data?.inventoryVendor?.pan,
      vendorAddress: data?.inventoryVendor?.vendorAddress,
      identificationNo: data?.inventoryVendor?.identificationNo,
      status:"ACTIVE"
    }
  };
  return payLoadInventoryVendor;
};

export const ProcerementData = (data) => {
  
  const payLoadProcerement = {
    ProcurementRequest: {
      requestId: "",
      tenantId: "pg.citya",
      item: data?.procurementReq?.parentCategory?.code,
      itemType: data?.procurementReq?.subCategory?.code,
      quantity: convertStringToFloat(data?.procurementReq.quantity),
      assetApplicationNumber: data?.procurementReq.assetApplicationNumber?.code,
      status:"PENDING"
    }
  };
  return payLoadProcerement;
};
export const InventoryCreationData = (data) => {
  
  const payLoadInventoryCreation = {
    Asset: {
      tenantId: data?.inventory?.office,
      assetInventory:{
          procurementRequestId: data?.inventory?.requestId?.code,
          office: data?.inventory?.office,
          item: data?.inventory.item,
          itemType: data?.inventory.itemType,
          itemDescription: data?.inventory.itemDescription,
          attributes: data?.inventory.attributes,
          vendorNumber: data?.inventory?.vendorDetails?.code,
          purchaseMode: data?.inventory?.purchaseMode?.code,
          purchaseDate: convertDateToEpoch(data?.inventory.purchaseDate),
          deliveryDate: convertDateToEpoch(data?.inventory.deliveryDate),
          endOfLife: convertDateToEpoch(data?.inventory.endOfLife),
          endOfSupport: data?.inventory.endOfSupport,
          quantity: data?.inventory.quantity,
          unitPrice: convertStringToFloat(data?.inventory.unitPrice),
          totalPrice: convertStringToFloat(data?.inventory.totalPrice),
          uain: data?.inventory.assetApplicationNumber,
          insuranceApplicability: data?.insuranceApplicability?.code,
          inventoryStatus:"ACTIVE"
      }
    }
  };
  return payLoadInventoryCreation;
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
    businessService: `asset-create`,
    moduleName: "asset-services",
  };
};
