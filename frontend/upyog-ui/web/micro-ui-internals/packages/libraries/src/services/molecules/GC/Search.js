import { GCServices } from "../../elements/GC";

/**
 * GCSearch
 *
 * Provides helper methods to search and format Garbage Collection (GC) application data
 * similar to the CHB module's Search.js implementation.
 */
export const GCSearch = {
  all: async (tenantId, filters = {}) => {
    const response = await GCServices.search({ tenantId, filters });
    return response;
  },

  application: async (tenantId, filters = {}) => {
    const response = await GCServices.search({ tenantId, filters });
    return (
      response?.garbageAccounts?.[0] ||
      response?.GarbageApplications?.[0] ||
      response?.data?.[0] ||
      response
    );
  },

  BookingDetails: ({ garbageAccounts: app, t }) => {
    const appDetails = app?.grbgApplication || {};
    const address = app?.addresses?.[0] || {};
    const addressAdditional = address?.additionalDetail || {};
    const collectionUnit = app?.grbgCollectionUnits?.[0] || {};
    const applicant = app?.additionalDetail?.applicantDetails?.[0] || {};

    return [
      {
        title: "GC_APPLICATION_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "GC_APPLICATION_NUMBER_LABEL", value: appDetails?.applicationNo || t("CS_NA") },
          { title: "GC_APPLICATION_STATUS_LABEL", value: appDetails?.status ? t(`GC_STATUS_${appDetails.status}`) : t("CS_NA") },
          ...(app?.dueDate ? [{ title: "GC_DUE_DATE", value: app.dueDate }] : []),
        ],
      },
      {
        title: "ES_APPLICANT_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "GC_APPLICANT_NAME", value: applicant?.applicantName || t("CS_NA") },
          { title: "GC_MOBILE_NUMBER", value: applicant?.mobileNumber || t("CS_NA") },
          { title: "GC_ALT_MOBILE_NUMBER", value: applicant?.alternateNumber || t("CS_NA") },
          { title: "GC_EMAIL_ID", value: applicant?.emailId || t("CS_NA") },
        ],
      },
      {
        title: "GC_PROPERTY_LOCATION_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "GC_PROPERTY_ID", value: app?.propertyId || t("CS_NA") },
          { title: "GC_PINCODE", value: address?.pincode || t("CS_NA") },
          { title: "GC_CITY", value: address?.city || t("CS_NA") },
          { title: "GC_LOCALITY", value: addressAdditional?.locality || t("CS_NA") },
          { title: "GC_STREET_NAME", value: addressAdditional?.streetName || t("CS_NA") },
          { title: "GC_HOUSE_NO", value: addressAdditional?.houseNo || t("CS_NA") },
          { title: "GC_BUILDING_NAME", value: addressAdditional?.houseName || t("CS_NA") },
          { title: "GC_ADDRESS_LINE1", value: address?.address1 || t("CS_NA") },
          { title: "GC_ADDRESS_LINE2", value: address?.address2 || t("CS_NA") },
          { title: "GC_LANDMARK", value: addressAdditional?.landmark || t("CS_NA") },
        ],
      },
      {
        title: "GC_GARBAGE_SPECIFICATIONS",
        asSectionHeader: true,
        values: [
          { title: "GC_OLD_GARBAGE_ID", value: app?.grbgOldDetails?.oldGarbageId || t("CS_NA") },
          { title: "GC_TYPE_OF_COLLECTION", value: collectionUnit?.unitType ? t(collectionUnit.unitType) : t("CS_NA") },
          { title: "GC_OWNER_OR_TENANT", value: collectionUnit?.ownerType ? t(collectionUnit.ownerType) : t("CS_NA") },
          { title: "GC_NAME", value: app?.name || t("CS_NA") },
          { title: "GC_PHONE_NUMBER", value: app?.mobileNumber || t("CS_NA") },
          { title: "GC_GENDER", value: app?.gender ? t(app.gender) : t("CS_NA") },
          { title: "GC_EMAIL", value: app?.emailId || t("CS_NA") },
          { title: "GC_CATEGORY", value: collectionUnit?.category ? t(collectionUnit.category) : t("CS_NA") },
          { title: "GC_SUB_CATEGORY", value: collectionUnit?.subCategory ? t(collectionUnit.subCategory) : t("CS_NA") },
          { title: "GC_SUB_CATEGORY_TYPE", value: collectionUnit?.subCategoryType ? t(collectionUnit.subCategoryType) : t("CS_NA") },
          { title: "GC_SPECIAL_CATEGORY", value: collectionUnit?.specialCategory ? t(collectionUnit.specialCategory) : t("CS_NA") },
          { title: "GC_IS_INHERITANCE", value: collectionUnit?.isInheritance ? t("YES") : t("NO") },
        ],
      },
      {
        title: "GC_GARBAGE_DOCUMENTS",
        additionalDetails: {
          documents: [
            {
              values: app?.documents?.map((document) => ({
                title: `GC_${document?.documentType?.split(".").slice(0, 5).join("_")}`,
                documentType: document?.documentType,
                documentUid: document?.documentUid,
                fileStoreId: document?.fileStoreId,
                status: document?.status,
              })) || [],
            },
          ],
        },
      },
    ];
  },

  applicationDetails: async (t, tenantId, applicationNo, userType, args) => {
    const response = await GCSearch.application(tenantId, { applicationNumber: applicationNo, ...args });

    return {
      tenantId: response?.tenantId || tenantId,
      applicationDetails: GCSearch.BookingDetails({ garbageAccounts: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: GCSearch.BookingDetails,
    };
  },
};

export default GCSearch;
