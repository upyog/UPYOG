import React from "react";

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
            applicantDetailId: user?.info?.uuid,
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
                houseNumber: data?.address?.houseNo,
                addressLine1: data?.address?.addressLine1,
                addressLine2: data?.address?.addressLine2,
                landmark: data?.address?.landmark,
                floorNumber: null,
                locality: data?.address?.locality?.i18nKey,
                city: data?.address?.city?.city?.name,
                pinCode: data?.address?.pincode
            }
        }
    };
    return formData;

};