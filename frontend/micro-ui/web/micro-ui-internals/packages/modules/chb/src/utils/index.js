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

export const sethallDetails = (data) => {
  let { slotlist } = data;
  let hallDetails = slotlist?.bookingSlotDetails.map((slot) => {
    return { 
      bookingId:slot.slotId,
      hallCode: slot.slotId,
      bookingDate:"20-06-2024",
      bookingFromTime:"10:00",
      bookingToTime:"9:59",
      hallName: slot.name,
      status:"Available",
    };
  }) || [];

  data.slotlist = hallDetails;
  return data;
}

export const setBankDetails = (data) => {
  let { bankdetails } = data;

  let propbankdetails = {
    ...bankdetails,
    accountNumber: bankdetails?.accountNumber,
    confirmAccountNumber: bankdetails?.confirmAccountNumber,
    ifscCode: bankdetails?.ifscCode,
    bankName: bankdetails?.bankName,
    bankBranchName: bankdetails?.bankBranchName,
    accountHolderName: bankdetails?.accountHolderName,
    refundStatus: "Done",
  };

  data.bankdetails = propbankdetails;
  return data;

};

export const setOwnerDetails = (data) => {
    let { ownerss } = data;
  
    let propOwners = {
      ...ownerss,
      applicantName:ownerss?.applicantName,
      applicantMobileNo:ownerss?.mobileNumber,
      applicantAlternateMobileNo:ownerss?.alternateNumber,
      applicantEmailId:ownerss?.emailId,
    };
  
    data.ownerss = propOwners;
    return data;
  };
  
  export const setSlotDetails = (data) => {
    let { slots } = data;
  
    let bookingSlotDetails = {
      ...slots,
      type:slots?.residentType?.value,
      category:slots?.specialCategory?.value,
      purposes:slots?.purpose?.value,
      purposeDescription:slots?.purposeDescription,
      
    };
  
    data.slots = bookingSlotDetails;
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


export const CHBDataConvert = (data) => {
 
  data = setDocumentDetails(data);
  data = setOwnerDetails(data);
  data = setBankDetails(data);
  data = setSlotDetails(data);
  data=sethallDetails(data);

const formdata={
  hallsBookingApplication: {
    tenantId: data.tenantId,
    applicantName:data.ownerss?.applicantName,
    applicantMobileNo:data.ownerss?.mobileNumber,
    applicantAlternateMobileNo:data.ownerss?.alternateNumber,
    applicantEmailId:data.ownerss?.emailId,
    bankDetails:data.bankdetails,
    purposeDescription:data.slots?.purposeDescription,
    specialCategory:{
      category:data.slots?.specialCategory?.value
    },
    residentType:{
      type:data.slots?.residentType?.value
    },
    purpose:{
      purpose:data.slots?.purpose?.value
    },
    bookingSlotDetails:data?.slotlist,
    // uploadedDocumentDetails:data.documents || [],
    uploadedDocumentDetails:[],

  workflow : {
      businessService: "chb",
      action : "CREATE",
      moduleName: "chb-services"
    }
  }
}

  // const formdata = {
  //     hallsBookingApplication: {
  //       tenantId: data.tenantId,
  //       // ...data?.ownerss,
  //       bankDetails: data.bankdetails,
  //         // ...data.documents,
  //       bookingSlotDetails: [
  //           {
  //               bookingId: "675464564",
  //               hallCode: "456",
  //               slotId: "464564",
  //               status: "Booked"
  //           }
  //       ],
  //       purpose:{
  //         purpose:"kjskldfjsd"
  //       },
  //       purposeDescription: "sjfdsljfdskf",
  //       residentType: {
  //           type:"kjflsdkfj"
  //       },
  //       specialCategory: {
  //           category: "lksjdklf"
  //       },
  //       uploadedDocumentDetails: [],

  //         workflow : {
  //           businessService: "chb",
  //           action : "CREATE",
  //           moduleName: "chb-services"
  //         }
  //     },
  // }

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
  return checkForNotNull(value) ? value : "CHB_NA";
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
    if(businessService == "chb")
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

    businessService: `chb`,
    moduleName: "chb-services",
  };
};

