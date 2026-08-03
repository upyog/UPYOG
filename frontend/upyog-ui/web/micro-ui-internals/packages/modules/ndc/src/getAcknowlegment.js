import React from "react";
import { Card, CardHeader } from "@nudmcdgnpm/digit-ui-react-components";

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
// const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

const getReadableCity = (tenantId = "") => {
  if (!tenantId) return "NA";
  const parts = tenantId.split(".");
  if (parts.length < 2) return tenantId;

  const key = parts[1].toLowerCase();

  const cityMap = {
    testing: "Testing",
    amritsar: "Amritsar",
    punjab: "Punjab",
    chandigarh: "Chandigarh",
    ludhiana: "Ludhiana",
  };

  return cityMap[key] || capitalize(key);
};

const capitalize = (text) => text?.charAt(0).toUpperCase() + text?.slice(1);

// This function prepares the data for the NDC acknowledgment certificate, including dynamic values and translated text fragments.
const getAcknowledgementData = async (application, formattedAddress, tenantInfo, t, approver,ulbType, empData, approverStatement) => {
  const appData = application?.Applications?.[0] || {};
  const owner = appData?.owners?.[0] || {};
  const ndc = appData?.NdcDetails?.[0] || {};
  const add = ndc?.additionalDetails || {};
  const approvalDate = appData?.auditDetails?.lastModifiedTime ? new Date(appData.auditDetails?.lastModifiedTime).toLocaleDateString("en-GB"): "N/A"
  const designationCode = empData?.officer?.designation; // e.g. "DESIG_68"
  const designationKey = designationCode ? `COMMON_MASTERS_DESIGNATION_${designationCode}` : null;
  const designation = designationKey ? t(designationKey, { defaultValue: designationCode }) : "NA";

  const applicationNumber = appData?.applicationNo || "NA";
  // const propertyId = ndc?.consumerCode || "NA";
  const ptObj = appData?.NdcDetails?.find(item => item.businessService === 'PT');
  const propertyId = ptObj?.consumerCode;

  const propertyType = add?.propertyType ? t(add.propertyType) : "NA";
  const applicantName = owner?.name || "NA";
  // const address = owner?.permanentAddress || owner?.correspondenceAddress || "NA";
  const address = appData?.NdcDetails?.[0]?.additionalDetails?.propertyAddress || owner?.permanentAddress || owner?.correspondenceAddress || "NA";
  const remarks = appData?.NdcDetails?.[0]?.additionalDetails?.remarks || null;

  const ulbName = tenantInfo?.name || appData?.tenantId || "NA";
  const duesAmount = add?.duesAmount || appData?.additionalDetails?.duesAmount || "0";
  const dateOfApplication = add?.dateOfApplication || "NA";
  const dateOfApproval = add?.dateOfApproval || "NA";
  const ownerNames = (application?.propertyOwnerNames || []).join(", ") || "NA";

  const readableCity = getReadableCity(appData?.tenantId);

  // Build single certificate body by concatenating translated fragments and dynamic values
  const certificateBody = [
  { text: "NDC No: ", bold: false , fontSize: 9,},
  { text: `${appData?.applicationNo}`, bold: true,  fontSize: 9, },
  { text: ", Property ID: ", bold: false , fontSize: 9,},
  { text: `${propertyId}`, bold: true ,  fontSize: 9,},
  { text: ", Property Type: ", bold: false ,  fontSize: 9, },
  { text: `${propertyType}\n`, bold: true ,  fontSize: 9, },

  { text: "Property Address: ", bold: false ,  fontSize: 9, },
  { text: `${formattedAddress}`, bold: true ,  fontSize: 9, }, { text: " Owned by: ", bold: false , fontSize: 9, }, { text: `${ownerNames}\n`, bold: true ,  fontSize: 9,},
  { text: "Applicant Name: ", bold: false , fontSize: 9},
  { text: `${applicantName}`, bold: true , fontSize: 9},
  { text: " (s/o, d/o) ", bold: false , fontSize: 9 },
  { text: `${appData?.owners?.[0]?.fatherOrHusbandName || "NA"}`, bold: true , fontSize: 9 },
  { text: " resident of ", bold: false , fontSize: 9},
  { text: `${address}.\n`, bold: true ,fontSize: 9 },
  {
    text: [
      { text: `• This is to certify that, as per the records and data with ${ulbName}, all applicable municipal dues related to the above mentioned property have been duly recovered/deposited. `, bold: true , fontSize: 9 },
      { text: `${t("NDC_CERTIFY_NOTE_ONE_PB")} ${ulbName} ${t("NDC_CERTIFY_NOTE_TWO_PB")}\n`, bold: false , fontSize: 9}
    ]
  },

  {
    text: [
      { text: `• This No Dues Certificate is valid for one month from the date of issuance.`, bold: true , fontSize: 9 },
      { text: `${t("NDC_VALIDITY_NOTE_PB")}\n`, bold: false , fontSize: 9 }
    ]
  },
  {
    text: [
      { text: `• This is only a No Dues Certificate for municipal dues as on date and it does not regulate the compliance of building regulations, change of land use, any fire safety regulations or any other compliance under any act/rules. `, bold: true , fontSize: 9},
      { text: `${t("NDC_BUILDING_NOTE_PB")}\n`, bold: false , fontSize: 9 }
    ]
  },
  {
    text: [
      { text: `• This No Dues Certificate does not bar any competent authority to take action under their prevailing act/rules. `, bold: true, fontSize: 9 },
      { text: `${t("NDC_AUTHORITY_NOTE_PB")}\n`, bold: false , fontSize: 9 }
    ]
  },
  {
    text: [
      { text: `• In case any discrepancies in the amount deposited are discovered by the Municipal Corporation/Council at any stage, it shall be the responsibility of the owner to deposit the differential amount as notified by the Municipal Corporation/Council, which will have the full right to recover the same. `, bold: true , fontSize: 9 },
      { text: `${t("NDC_DISCREPANCY_NOTE_PB")}\n`, bold: false , fontSize: 9}
    ]
  },
  {
    text: [
      { text: `• This certificate is only for the purpose of municipal dues and this certificate is not a proof of ownership. `, bold: true , fontSize: 9 },
      { text: `${t("NDC_OWNERSHIP_NOTE_PB")}\n`, bold: false , fontSize: 9 }
    ]
  },
  remarks && remarks.trim() !== "" && {
    text: [
      { text: `• Remarks:  ${remarks}\n`, bold: true, fontSize: 9 }
    ]
  },
];




  return {
    t,
    approvalDate,
    approver,
    designation,
    approverStatement,
    ulbType,
    tenantId: appData?.tenantId,
    // Use readable city dynamically
    name: ` No Dues Certificate `,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    heading: `Local Government, Punjab`,
    applicationNumber,
    details: [
      {
        value: certificateBody,
      },
    ],
  };
};
export default getAcknowledgementData;
