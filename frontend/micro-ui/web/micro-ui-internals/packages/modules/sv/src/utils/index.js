/**
 * @author - Shivank - NIUA
 * This componet is developed for all the utility functions which  are used in the whole 
 * application.
 * I have already added the comments for each function.
 */



export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
    if (searcher == "") return str;
    while (str.includes(searcher)) {
      str = str.replace(searcher, replaceWith);
    }
    return str;
};
export const checkForNotNull = (value = "") => {
  return value && value != null && value != undefined && value != "" ? true : false;
};

export const checkForNA = (value = "") => {
  return checkForNotNull(value) ? value : "NA";
};


/**
 * This function processes an array of uploaded documents to create a list of unique document types,
 * grouping documents that share the same type. It returns an array of objects, each containing the 
 * title of the document type and the corresponding documents.
 *
 * @param {Array} appUploadedDocumnets - An array of uploaded document objects, each expected to have a 
 *                                       'documentType' property.
 * @returns {Array} finalDocs - An array of objects, each with a 'title' and 'values' property. 
 *                              'title' represents the unique document type, and 'values' is an 
 *                              array of documents of that type.
 *
 * The function first filters out duplicate document types based on the first two segments of the 
 * 'documentType' string (split by a dot). Then, for each unique document type, it gathers all 
 * associated documents, sets their titles to their document type, and pushes an object containing 
 * the title and values into the finalDocs array.
 */
export const getOrderDocuments = (appUploadedDocumnets, isNoc = false) => {
  let finalDocs = [];
  if (appUploadedDocumnets?.length > 0) {
    let uniqueDocmnts = appUploadedDocumnets.filter((elem, index) => appUploadedDocumnets.findIndex((obj) => obj?.documentType?.split(".")?.slice(0, 2)?.join("_") === elem?.documentType?.split(".")?.slice(0, 2)?.join("_")) === index);
    uniqueDocmnts?.map(uniDoc => {
      const resultsDocs = appUploadedDocumnets?.filter(appDoc => uniDoc?.documentType?.split(".")?.slice(0, 2)?.join("_") == appDoc?.documentType?.split(".")?.slice(0, 2)?.join("_"));
      resultsDocs?.forEach(resDoc => resDoc.title = resDoc.documentType);
      finalDocs.push({
        title: resultsDocs?.[0]?.documentType?.split(".")?.slice(0, 2)?.join("_"),
        values: resultsDocs
      })
    });
  }
  return finalDocs;
}



 /**
  *  Utility function to calculate that accurately calculates age from a birth date
   * Calculates age from a given birth date
   * 
   * @param {string} birthDate - Date of birth in YYYY-MM-DD format
   * @returns {number} - Age in years
   */
export const calculateAge = (birthDate) => {
  const today = new Date();
  const birth = new Date(birthDate);
  let age = today.getFullYear() - birth.getFullYear();
  const monthDiff = today.getMonth() - birth.getMonth();
  // Adjust age if birthday hasn't occurred this year
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--;
  }
  return age;
};


