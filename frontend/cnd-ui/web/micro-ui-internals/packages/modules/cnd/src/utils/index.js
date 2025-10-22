import React from "react";
import { useTranslation } from "react-i18next";

/**
 * @author - Shivank Shukla - NUDM
 * 
 * This is the Util Page where i developed the common functions as well as small components 
 * which will be used across the Construction & Demolition Module.
 * 
*/


// CND Common Variables used accross application where same and repetitive names are used
export const CND_VARIABLES = {
    MODULE:"MODULE_CND",
    NEXT:"COMMON_NEXT",
    MODULE_NAME:"CND",
    SITE_MEDIA_PHOTO:"siteMediaPhoto",
    SITE_STACK_PHOTO:"siteStack",
    MDMS_MASTER:"cnd-service",
    HOME_PATH: "/digit-ui/citizen"
};

export const convertToObject = (String) => String ? { i18nKey: String, code: String, value: String } : null;
export  const LoadingSpinner = () => (
    <div className="loading-spinner"/>
);

export const checkForNotNull = (value = "") => {
    return value && value != null && value != undefined && value != "" ? true : false;
};
  
export const checkForNA = (value = "") => {
return checkForNotNull(value) ? value : "NA";
};


// Create Payload for the CND Application
export const cndPayload = (data) => {
    const user = Digit.UserService.getUser();
    const formData =  
    {
        cndApplication: {
            tenantId: data?.tenantId,
            additionalDetails: null,
            applicationType: "REQUEST_FOR_PICKUP",
            applicationStatus: "BOOKING_CREATED",
            depositCentreDetails: "",
            description: "",
            vehicleId: "",
            vehicleType: "",
            vendorId: "",
            location: "",
            completedOn:"",
            constructionFromDate:data?.propertyNature?.constructionFrom,
            constructionToDate:data?.propertyNature?.constructionTo,
            propertyType: data?.propertyNature?.propertyUsage?.code,
            houseArea: data?.propertyNature?.houseArea,
            applicantDetailId : (user?.info?.mobileNumber === data?.owner?.mobileNumber) ? user?.info?.uuid : null,
            addressDetailId: (user?.info?.mobileNumber === data?.owner?.mobileNumber) && data?.addressDetails ? data?.addressDetails?.selectedAddressStatement?.id : null,
            totalWasteQuantity: data?.wasteType?.wasteQuantity,
            typeOfConstruction: data?.propertyNature?.constructionType?.code,
            noOfTrips: 0,
            pickupDate:"",
            requestedPickupDate:data?.wasteType?.pickupDate,
            facilityCenterDetail: {
                disposalId: "",
                netWeight: data?.wasteType?.wasteQuantity
            },
            wasteTypeDetails: data?.wasteType?.wasteMaterialType?.map(item => ({
                applicationId: "",
                wasteTypeId: "",
                enteredByUserType: user?.info?.type,
                wasteType: item.code, // Using the code value from wasteMaterialType
                quantity: 0,
                metrics: "",
                auditDetails: null
            })) || [],
            documentDetails: [
                ...(data?.wasteType?.siteMediaPhoto ? [{
                    documentDetailId: "",
                    applicationId: "",
                    documentType: CND_VARIABLES.SITE_MEDIA_PHOTO,
                    uploadedByUserType: user?.info?.type,
                    fileStoreId: data.wasteType.siteMediaPhoto,
                    auditDetails: null
                }] : []),
                ...(data?.wasteType?.siteStack ? [{
                    documentDetailId: "",
                    applicationId: "",
                    documentType: CND_VARIABLES.SITE_STACK_PHOTO,
                    uploadedByUserType: user?.info?.type,
                    fileStoreId: data.wasteType.siteStack,
                    auditDetails: null
                }] : [])
            ],
            workflow: {
            action: "APPLY",
            comments: "",
            businessService: "cnd",
            moduleName: "cnd-service"
            },
            applicantDetail: {
                nameOfApplicant: data?.owner?.applicantName,
                mobileNumber: data?.owner?.mobileNumber,
                alternateMobileNumber: data?.owner?.alternateNumber,
                emailId: data?.owner?.emailId
            },
            addressDetail: {
                houseNumber: data?.addressDetails?.selectedAddressStatement?.houseNumber||data?.address?.houseNo,
                addressLine1: data?.addressDetails?.selectedAddressStatement?.address||data?.address?.addressLine1,
                addressLine2: data?.addressDetails?.selectedAddressStatement?.address2||data?.address?.addressLine2,
                landmark: data?.addressDetails?.selectedAddressStatement?.landmark||data?.address?.landmark,
                floorNumber: null,
                locality: data?.addressDetails?.selectedAddressStatement?.locality||data?.address?.locality?.i18nKey,
                city: data?.addressDetails?.selectedAddressStatement?.city||data?.address?.city?.city?.name,
                pinCode: data?.addressDetails?.selectedAddressStatement?.pinCode||data?.address?.pincode,
                addressType: data?.addressDetails?.selectedAddressStatement?.type||data?.address?.addressType?.code
            }
        }
    };
    return formData;

};




    // Unit conversion constants
    const CONVERSION_FACTORS = {
        "Kilogram": 0.001, // 1 Kilogram = 0.001 Ton
        "Ton": 1,          // 1 Ton = 1 Ton (Standard unit)
        "Metric Ton": 1.1023 // 1 Metric Ton = 1.1023 Ton (short tons)
    };
  
  /**
   * Converts quantity from one unit to another
   * @param {number} quantity - The quantity to convert
   * @param {string} fromUnit - The source unit (Kilogram, Ton, Metric Ton)
   * @param {string} toUnit - The target unit (Kilogram, Ton, Metric Ton)
   * @returns {number} - The converted quantity
   */
  export const convertWasteQuantity = (quantity, fromUnit, toUnit) => {
    if (!quantity || isNaN(Number(quantity))) return 0;
    
    const numericQuantity = Number(quantity);
    
    // Convert to standard unit (Ton)
    const inTons = numericQuantity * (CONVERSION_FACTORS[fromUnit] || 0);
    
    // Convert from standard unit to target unit
    return inTons / (CONVERSION_FACTORS[toUnit] || 1);
  };
  
  /**
   * Calculates the total waste quantity in tons from waste details
   * @param {Object} wasteDetails - Object containing waste details {wasteTypeCode: {quantity, unit}}
   * @returns {number} - The total waste quantity in tons
   */
  export const calculateTotalWasteInTons = (wasteDetails) => {
    if (!wasteDetails || typeof wasteDetails !== 'object') return 0;
    
    return Object.values(wasteDetails).reduce((total, detail) => {
      if (!detail.quantity || isNaN(Number(detail.quantity))) return total;
      
      const quantityInTons = convertWasteQuantity(
        detail.quantity, 
        detail.unit || "Kilogram", 
        "Ton"
      );
      
      return total + quantityInTons;
    }, 0);
  };
  
  /**
   * Formats the waste quantity with appropriate unit
   * @param {number} quantity - The quantity to format
   * @returns {string} - Formatted quantity with unit
   */
  export const formatWasteQuantity = (quantity) => {
    if (!quantity || isNaN(Number(quantity))) return "0 Tons";
    
    const numericQuantity = Number(quantity);
    return `${numericQuantity.toFixed(2)} Tons`;
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


export function CNDDocumnetPreview({documents, titleStyles, isSendBackFlow = false, isHrLine = false}) {
  const { t } = useTranslation();
  const CndPDFSvg = () => (
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
              {document?.title!=="NONE" && document?.values && document?.values.length > 0 ? document?.values?.map((value, index) => (
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
                    <CndPDFSvg /> {/* SVG now appears on the right */}
                  </div>
                </a>
              )) : !(window.location.href.includes("citizen")) && <div><p>{t("CND_NO_DOCUMENTS_UPLOADED_LABEL")}</p></div>}
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