const capitalize = (text = "") => text.charAt(0).toUpperCase() + text.slice(1);

const getTenantName = (tenantInfo, t) => {
  const ulbName = tenantInfo?.i18nKey ? t(tenantInfo.i18nKey) : "";
  const ulbGrade = tenantInfo?.city?.ulbGrade;

  if (!ulbGrade) return ulbName;

  const translatedGrade = t(`ULBGRADE_${ulbGrade.toUpperCase().replace(/ |\./g, "_")}`);
  const formattedGrade = translatedGrade.toLowerCase().split(" ").map(capitalize).join(" ");
  return `${ulbName} ${formattedGrade}`.trim();
};

const getValue = (value, t) => {
  if (value === null || value === undefined || value === "") return t("CS_NA");

  if (typeof value === "object") {
    const translatedValue = value.i18nKey || value.code || value.value || value.name;
    return translatedValue ? t(translatedValue) : t("CS_NA");
  }

  return t(String(value));
};

/**
 * Shapes an NDC (No Dues Certificate) application into the common acknowledgement-PDF format.
 * Supports the NDC update/create payload, Redux form data, and the persisted Applications response.
 */
const getNdcAcknowledgementData = async (application = {}, tenantInfo, t) => {
  const appData =
    application?.Applications?.[0] ||
    application?.data?.Applications?.[0] ||
    application?.application ||
    application ||
    {};

  const applicant =
    appData?.owners?.find((o) => o?.isPrimaryOwner) ||
    appData?.owners?.[0] ||
    appData?.applicantDetails?.[0] ||
    appData?.applicant ||
    application?.NDCDetails?.PropertyDetails ||
    {};

  const ptObj = (appData?.NdcDetails || []).find(
    (item) => item?.businessService === "PT" || item?.businessService === "NDC_PROPERTY_TAX"
  );
  const wsObjs = (appData?.NdcDetails || []).filter(
    (item) => item?.businessService === "WS" || item?.businessService === "NDC_WATER_SERVICE_CONNECTION"
  );
  const swObjs = (appData?.NdcDetails || []).filter(
    (item) => item?.businessService === "SW" || item?.businessService === "NDC_SEWERAGE_SERVICE_CONNECTION"
  );

  const propertyId =
    ptObj?.consumerCode ||
    appData?.propertyId ||
    application?.NDCDetails?.cpt?.id ||
    application?.NDCDetails?.cpt?.details?.propertyId ||
    "";

  const propertyAddress =
    ptObj?.additionalDetails?.propertyAddress ||
    appData?.NdcDetails?.[0]?.additionalDetails?.propertyAddress ||
    applicant?.permanentAddress ||
    applicant?.correspondenceAddress ||
    application?.NDCDetails?.PropertyDetails?.address ||
    "";

  const propertyType =
    ptObj?.additionalDetails?.propertyType ||
    appData?.NdcDetails?.[0]?.additionalDetails?.propertyType ||
    application?.NDCDetails?.cpt?.details?.usageCategory ||
    "";

  const ndcReason =
    appData?.reason ||
    appData?.additionalDetails?.reason ||
    application?.NDCDetails?.NDCReason?.reason ||
    application?.NDCDetails?.NDCReason?.i18nKey ||
    "";

  const remarks =
    appData?.additionalDetails?.remarks ||
    appData?.NdcDetails?.[0]?.additionalDetails?.remarks ||
    application?.NDCDetails?.PropertyDetails?.remarks ||
    "";

  const waterConnectionNos =
    wsObjs?.length > 0
      ? wsObjs.map((w) => w?.consumerCode).filter(Boolean).join(", ")
      : (application?.NDCDetails?.PropertyDetails?.waterConnection || []).map((w) => w?.connectionNo).filter(Boolean).join(", ");

  const sewerageConnectionNos =
    swObjs?.length > 0
      ? swObjs.map((s) => s?.consumerCode).filter(Boolean).join(", ")
      : (application?.NDCDetails?.PropertyDetails?.sewerageConnection || []).map((s) => s?.connectionNo).filter(Boolean).join(", ");

  const applicationNo =
    appData?.applicationNo ||
    appData?.applicationNumber ||
    application?.applicationNo ||
    "";

  const applicationDate = appData?.auditDetails?.createdTime
    ? (Digit.DateUtils?.ConvertTimestampToDate?.(appData.auditDetails.createdTime, "dd/MM/yyyy") ||
       new Date(appData.auditDetails.createdTime).toLocaleDateString("en-GB"))
    : "";

  return {
    t,
    tenantId: tenantInfo?.code || appData?.tenantId || application?.tenantId,
    name: getTenantName(tenantInfo, t),
    email: getValue(tenantInfo?.emailId, t),
    applicationNumber: getValue(applicationNo, t),
    phoneNumber: getValue(tenantInfo?.contactNumber, t),
    heading: t("NDC_ACKNOWLEDGEMENT"),
    details: [
      {
        title: t("ES_APPLICANT_DETAILS"),
        values: [
          { title: t("NDC_APPLICANT_NAME"), value: getValue(applicant?.name || applicant?.applicantName || applicant?.firstName, t) },
          { title: t("NDC_MOBILE_NUMBER"), value: getValue(applicant?.mobileNumber, t) },
          { title: t("NDC_EMAIL_ID"), value: getValue(applicant?.emailId || applicant?.email, t) },
          { title: t("NDC_FATHER_HUSBAND_NAME"), value: getValue(applicant?.fatherOrHusbandName, t) },
          { title: t("NDC_GENDER"), value: getValue(applicant?.gender, t) },
        ],
      },
      {
        title: t("NDC_PROPERTY_LOCATION_DETAILS"),
        values: [
          { title: t("NDC_PROPERTY_ID"), value: getValue(propertyId, t) },
          { title: t("NDC_PROPERTY_TYPE"), value: getValue(propertyType, t) },
          { title: t("NDC_PROPERTY_ADDRESS"), value: getValue(propertyAddress, t) },
        ],
      },
      {
        title: t("NDC_APPLICATION_DETAILS"),
        values: [
          { title: t("NDC_REASON"), value: getValue(ndcReason, t) },
          { title: t("NDC_WATER_CONNECTION_NO"), value: getValue(waterConnectionNos, t) },
          { title: t("NDC_SEWERAGE_CONNECTION_NO"), value: getValue(sewerageConnectionNos, t) },
          { title: t("NDC_REMARKS"), value: getValue(remarks, t) },
          { title: t("CS_APPLICATION_DETAILS_APPLICATION_DATE"), value: getValue(applicationDate, t) },
          { title: t("CS_APPLICATION_DETAILS_APPLICATION_STATUS"), value: getValue(appData?.applicationStatus, t) },
        ],
      },
    ],
  };
};

export default getNdcAcknowledgementData;