// Utility function to transform documents array into required format
export const transformDocuments = (documents) => {

  if (!Array.isArray(documents)) return [];

  // Retrieve and parse CategoryDocument from sessionStorage
  const categoryDocument = sessionStorage.getItem("CategoryDocument");
  const parsedCategoryDocument = categoryDocument ? JSON.parse(categoryDocument) : null;
  // Transform existing documents
  const transformedDocs = documents.map(doc => ({
    applicationId: "",  // Populate as required
    documentType: doc.documentType,
    fileStoreId: window.location.href.includes("edit")?doc?.fileStoreId?.fileStoreId:doc.fileStoreId,
    documentDetailId: window.location.href.includes("edit")?doc?.fileStoreId?.fileStoreId:doc.documentUid, // Use documentUid as documentDetailId
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    }
  }));

  // Add parsedCategoryDocument as an additional document object, if it exists
  if (parsedCategoryDocument) {
    transformedDocs.push({
      applicationId: "",  // Populate if needed
      documentType: parsedCategoryDocument?.[0]?.documentType || "", // Provide appropriate type if applicable
      fileStoreId: parsedCategoryDocument?.[0]?.fileStoreId || "",   // Provide fileStoreId if applicable
      documentDetailId: parsedCategoryDocument?.[0]?.documentUid || "", // Provide detailId if applicable
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      }
    });
  }

  return transformedDocs;
};
/**
 * This function `svPayloadData` processes the input `data` to create a structured payload 
 * for street vending details. It constructs vendor, spouse, and dependent objects based on 
 * the provided data, while also handling the address and bank details. 

 * The function performs the following steps:
 * 1. Defines helper functions to create vendor, spouse, and dependent objects with 
 *    necessary properties such as name, date of birth, email, and relationship type.
 * 2. Checks if the vendor name exists and determines the presence of spouse and dependent 
 *    names to construct an array of vendor details accordingly:
 *    - If only the vendor exists, it creates a single vendor object.
 *    - If both vendor and spouse exist, it creates both objects.
 *    - If all three (vendor, spouse, and dependent) exist, it creates all three objects.
 * 3. Collects operational time details for vending days, filtering and mapping selected days 
 *    to a structured format.
 * 4. Constructs a final `formdata` object that encapsulates all the details including 
 *    address information, bank details, vendor details, and operational time details.
 * 5. Returns the `formdata` object for submission.

 * Note: The function relies on the input `data` being structured correctly and assumes certain 
 * properties exist. It also uses sessionStorage to retrieve specific statuses related to 
 * disability and beneficiaries.
 */


