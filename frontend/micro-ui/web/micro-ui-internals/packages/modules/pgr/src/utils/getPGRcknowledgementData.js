import { ComplaintDetails } from "../pages/employee/ComplaintDetails";

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
const getPGRcknowledgementData = async (complaintDetails,tenantInfo, t) => {
   
      return {
        t: t,
        tenantId: tenantInfo?.code,
        name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
        email: tenantInfo?.emailId,
        phoneNumber: tenantInfo?.contactNumber,
        heading: t("NEW_GRIEVANCE_APPLICATION"),
        applicationNumber:complaintDetails?.service?.serviceRequestId,
        details: [
          {
            title: t("CS_TITLE_APPLICATION_DETAILS"),
            values: [
              {
                title: t("CS_COMPLAINT_FILED_DATE"),
                value: Digit.DateUtils.ConvertTimestampToDate(complaintDetails?.audit?.details?.createdTime, "dd/MM/yyyy"),
              },
              {
                title: t("CS_COMPLAINT_TYPE"),
                value: complaintDetails?.details?.CS_ADDCOMPLAINT_COMPLAINT_TYPE,
              },
              {
                title: t("CS_COMPLAINT_SUB_TYPE"),
                value: complaintDetails?.details?.CS_ADDCOMPLAINT_COMPLAINT_SUB_TYPE,
              },
              {
                title: t("CS_COMPLAINT_PRIORITY_LEVEL"),
                value: complaintDetails?.service?.priority,
              },
              {
                title: t("CS_COMPLAINT_ADDITIONAL_DETAILS"),
                value: complaintDetails?.details?.CS_COMPLAINT_ADDTIONAL_DETAILS||"NA",
              },
            ],
          },
          {
            title: t("PGR_ADDRESS_SUB_HEADER"),
            values: [
              { title: t("PGR_ADDRESS_PINCODE"), value: complaintDetails?.service?.address?.pincode || t("CS_NA") },
              { title: t("PT_ADDRESS_CITY"), value: t(getCityLocale(complaintDetails?.service?.tenantId)) || t("CS_NA") },
              {
                title: t("PT_ADDRESS_MOHALLA"),
                value: t(`${getMohallaLocale(complaintDetails?.service?.address?.locality?.code, tenantInfo?.code)}`) || t("CS_NA"),
              },
              { title: t("PGR_ADDRESS_STREET_NAME"), value: complaintDetails?.service?.address?.street || t("CS_NA") },
              { title: t("PGR_ADDRESS_HOUSE_NO"), value: complaintDetails?.service?.address?.doorNo || t("CS_NA") },
              { title: t("PGR_ADDRESS_LANDMARK"), value: complaintDetails?.service?.address?.landmark || t("CS_NA") },
            ],
          },
         
        ],
      };
    

  };
  
  export default getPGRcknowledgementData;