import { convertEpochToDate } from "./index";

const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase()?.split(" ")?.map(capitalize).join(" ");

const getNocDetails = (application, t) => {
  return {
    title: t("NOC_NOC_DETAILS_HEADER"),
    values: [
      { title: t("NOC_APPLICATION_NO_LABEL"), value: application?.fireNOCDetails?.applicationNumber || t("CS_NA") },
      {
        title: t("NOC_APPLICATION_DATE_LABEL"),
        value: application?.auditDetails?.createdTime ? convertEpochToDate(application?.auditDetails?.createdTime) : t("CS_NA"),
      },
      { title: t("NOC_TYPE_LABEL"), value: t(`NOC_TYPE_${application?.fireNOCDetails?.fireNOCType}`) || t("CS_NA") },
      { title: t("NOC_PROVISIONAL_FIRE_NOC_NO_LABEL"), value: application?.fireNOCDetails?.provisionalNocNumber || t("CS_NA") },
    ],
  };
};

const getPropertyDetails = (application, t) => {
  const building = application?.fireNOCDetails?.buildings?.[0] || {};

  // Build dynamic building details list
  let values = [
    { title: t("NOC_PROPERTY_TYPE_LABEL"), value: application?.fireNOCDetails?.noOfBuildings || t("CS_NA") },
    { title: t("NOC_NAME_OF_BUILDING_LABEL"), value: building?.name || t("CS_NA") },
    { title: t("NOC_PROPERTY_DETAILS_BUILDING_USAGE_TYPE_LABEL"), value: building?.usageType ? t(`FIRENOC_BUILDINGTYPE_${building.usageType.split('.')[0]}`) : t("CS_NA") },
    { title: t("NOC_PROPERTY_DETAILS_BUILDING_USAGE_SUBTYPE_LABEL"), value: building?.usageType ? t(`FIRENOC_BUILDINGTYPE_${building.usageType.replaceAll(".", "_").replaceAll("-", "_")}`) : t("CS_NA") }
  ];

  // Add building UOMs if present
  if (building?.uoms) {
    building.uoms.forEach((uom) => {
      if (uom.active) {
        values.push({
          title: t(`NOC_PROPERTY_DETAILS_${uom.code}_LABEL`),
          value: uom.value || t("CS_NA")
        });
      }
    });
  }

  return {
    title: t("NOC_COMMON_PROPERTY_DETAILS"),
    values: values,
  };
};

const getPropertyLocationDetails = (application, t) => {
  const address = application?.fireNOCDetails?.propertyDetails?.address || {};
  return {
    title: t("NOC_COMMON_PROPERTY_LOCATION_SUMMARY"),
    values: [
      { title: t("NOC_LOCATION_CITY_LABEL"), value: t(address?.city) || t("CS_NA") },
      { title: t("NOC_LOCATION_MOHALLA_LABEL"), value: t(address?.locality?.code) || t("CS_NA") },
      { title: t("NOC_LOCATION_PLOT_NO_LABEL"), value: address?.doorNo || t("CS_NA") },
      { title: t("NOC_LOCATION_STREET_LABEL"), value: address?.street || t("CS_NA") },
      { title: t("NOC_LOCATION_BUILDING_LABEL"), value: address?.buildingName || t("CS_NA") },
      { title: t("NOC_LOCATION_PINCODE_LABEL"), value: address?.pincode || t("CS_NA") },
      { title: t("NOC_LOCATION_FIRESTATION_LABEL"), value: address?.additionalDetails?.fireStation || t("CS_NA") },
    ],
  };
};

const getApplicantDetails = (application, t) => {
  const applicantDetails = application?.fireNOCDetails?.applicantDetails || {};
  const owners = applicantDetails?.owners || [];

  let values = [
    { title: t("NOC_OWNERSHIP_TYPE_LABEL"), value: t(applicantDetails?.ownerShipType) || t("CS_NA") },
  ];

  owners.forEach((owner) => {
    values.push(
      { title: t("NOC_OWNER_NAME_LABEL"), value: owner?.name || t("CS_NA") },
      { title: t("NOC_OWNER_MOBILE_NO_LABEL"), value: owner?.mobileNumber || t("CS_NA") },
      { title: t("NOC_OWNER_GENDER_LABEL"), value: t(owner?.gender) || t("CS_NA") },
      { title: t("NOC_OWNER_FATHER_HUSBAND_NAME_LABEL"), value: owner?.fatherOrHusbandName || t("CS_NA") },
      { title: t("NOC_OWNER_CORRESPONDENCE_ADDRESS_LABEL"), value: owner?.correspondenceAddress || t("CS_NA") }
    );
  });

  return {
    title: applicantDetails?.ownerShipType?.startsWith("INSTITUTION") ? t("NOC_INSTITUTION_DETAILS_HEADER") : t("NOC_APPLICANT_DETAILS_HEADER"),
    values: values,
  };
};

const getUploadedDocuments = (application, t, fileStoreData) => {
  const documents = application?.fireNOCDetails?.additionalDetail?.documents || [];

  const values = documents.map((doc, idx) => {
    let docLink = fileStoreData?.[doc?.fileStoreId] || "";
    let cleanLink = docLink?.split(",")?.[0] || "";
    let docName = decodeURIComponent(cleanLink.split("?")[0].split("/").pop().slice(13)) || `${t(doc?.documentType)} - ${idx + 1}`;

    return {
      title: t(doc?.documentType?.replaceAll(".", "_")),
      value: docName,
    };
  });

  return {
    title: t("NOC_SUMMARY_DOCUMENTS_HEADER"),
    values: values,
  };
};

const getNocAcknowledgementData = async (application, tenantInfo, t) => {
  const filesArray = application?.fireNOCDetails?.additionalDetail?.documents?.map((value) => value?.fileStoreId) || [];
  let fileStoreData = {};
  if (filesArray.length > 0) {
    try {
      const res = await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId());
      fileStoreData = res?.data || {};
    } catch (err) {
      console.error("Failed to fetch documents for NOC PDF:", err);
    }
  }

  return {
    t: t,
    tenantId: tenantInfo?.code,
    title: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    heading: t("NOC_ACKNOWLEDGEMENT_PDF_HEADING", "NOC Acknowledgement"),
    applicationNumber: application?.fireNOCDetails?.applicationNumber,
    details: [
      getNocDetails(application, t),
      getPropertyDetails(application, t),
      getPropertyLocationDetails(application, t),
      getApplicantDetails(application, t),
      getUploadedDocuments(application, t, fileStoreData),
    ],
  };
};

export default getNocAcknowledgementData;