export const svPayloadData = (data) =>{
  let vendordetails = [];

  const createVendorObject = (data) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: data?.owner?.units?.[0]?.vendorDateOfBirth,
    userCategory:data?.owner?.units?.[0]?.userCategory?.code,
    emailId: data?.owner?.units?.[0]?.email,
    fatherName: data?.owner?.units?.[0]?.fatherName,
    specialCategory: data?.specialCategoryData?.ownerCategory?.code,
    gender: data?.owner?.units?.[0]?.gender?.code.charAt(0),
    id: "",
    isInvolved: true,
    mobileNo: data?.owner?.units?.[0]?.mobileNumber,
    name: data?.owner?.units?.[0]?.vendorName,
    relationshipType: "VENDOR",
    vendorId: null
  });

  const createSpouseObject = (data) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: data?.owner?.units?.[0]?.spouseDateBirth,
    userCategory:data?.owner?.units?.[0]?.userCategory?.code,
    emailId: "",
    specialCategory: data?.specialCategoryData?.ownerCategory?.code,
    isInvolved: data?.owner?.spouseDependentChecked,
    fatherName: "",
    gender: "O",
    id: "",
    mobileNo: "",
    name: data?.owner?.units?.[0]?.spouseName,
    relationshipType: "SPOUSE",
    vendorId: null
  });

  const createDependentObject = (data) => ({
    applicationId: "",
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: data?.owner?.units?.[0]?.dependentDateBirth,
    userCategory:data?.owner?.units?.[0]?.userCategory?.code,
    emailId: "",
    isInvolved: data?.owner?.dependentNameChecked,
    specialCategory: data?.specialCategoryData?.ownerCategory?.code,
    fatherName: "",
    gender: data?.owner?.units?.[0]?.dependentGender?.code.charAt(0),
    id: "",
    mobileNo: "",
    name: data?.owner?.units?.[0]?.dependentName,
    relationshipType: "DEPENDENT",
    vendorId: null
  });

  // Helper function to check if a string is empty or undefined
  const isEmpty = (str) => !str || str.trim() === '';

  // Main logic
  if (!isEmpty(data?.owner?.units?.[0]?.vendorName)) {
    const spouseName = data?.owner?.units?.[0]?.spouseName;
    const dependentName = data?.owner?.units?.[0]?.dependentName;

    if (isEmpty(spouseName) && isEmpty(dependentName)) {
      // Case 1: Only vendor exists
      vendordetails = [createVendorObject(data)];
    } else if (!isEmpty(spouseName) && isEmpty(dependentName)) {
      // Case 2: Both vendor and spouse exist
      vendordetails = [
        createVendorObject(data),
        createSpouseObject(data)
      ];
    } else if (!isEmpty(spouseName) && !isEmpty(dependentName)) {
      // Case 3: All three exist (vendor, spouse, and dependent)
      vendordetails = [
        createVendorObject(data),
        createSpouseObject(data),
        createDependentObject(data)
      ];
    }
  }

  const daysOfOperation = data?.businessDetails?.daysOfOperation;
  const vendingOperationTimeDetails = daysOfOperation
  .filter(day => day.isSelected) // Filter only selected days
  .map(day => ({
    applicationId: "", // Add actual applicationId if available
    auditDetails: {
      createdBy: "", // Adjust these fields based on your data
      createdTime: 0, 
      lastModifiedBy: "",
      lastModifiedTime: 0,
    },
    dayOfWeek: day.name.toUpperCase(),
    fromTime: day.startTime,
    toTime: day.endTime,
    id: ""
  }));

  const draftid=sessionStorage.getItem("Response");
  const draftID= draftid ? JSON.parse(draftid) : null

  const formdata={
    streetVendingDetail: {
    addressDetails: [
      {
        addressId: "",
        addressLine1: data?.address?.addressline1,
        addressLine2: data?.address?.addressline2,
        addressType: "",
        city: data?.address?.city?.name||data?.address?.city?.code,
        cityCode: data?.address?.city?.code||data?.tenantId,
        doorNo: "",
        houseNo: data?.address?.houseNo,
        landmark: data?.address?.landmark,
        locality: draftID?.SVDetail?.[0]?.addressDetails?.[0]?.locality||data?.address?.locality?.i18nKey,
        localityCode: draftID?.SVDetail?.[0]?.addressDetails?.[0]?.localityCode||data?.address?.locality?.code,
        pincode: data?.address?.pincode,
        streetName: "",
        vendorId: ""
      },
      { // sending correspondence address here
        addressId: "",
        addressLine1: data?.correspondenceAddress?.caddressline1,
        addressLine2: data?.correspondenceAddress?.caddressline2,
        addressType: "",
        city: data?.correspondenceAddress?.ccity?.name||data?.correspondenceAddress?.ccity?.code,
        cityCode:  data?.correspondenceAddress?.ccity?.code||data?.tenantId,
        doorNo: "",
        houseNo: data?.correspondenceAddress?.chouseNo,
        landmark: data?.correspondenceAddress?.clandmark,
        locality: draftID?.SVDetail?.[0]?.addressDetails?.[1]?.locality||data?.correspondenceAddress?.clocality?.i18nKey,
        localityCode: draftID?.SVDetail?.[0]?.addressDetails?.[1]?.localityCode||data?.correspondenceAddress?.clocality?.code,
        pincode: data?.correspondenceAddress?.cpincode,
        streetName: "",
        vendorId: "",
        isAddressSame: data?.correspondenceAddress?.isAddressSame
      }
    ],
    applicationDate: 0,
    applicationId: "",
    applicationNo: "",
    applicationStatus: "",
    approvalDate: 0,
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    bankDetail: {
      accountHolderName: data?.bankdetails?.accountHolderName,
      accountNumber: data?.bankdetails?.accountNumber,
      applicationId: "",
      bankBranchName: data?.bankdetails?.bankBranchName,
      bankName: data?.bankdetails?.bankName,
      id: "",
      ifscCode: data?.bankdetails?.ifscCode,
      refundStatus: "",
      refundType: "",
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
    },
    benificiaryOfSocialSchemes: data?.specialCategoryData?.beneficiary?.value,
    enrollmentId:data?.specialCategoryData?.enrollmentId,
    cartLatitude: 0,
    cartLongitude: 0,
    certificateNo: null,
    disabilityStatus: data?.specialCategoryData?.ownerCategory?.code,
    draftId:draftID?.SVDetail?.draftId||"",
    documentDetails: transformDocuments(data?.documents?.documents),
    localAuthorityName: data?.businessDetails?.nameOfAuthority,
    tenantId: data?.tenantId,
    termsAndCondition: "Y",
    tradeLicenseNo: data?.owner?.units?.[0]?.tradeNumber,
    vendingActivity: data?.businessDetails?.vendingType?.code,
    vendingArea: data?.businessDetails?.areaRequired||"0",
    vendingLicenseCertificateId: "",
    vendingOperationTimeDetails,
    vendingZone:  data?.businessDetails?.vendingZones?.code,
    vendorDetail: [
      ...vendordetails
    ],
    workflow: {
      action: "APPLY",
      comments: "",
      businessService: "street-vending",
      moduleName: "sv-services",
      businessService: "street-vending",
      moduleName: "sv-services",
      varificationDocuments: [
        {
          additionalDetails: {},
          auditDetails: {
            createdBy: "",
            createdTime: 0,
            lastModifiedBy: "",
            lastModifiedTime: 0
          },
          documentType: "",
          documentUid: "",
          fileStoreId: "",
          id: ""
        }
      ]
    }
  },
  draftApplication:false,
  };
  return formdata;
}


