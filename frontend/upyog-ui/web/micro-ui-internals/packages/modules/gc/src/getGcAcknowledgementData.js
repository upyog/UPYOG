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
 * Shapes a Garbage Collection application into the common acknowledgement-PDF format.
 * It supports both the GC create payload and the persisted garbage-account response.
 */
const getGcAcknowledgementData = async (application = {}, tenantInfo, t) => {
  const account = application?.garbageAccounts?.[0] || application?.garbageAccount || application;
  const applicationDetails = account?.grbgApplication || account?.GarbageApplication || {};
  const applicant =
    account?.additionalDetail?.applicantDetails?.[0] ||
    account?.additionalDetails?.applicantDetails?.[0] ||
    account?.applicantDetails?.[0] ||
    {};
  const address = account?.addresses?.[0] || account?.address || account?.propertyLocation || {};
  const addressDetails = address?.additionalDetail || address?.additionalDetails || {};
  const collectionUnit = account?.grbgCollectionUnits?.[0] || account?.garbageSpecification || {};

  return {
    t,
    tenantId: tenantInfo?.code || account?.tenantId,
    name: getTenantName(tenantInfo, t),
    email: getValue(tenantInfo?.emailId, t),
    applicationNumber: getValue(
      applicationDetails?.applicationNo || account?.grbgApplicationNumber || account?.applicationNo,
      t
    ),
    phoneNumber: getValue(tenantInfo?.contactNumber, t),
    heading: t("GC_APPLICATION_DETAILS"),
    details: [
      {
        title: t("ES_APPLICANT_DETAILS"),
        values: [
          { title: t("GC_APPLICANT_NAME"), value: getValue(applicant?.applicantName || applicant?.name || account?.name, t) },
          { title: t("GC_MOBILE_NUMBER"), value: getValue(applicant?.mobileNumber || account?.mobileNumber, t) },
          { title: t("GC_ALT_MOBILE_NUMBER"), value: getValue(applicant?.alternateNumber || account?.alternateNumber, t) },
          { title: t("GC_EMAIL_ID"), value: getValue(applicant?.emailId || applicant?.email || account?.emailId || account?.email, t) },
        ],
      },
      {
        title: t("GC_PROPERTY_LOCATION_DETAILS"),
        values: [
          { title: t("GC_PROPERTY_ID"), value: getValue(account?.propertyId || address?.propertyId, t) },
          { title: t("GC_HOUSE_NO"), value: getValue(addressDetails?.houseNo || address?.houseNo, t) },
          { title: t("GC_BUILDING_NAME"), value: getValue(addressDetails?.houseName || address?.houseName, t) },
          { title: t("GC_STREET_NAME"), value: getValue(addressDetails?.streetName || address?.streetName, t) },
          { title: t("GC_ADDRESS_LINE1"), value: getValue(address?.address1 || address?.addressLine1, t) },
          { title: t("GC_ADDRESS_LINE2"), value: getValue(address?.address2 || address?.addressLine2, t) },
          { title: t("GC_LANDMARK"), value: getValue(addressDetails?.landmark || address?.landmark, t) },
          { title: t("GC_CITY"), value: getValue(address?.city, t) },
          { title: t("GC_LOCALITY"), value: getValue(addressDetails?.locality || address?.locality, t) },
          { title: t("GC_ADDRESS_PINCODE"), value: getValue(address?.pincode, t) },
        ],
      },
      {
        title: t("GC_GARBAGE_SPECIFICATIONS"),
        values: [
          { title: t("GC_OLD_GARBAGE_ID"), value: getValue(collectionUnit?.oldGarbageId, t) },
          { title: t("GC_TYPE_OF_COLLECTION"), value: getValue(collectionUnit?.unitType || collectionUnit?.typeOfCollection, t) },
          { title: t("GC_OWNER_OR_TENANT"), value: getValue(collectionUnit?.ownerType || collectionUnit?.propertyOwnerType, t) },
          { title: t("GC_CATEGORY"), value: getValue(collectionUnit?.category, t) },
          { title: t("GC_SUB_CATEGORY"), value: getValue(collectionUnit?.subCategory, t) },
          { title: t("GC_SUB_CATEGORY_TYPE"), value: getValue(collectionUnit?.subCategoryType, t) },
          { title: t("GC_SPECIAL_CATEGORY"), value: getValue(collectionUnit?.specialCategory, t) },
          { title: t("GC_NO_OF_UNITS"), value: getValue(collectionUnit?.no_of_units, t) },
        ],
      },
    ],
  };
};

export default getGcAcknowledgementData;
