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

export const setProductDetails = (data) => {
  let { ewdet } = data;

  let productDetails = ewdet?.prlistName.map((product, index) => {
    return { 
      productId: "",
      productName: product.code,
      quantity: ewdet?.prlistQuantity[index].code,
      price: product.price * ewdet?.prlistQuantity[index].code
    };
  }) || [];

  data.ewdet = productDetails;
  return data;
}

// export const setOwnerDetails = (data) => {
//   let { ownerKey } = data;

//   let ownerDetails = {
//     ...ownerKey, 
//     applicantName: ownerKey?.applicantName,
//     mobileNumber: ownerKey?.mobileNumber,
//     emailId: ownerKey?.emailId
//   }

//   data.ownerKey = ownerDetails;
//   return data;
// }


export const EWDataConvert = (data) => {
 
  data = setProductDetails(data);
  // data = setOwnerDetails(data);
  data = setAddressDetails(data);

  console.log("this is data in ::", data)

  // const formdata = {
  //   EwasteApplication: [{
  //     tenantId: "pg.citya",
  //     applicant: {...data?.ownerKey},
  //     address: data.address,
  //       ...data?.documents,

      
  //     workflow : {
  //       businessService: "ewst",
  //       action : "CREATE",
  //       moduleName: "ewaste-services"
  //     }
  //   }],
  // };

  const formdata = {
    EwasteApplication: [
    {
      tenantId: "pg.citya",
      requestId: data.requestId || "",
      transactionId: data.transactionId || "",
      pickUpDate: data.pickUpDate || "",
      vendorUuid: "345",
      requestStatus: "New Request",
      applicant: {
        applicantName: data?.ownerKey?.applicantName,
        mobileNumber: data?.ownerKey?.mobileNumber,
        emailId: data?.ownerKey?.emailId,
        altMobileNumber: data?.ownerKey?.altMobileNumber,
      },
      ewasteDetails: data?.ewdet,
      address: {
        tenantId: "pg.citya",
        doorNo: data.address?.doorNo,
        calculatedAmount: data.calculatedAmount,
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
          name: data.address?.locality?.name || ""
        }
      },
      documents: data.documents || [],
      workflow: {
        businessService: "ewst",
        action: "CREATE",
        moduleName: "ewaste-services"
      }
    }
  ]
  };
 
  return formdata;
};

export const CompareTwoObjects = (ob1, ob2) => {
  let comp = 0;
Object.keys(ob1).map((key) =>{
  if(typeof ob1[key] == "object")
  {
    if(key == "institution")
    {
      if((ob1[key].name || ob2[key].name) && ob1[key]?.name !== ob2[key]?.name)
      comp=1
      else if(ob1[key]?.type?.code !== ob2[key]?.type?.code)
      comp=1
      
    }
    else if(ob1[key]?.code !== ob2[key]?.code)
    comp=1
  }
  else
  {
    if((ob1[key] || ob2[key]) && ob1[key] !== ob2[key])
    comp=1
  }
});
if(comp==1)
return false
else
return true;
}

/*   method to check value  if not returns NA*/
export const checkForNA = (value = "") => {
  return checkForNotNull(value) ? value : "EWASTE_NA";
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
export const convertEpochToDate = (dateEpoch,businessService) => {
  // Returning null in else case because new Date(null) returns initial date from calender
  if (dateEpoch) {
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    if(businessService == "ewst")
    return `${day}-${month}-${year}`;
    else
    return `${day}/${month}/${year}`;
  } else {
    return null;
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

    businessService: `ewst`,
    moduleName: "ewaste-services",
  };
};

