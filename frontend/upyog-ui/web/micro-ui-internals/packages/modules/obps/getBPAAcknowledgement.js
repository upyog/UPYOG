import React from "react";
import { Card, CardHeader } from "@upyog/digit-ui-react-components";

const getMohallaLocale = (value = "", tenantId = "") => {
    let convertedValue = convertDotValues(tenantId);
    if (convertedValue == "NA" || !checkForNotNull(value)) {
      return "PGR_NA";
    }
    convertedValue = convertedValue.toUpperCase();
    return convertToLocale(value, `${convertedValue}_REVENUE`);
  };
   const convertDotValues = (value = "") => {
    return (
      (checkForNotNull(value) && ((value.replaceAll && value.replaceAll(".", "_")) || (value.replace && stringReplaceAll(value, ".", "_")))) || "NA"
    );
  };
   const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
    if (searcher == "") return str;
    while (str.includes(searcher)) {
      str = str.replace(searcher, replaceWith);
    }
    return str;
  };
   const checkForNotNull = (value = "") => {
    return value && value != null && value != undefined && value != "" ? true : false;
  };
   const getCityLocale = (value = "") => {
    let convertedValue = convertDotValues(value);
    if (convertedValue == "NA" || !checkForNotNull(value)) {
      return "PGR_NA";
    }
    convertedValue = convertedValue.toUpperCase();
    return convertToLocale(convertedValue, `TENANT_TENANTS`);
  };
   const convertToLocale = (value = "", key = "") => {
    let convertedValue = convertDotValues(value);
    if (convertedValue == "NA") {
      return "PGR_NA";
    }
    return `${key}_${convertedValue}`;
  };
  const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
  const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");
  const getBPAAcknowledgement=async(application,tenantInfo,t)=>{
    
    const owners=application?.landInfo?.owners

    const getOwnersForNewApplication =()=>{
        let values = [];
        owners.map((owner) => {
            let doc = [
                {  
                    title: t("CORE_COMMON_NAME"), 
                    value:owner?.name|| "NA"
                },
                { 
                    title: t("BPA_APPLICANT_GENDER_LABEL"), 
                    value: owner?.gender || "NA"
                },
                { 
                    title: t("CORE_COMMON_MOBILE_NUMBER"), 
                    value: owner?.mobileNumber || "NA"
                },
                { 
                    title: t("CORE_COMMON_EMAIL_ID"), 
                    value: owner?.emailId || "NA"
                },
                { 
                    title: application?.businessService==="BPA-PAP" ? t("PRIMARY_OWNER_LABEL") : t("BPA_IS_PRIMARY_OWNER_LABEL"), 
                    value: application?.businessService==="BPA-PAP" ? owners.length===1 ? "Single Owner" : "Multiple Owner" : owner?.isPrimaryOwner || "NA"
                }
            ];
               values.push(...doc);
          });
          return values
    }
    return{
        t: t,
        tenantId: tenantInfo?.code,
        name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
        email: tenantInfo?.emailId,
        phoneNumber: tenantInfo?.contactNumber,
        heading: t("NEW_BUILD_PERMIT_APPLICATION"),
        applicationNumber:application?.applicationNo||"NA",
        details:[
            {
                title : t("BPA_BASIC_DETAILS_TITLE"),
                values:[
                    { 
                        title: application?.businessService ==="BPA-PAP" ? t("BPA_DRAWING_NUMBER") : application?.businessService !== t("BPA_OC") ? t("BPA_EDCR_NO_LABEL") : t("BPA_OC_EDCR_NO_LABEL"), 
                        value: application?.edcrNumber || "NA" 
                    },
                    { 
                        title: t("BPA_BASIC_DETAILS_APP_DATE_LABEL"),
                        value: Digit.DateUtils.ConvertTimestampToDate(application?.auditDetails?.createdTime, "dd/MM/yyyy") || "NA",
                    },
                    { 
                        title: t("BPA_BASIC_DETAILS_APPLICATION_TYPE_LABEL"), 
                        value: t(`WF_BPA_${application?.data?.edcrDetails?.appliactionType||application?.data?.applicationType}`) || "NA"
                    },
                    {
                        title: t("BPA_IS_PREAPPROVED"), 
                        value:application?.additionalDetails?.isPreApproved ? application?.additionalDetails?.isPreApproved: application?.businessService==="BPA-PAP" ? "true" : "false"
                    },
                    { 
                        title: t("BPA_BASIC_DETAILS_SERVICE_TYPE_LABEL"), 
                        value:t(`${application?.data?.edcrDetails?.applicationSubType||application?.data?.edcrDetails?.drawingDetail?.serviceType}`) || "NA"
                    },
                    { 
                        title: t("BPA_BASIC_DETAILS_OCCUPANCY_LABEL"), 
                        value: application?.data?.edcrDetails?.planDetail?.planInformation?.occupancy ||application?.data?.edcrDetails?.drawingDetail?.occupancy
                    },
                    { 
                        title: t("BPA_BASIC_DETAILS_RISK_TYPE_LABEL"), 
                        value: t(`WF_BPA_${application?.riskType}`) || "NA"
                    },
                    { 
                        title: t("BPA_BASIC_DETAILS_APPLICATION_NAME_LABEL"), 
                        value: application?.data?.edcrDetails?.planDetail?.planInformation?.applicantName || application?.additionalDetails?.applicantName
                    },
                    
                ]
            
             },
             {
                title: t("BPA_PLOT_DETAILS_TITLE"),
                values: [
                    { 
                        title: t("BPA_BOUNDARY_PLOT_AREA_LABEL"),
                        value: `${application?.data?.edcrDetails?.planDetail?.planInformation?.plotArea|| application?.data?.edcrDetails?.drawingDetail?.plotArea} sq.ft` || "NA"
                    },
                    { 
                        title: t("BPA_PLOT_NUMBER_LABEL"), 
                        value: application?.data?.edcrDetails?.planDetail?.planInformation?.plotNo || application?.additionalDetails?.plotNo||"NA"  
                    },
                    { 
                        title: t("BPA_KHATHA_NUMBER_LABEL"), 
                        value: application?.data?.edcrDetails?.planDetail?.planInformation?.khataNo || application?.additionalDetails?.khataNo||"NA"  
                    },
                    { 
                        title: t("BPA_HOLDING_NUMBER_LABEL"), 
                        value: application?.additionalDetails?.holdingNo || "NA" 
                    },
                    { 
                        title: t("BPA_BOUNDARY_LAND_REG_DETAIL_LABEL"), 
                        value: application?.additionalDetails?.registrationDetails || "NA" 
                    },
                    { 
                        title: t("BPA_APPLICATION_DEMOLITION_AREA_LABEL"), 
                        value: application?.data?.edcrDetails?.planDetail?.planInformation?.demolitionArea ? `${application?.data?.edcrDetails?.planDetail?.planInformation?.demolitionArea} sq mts`:"NA"
                    } 
                ]
             },
             application?.nocDocuments?.NocDetails[0] ? 
             {
                title: t(`BPA_NOC_DETAILS_SUMMARY`) , 
                values: [
                    {
                      title: t(`BPA_${application?.nocDocuments?.NocDetails[0]?.nocType}_LABEL`),
                      value: application?.nocDocuments?.NocDetails[0]?.applicationNo || "NA", 
                    },
                    {
                        title: t(`BPA_${application?.nocDocuments?.NocDetails[1]?.nocType}_LABEL`),
                        value: application?.nocDocuments?.NocDetails[1]?.applicationNo || "NA",
                      
                    },
                    
                ],    
             }:null,
             {
                title: t("BPA_APPLICANT_DETAILS_HEADER"),
                values:  getOwnersForNewApplication()
            },
            {
                title : t("BPA_NEW_TRADE_DETAILS_HEADER_DETAILS"),
                values:[
                    { 
                        title: t("BPA_DETAILS_PROPERTYID"), 
                        value: application?.additionalDetails?.propertyID ||"NA"
                    },
                    { 
                        title: t("BPA_DETAILS_PIN_LABEL"), 
                        value: application?.landInfo?.address?.pincode || "NA"
                    },
                    { 
                        title: t("BPA_CITY_LABEL"), 
                        value: application?.landInfo?.address?.city || "NA"
                    },
                    { 
                        title: t("BPA_LOC_MOHALLA_LABEL"), 
                        value: application?.landInfo?.address?.locality?.name || "NA"
                    },
                    { 
                        title: t("BPA_DETAILS_SRT_NAME_LABEL"), 
                        value: application?.landInfo?.address?.street || "NA"
                    },
                    { 
                        title: t("ES_NEW_APPLICATION_LOCATION_LANDMARK"), 
                        value: application?.landInfo?.address?.landmark || "NA"
                    }
                ]
            },
            
            
        ]
    }
  }
  export default getBPAAcknowledgement;