export const svUpdatePayload = (data) =>{
  let vendordetails = [];

  const createVendorObject = (data) => ({
    applicationId: sessionStorage.getItem("ApplicationId"),
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: data?.owner?.units?.[0]?.vendorDateOfBirth,
    userCategory:data?.owner?.units?.[0]?.userCategory?.code,
    emailId: data?.owner?.units?.[0]?.email,
    fatherName: data?.owner?.units?.[0]?.fatherName,
    specialCategory: data?.specialCategoryData?.ownerCategory?.code,
    gender: data?.owner?.units?.[0]?.gender?.code.charAt(0),
    id: sessionStorage.getItem("venId"),
    mobileNo: data?.owner?.units?.[0]?.mobileNumber,
    name: data?.owner?.units?.[0]?.vendorName,
    relationshipType: "VENDOR",
    vendorId: null
  });

  const createSpouseObject = (data) => ({
    applicationId: sessionStorage.getItem("ApplicationId"),
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: data?.owner?.units?.[0]?.spouseDateBirth,
    userCategory:data?.owner?.units?.[0]?.userCategory?.code,
    emailId: "",
    isInvolved: data?.owner?.spouseDependentChecked,
    fatherName: "",
    specialCategory: data?.specialCategoryData?.ownerCategory?.code,
    gender: "N/A",
    id: sessionStorage.getItem("venId"),
    mobileNo: "",
    name: data?.owner?.units?.[0]?.spouseName,
    relationshipType: "SPOUSE",
    vendorId: null
  });

  const createDependentObject = (data) => ({
    applicationId: sessionStorage.getItem("ApplicationId"),
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    dob: data?.owner?.units?.[0]?.dependentDateBirth,
    userCategory:data?.owner?.units?.[0]?.userCategory?.code,
    emailId: "",
    isInvolved: data?.owner?.dependentNameChecked,
    fatherName: "",
    specialCategory: data?.specialCategoryData?.ownerCategory?.code,
    gender: data?.owner?.units?.[0]?.dependentGender?.code.charAt(0),
    id: sessionStorage.getItem("venId"),
    mobileNo: "",
    name: data?.owner?.units?.[0]?.dependentName,
    relationshipType: "DEPENDENT",
    vendorId: null
  });

  // Helper function to check if a string is empty or undefined
  const isEmpty = (str) => !str || str.trim() === '';

  // Main logic
  if (!isEmpty(data?.owner?.units?.[0]?.vendorName)) {
    const spouseName = data?.owner?.units?.[0]?.spouseName;
    const dependentName = data?.owner?.units?.[0]?.dependentName;

    if (isEmpty(spouseName) && isEmpty(dependentName)) {
      // Case 1: Only vendor exists
      vendordetails = [createVendorObject(data)];
    } else if (!isEmpty(spouseName) && isEmpty(dependentName)) {
      // Case 2: Both vendor and spouse exist
      vendordetails = [
        createVendorObject(data),
        createSpouseObject(data)
      ];
    } else if (!isEmpty(spouseName) && !isEmpty(dependentName)) {
      // Case 3: All three exist (vendor, spouse, and dependent)
      vendordetails = [
        createVendorObject(data),
        createSpouseObject(data),
        createDependentObject(data)
      ];
    }
  }

  const daysOfOperation = data?.businessDetails?.daysOfOperation;
  const vendingOperationTimeDetails = daysOfOperation
  .filter(day => day.isSelected) // Filter only selected days
  .map(day => ({
    applicationId: sessionStorage.getItem("ApplicationId"),
    auditDetails: {
      createdBy: "", // Adjust these fields based on your data
      createdTime: 0, 
      lastModifiedBy: "",
      lastModifiedTime: 0,
    },
    dayOfWeek: day.name.toUpperCase(),
    fromTime: day.startTime,
    toTime: day.endTime,
    id: ""
  }));

  const formdata={
    streetVendingDetail: {
    addressDetails: [
      {
        addressId: sessionStorage.getItem("addressIdOne"),
        addressLine1: data?.address?.addressline1,
        addressLine2: data?.address?.addressline2,
        addressType: "PERMANENT",
        city: data?.address?.city?.name||data?.address?.city?.code,
        cityCode: data?.address?.city?.code,
        doorNo: "",
        houseNo: data?.address?.houseNo,
        landmark: data?.address?.landmark,
        locality: data?.address?.locality?.i18nKey,
        localityCode: data?.address?.locality?.code,
        pincode: data?.address?.pincode,
        streetName: "",
        vendorId: sessionStorage.getItem("vendorIds")
      },
      { // sending correspondence address here
        addressId: sessionStorage.getItem("addressIdTwo"),
        addressLine1: data?.correspondenceAddress?.caddressline1,
        addressLine2: data?.correspondenceAddress?.caddressline2,
        addressType: "CORRESPONDENCE",
        city: data?.correspondenceAddress?.ccity?.name||data?.correspondenceAddress?.ccity?.code,
        cityCode: data?.correspondenceAddress?.ccity?.code,
        doorNo: "",
        houseNo: data?.correspondenceAddress?.chouseNo,
        landmark: data?.correspondenceAddress?.clandmark,
        locality: data?.correspondenceAddress?.clocality?.i18nKey,
        localityCode: data?.correspondenceAddress?.clocality?.code,
        pincode: data?.correspondenceAddress?.cpincode,
        streetName: "",
        vendorId: sessionStorage.getItem("vendorIds"),
        isAddressSame: data?.correspondenceAddress?.isAddressSame
      }
    ],
    applicationDate: 0,
    applicationId: sessionStorage.getItem("ApplicationId"),
    applicationNo: sessionStorage.getItem("vendingApplicationID"),
    applicationStatus: "",
    approvalDate: 0,
    auditDetails: {
      createdBy: "",
      createdTime: 0,
      lastModifiedBy: "",
      lastModifiedTime: 0
    },
    bankDetail: {
      accountHolderName: data?.bankdetails?.accountHolderName,
      accountNumber: data?.bankdetails?.accountNumber,
      applicationId: sessionStorage.getItem("ApplicationId"),
      bankBranchName: data?.bankdetails?.bankBranchName,
      bankName: data?.bankdetails?.bankName,
      id: sessionStorage.getItem("bankIds"),
      ifscCode: data?.bankdetails?.ifscCode,
      refundStatus: "",
      refundType: "",
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
    },
    benificiaryOfSocialSchemes: data?.specialCategoryData?.beneficiary?.value,
    enrollmentId:data?.specialCategoryData?.enrollmentId,
    cartLatitude: 0,
    cartLongitude: 0,
    certificateNo: null,
    disabilityStatus: data?.specialCategoryData?.ownerCategory?.code,
    documentDetails: transformDocuments(data?.documents?.documents),
    localAuthorityName: data?.businessDetails?.nameOfAuthority,
    tenantId: data?.tenantId,
    termsAndCondition: "Y",
    tradeLicenseNo: data?.owner?.units?.[0]?.tradeNumber,
    vendingActivity: data?.businessDetails?.vendingType?.code,
    vendingArea: data?.businessDetails?.areaRequired||"0",
    vendingLicenseCertificateId: "",
    vendingOperationTimeDetails,
    vendingZone:  data?.businessDetails?.vendingZones?.code,
    vendorDetail: [
      ...vendordetails
    ],
    workflow: {
      action: "RESUBMIT",
      comments: "",
      businessService: "street-vending",
      moduleName: "sv-services",
      businessService: "street-vending",
      moduleName: "sv-services",
      varificationDocuments: [
        {
          additionalDetails: {},
          auditDetails: {
            createdBy: "",
            createdTime: 0,
            lastModifiedBy: "",
            lastModifiedTime: 0
          },
          documentType: "",
          documentUid: "",
          fileStoreId: "",
          id: ""
        }
      ]
    }
  }
  };
  return formdata;
}

