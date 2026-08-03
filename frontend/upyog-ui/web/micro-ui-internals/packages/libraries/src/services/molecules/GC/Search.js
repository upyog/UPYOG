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

  BookingDetails: ({ garbageAccounts: response, t }) => {
    // Try to handle different response shapes; normalize to `response` as the app object
    const app = (response && (response.garbageAccount || response)) || response;

    const appDetails = app?.grbgApplication || app?.GarbageApplication || app || {};

    const owners = app?.additionalDetail?.applicantDetails || app?.additionalDetails?.applicantDetails || app?.applicantDetails || [];
    const ownerArr = Array.isArray(owners) ? owners : owners ? [owners] : [];
    const ownerNames = ownerArr?.map((o) => o?.name || o?.applicantName)?.filter(Boolean)?.join(", ") || app?.name || t("CS_NA");
    const mobileNumbers = ownerArr?.map((o) => o?.mobileNumber)?.filter(Boolean)?.join(", ") || app?.mobileNumber || t("CS_NA");
    const emails = ownerArr?.map((o) => o?.emailId || o?.email)?.filter(Boolean)?.join(", ") || app?.emailId || t("CS_NA");

    let address = app?.addresses?.[0] || app?.address || app?.propertyLocation || {};
    if (Array.isArray(address)) address = address[0] || {};
    const addressAdditional = address?.additionalDetail || address?.additionalDetails || {};
    const propertyId = address?.propertyId || app?.propertyId || t("CS_NA");
    const pincode = address?.pincode || address?.pinCode || address?.zipCode || app?.pincode || t("CS_NA");
    const city = address?.city || address?.cityCode || address?.cityName || app?.city || t("CS_NA");
    const localityObj = address?.locality || addressAdditional?.locality || address?.mohalla || app?.locality;
    const localityText = localityObj?.name ? t(localityObj.name) : localityObj?.code ? t(localityObj.code) : typeof localityObj === "string" ? t(localityObj) : t("CS_NA");
    const street = address?.street || addressAdditional?.streetName || address?.streetName || app?.streetName || t("CS_NA");
    const houseNo = address?.doorNo || addressAdditional?.houseNo || address?.houseNo || app?.doorNo || t("CS_NA");
    const buildingName = address?.buildingName || addressAdditional?.houseName || address?.premise || app?.buildingName || t("CS_NA");
    const addressLine1 = address?.addressLine1 || address?.address1 || app?.addressLine1 || t("CS_NA");
    const addressLine2 = address?.addressLine2 || address?.address2 || app?.addressLine2 || t("CS_NA");
    const landmark = address?.landmark || addressAdditional?.landmark || app?.landmark || t("CS_NA");

    const specs = app?.grbgCollectionUnits?.[0] || app?.garbageSpecification || {};
    const typeOfCollection = specs?.typeOfCollection || specs?.unitType || specs?.collectionType || t("CS_NA");
    const category = specs?.category || specs?.propertyCategory || t("CS_NA");
    const subCategory = specs?.subCategory || specs?.propertySubCategory || t("CS_NA");
    const ownerOrTenant = app?.isOwner !== undefined ? (app.isOwner ? "Owner" : "Tenant") : (specs?.propertyOwnerType || specs?.ownerOrTenant || t("CS_NA"));
    const estimatedQuantity = specs?.estimatedQuantity ?? specs?.no_of_units ?? specs?.wasteQuantity ?? t("CS_NA");
    const collectionFrequency = specs?.collectionFrequency || specs?.frequency || t("CS_NA");
    const wasteType = specs?.wasteType || specs?.typeOfWaste || t("CS_NA");
    const specialRequest = specs?.specialRequest || specs?.remarks || t("CS_NA");

    const detailsArray = [
      { title: "GC_APPLICATION_SUMMARY", asSectionHeader: true, values: [{ title: "GC_APPLICATION_NUMBER_LABEL", value: appDetails?.applicationNo || app?.grbgApplicationNumber || app?.applicationNo || t("CS_NA") }, { title: "GC_APPLICATION_STATUS_LABEL", value: appDetails?.status || app?.status || app?.applicationStatus || t("CS_NA") }] },
      { title: "ES_APPLICANT_DETAILS", asSectionHeader: true, values: [{ title: "GC_APPLICANT_NAME", value: ownerNames }, { title: "GC_MOBILE_NUMBER", value: mobileNumbers }, { title: "GC_EMAIL_ID", value: emails }] },
      { title: "GC_PROPERTY_LOCATION_DETAILS", asSectionHeader: true, values: [{ title: "GC_PROPERTY_ID", value: propertyId }, { title: "GC_PINCODE", value: pincode }, { title: "GC_CITY", value: city ? t(city) : t("CS_NA") }, { title: "GC_LOCALITY", value: localityText }, { title: "GC_STREET_NAME", value: street }, { title: "GC_HOUSE_NO", value: houseNo }, { title: "GC_BUILDING_NAME", value: buildingName }, { title: "GC_ADDRESS_LINE1", value: addressLine1 }, { title: "GC_ADDRESS_LINE2", value: addressLine2 }, { title: "GC_LANDMARK", value: landmark }] },
      { title: "GC_GARBAGE_SPECIFICATIONS", asSectionHeader: true, values: [{ title: "GC_TYPE_OF_COLLECTION", value: typeOfCollection ? t(typeOfCollection) : t("CS_NA") }, { title: "GC_CATEGORY", value: category ? t(category) : t("CS_NA") }, { title: "GC_SUB_CATEGORY", value: subCategory ? t(subCategory) : t("CS_NA") }, { title: "GC_OWNER_OR_TENANT", value: ownerOrTenant ? t(ownerOrTenant) : t("CS_NA") }, { title: "GC_ESTIMATED_QUANTITY", value: estimatedQuantity }, { title: "GC_COLLECTION_FREQUENCY", value: collectionFrequency ? t(collectionFrequency) : t("CS_NA") }, { title: "GC_WASTE_TYPE", value: wasteType ? t(wasteType) : t("CS_NA") }, { title: "GC_SPECIAL_REQUEST", value: specialRequest }] },
      {
        title: "GC_DOCUMENTS_DETAILS",
        additionalDetails: {
          documents: [
            {
              values: app?.documents?.map((document) => ({
                title: `GC_${document?.documentType?.split('.').slice(0, 2).join('_')}`,
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

    return detailsArray;
  },

  applicationDetails: async (t, tenantId, applicationNo, userType, args) => {
    const filter = { applicationNo, ...args };
    const response = await GCSearch.application(tenantId, filter);

    return {
      tenantId: response?.tenantId || tenantId,
      applicationDetails: GCSearch.BookingDetails({ garbageAccounts: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: GCSearch.BookingDetails,
    };
  },
};

export default GCSearch;