import { useTranslation } from "react-i18next";
import React from "react";

export function SVDocumnetPreview({documents, titleStyles, isSendBackFlow = false, isHrLine = false}) {
  const { t } = useTranslation();
  const SvPDFSvg = () => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect width="24" height="24" rx="4" fill="#D32F2F"/>
      <text x="0" y="16" font-family="Arial, sans-serif" font-size="12" font-weight="bold" fill="#FFFFFF">PDF</text>
    </svg>
  );
  
  return (
    <div style={{ marginTop: "19px"}}>
      {documents?.map((document, index) => (
        <React.Fragment key={index}>
          <div className="documentWidth" style={{width:"50%"}}>
            <div>
              {document?.values && document?.values.length > 0 ? document?.values?.map((value, index) => (
                <a target="_" href={value?.url} style={{ minWidth: "80px", marginRight: "10px", maxWidth: "100px", height: "auto", minWidth: "100px" }} key={index}>
                  {/* Remove the centered SVG div from here */}
                  <div style={{ 
                    display: "flex", 
                    alignItems: "center", 
                    gap: "8px",
                    marginTop: "8px"
                  }}>
                    <p style={{ 
                      margin: 0,
                      fontWeight: "bold", 
                      color: "#0000FF", 
                      textDecoration: "underline"
                    }}>
                      {t(value?.title)}
                    </p>
                    <SvPDFSvg /> {/* SVG now appears on the right */}
                  </div>
                  {isSendBackFlow ? (
                    value?.documentType?.includes("NOC") ? 
                      <p style={{ textAlign: "center" }}>{t(value?.documentType.split(".")[1])}</p> : 
                      <p style={{ textAlign: "center" }}>{t(value?.documentType)}</p>
                  ) : ""}
                </a>
              )) : !(window.location.href.includes("citizen")) && <div><p>{t("SV_NO_DOCUMENTS_UPLOADED_LABEL")}</p></div>}
            </div>
            {isHrLine && documents?.length != index + 1 ? (
              <hr style={{ 
                color: "#D6D5D4", 
                backgroundColor: "#D6D5D4", 
                height: "2px", 
                marginTop: "20px", 
                marginBottom: "20px" 
              }} />
            ) : null}
          </div>
        </React.Fragment>
      ))}
    </div>
  );
}