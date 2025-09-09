import { useQuery, useQueryClient } from "react-query";

/**
 * useApplicationDetails Hook
 *
 * A custom React Query hook to fetch and transform complaint/application details for PGR AI.
 *
 * - Fetches service details, workflow, and related metadata from PGRAIService.
 * - Retrieves service definitions (MDMS), verification document thumbnails, and audit info.
 * - Transforms the data for display using custom logic or fallback structure.
 * - Handles localization, thumbnails, complaint type mapping, and role-based customizations.
 *
 * Returns:
 * - `complaintDetails`: structured object for UI display
 * - `isLoading`: loading state for API
 * - `error`: error state if fetch fails
 * - `revalidate()`: function to refetch complaint details on demand
 */




/**
 * Fetches thumbnail and full image URLs from file store IDs.
 * Splits the URL to get thumbnail reference and full image URLs using utility methods.
 */
const getThumbnails = async (ids, tenantId) => {
  const res = await Digit.UploadServices.Filefetch(ids, tenantId);
  if (res.data.fileStoreIds && res.data.fileStoreIds.length !== 0) {
    return { thumbs: res.data.fileStoreIds.map((o) => o.url.split(",")[3]), images: res.data.fileStoreIds.map((o) => Digit.Utils.getFileUrl(o.url)) };
  } else {
    return null;
  }
};


/**
 * Fallback/default structure for complaint details display when no custom format is provided.
 * Maps relevant fields such as complaint number, status, type, sub-type, date, and address.
 */
const getDetailsRow = ({ id, service, complaintType }) => ({ 
  CS_COMPLAINT_DETAILS_COMPLAINT_NO: id,
  CS_COMPLAINT_DETAILS_APPLICATION_STATUS: `CS_COMMON_${service.applicationStatus}`,
  CS_ADDCOMPLAINT_COMPLAINT_TYPE: complaintType === "" ? `SERVICEDEFS.OTHERS` : `SERVICEDEFS.${complaintType}`,
  CS_ADDCOMPLAINT_COMPLAINT_SUB_TYPE: `SERVICEDEFS.${service.serviceCode.toUpperCase()}`,
  CS_COMPLAINT_FILED_DATE: Digit.DateUtils.ConvertTimestampToDate(service.auditDetails.createdTime),
  ES_CREATECOMPLAINT_ADDRESS: [
    service.address.landmark,
    Digit.Utils.locale.getLocalityCode(service.address.locality, service.tenantId),
    service.address.city,
    service.address.pincode,
  ],
});

const isEmptyOrNull = (obj) => obj === undefined || obj === null || Object.keys(obj).length === 0;

/**
 * Transforms fetched complaint details into a structured object suitable for UI rendering.
 * - Applies custom format if available (based on role).
 * - Includes thumbnails, images, audit, and workflow information.
 */
const transformDetails = ({ id, service, workflow, thumbnails, complaintType }) => {
  const { Customizations, SessionStorage } = window.Digit;
  const role = (SessionStorage.get("user_type") || "CITIZEN").toUpperCase();
  const customDetails = Customizations?.PGR?.getComplaintDetailsTableRows
    ? Customizations.PGR.getComplaintDetailsTableRows({ id, service, role })
    : {};
  return {
    details: !isEmptyOrNull(customDetails) ? customDetails : getDetailsRow({ id, service, complaintType }),
    thumbnails: thumbnails?.thumbs,
    images: thumbnails?.images,
    workflow: workflow,
    service,
    audit: {
      citizen: service.citizen,
      details: service.auditDetails,
      source: service.source,
      rating: service.rating,
      serviceCode: service.serviceCode,
      prioritylevel : service.priorityLevel
    },
    service: service,
  };
};



/**
 * Fetches complaint details from PGRAI services.
 * - Retrieves service metadata, workflow, and document attachments.
 * - Applies transformation for frontend use.
 */
const fetchComplaintDetails = async (tenantId, id) => {
  var serviceDefs = await Digit.MDMSService.getServiceDefs(tenantId, "PGR");
  const { service, workflow } = (await Digit.PGRAIService.search(tenantId, { serviceRequestId: id })).ServiceWrappers[0] || {};
  Digit.SessionStorage.set("complaintDetails", { service, workflow });
  if (service && workflow && serviceDefs) {
    const complaintType = serviceDefs.filter((def) => def.serviceCode === service.serviceCode)[0]?.menuPath?.toUpperCase();
    const ids = workflow.verificationDocuments
      ? workflow.verificationDocuments.filter((doc) => doc.documentType === "PHOTO").map((photo) => photo.fileStoreId || photo.id)
      : null;
    const thumbnails = ids ? await getThumbnails(ids, service.tenantId) : null;
    const details = transformDetails({ id, service, workflow, thumbnails, complaintType });
    return details;
  } else {
    return {};
  }
};


/**
 * Custom React Query hook to retrieve complaint/application details.
 * Caches response and provides revalidation capability via React Query's queryClient.
 */
const useApplicationDetails = ({ tenantId, id }) => {
  const queryClient = useQueryClient();
  const { isLoading, error, data } = useQuery(["complaintDetails", tenantId, id], () => fetchComplaintDetails(tenantId, id));
  return { isLoading, error, complaintDetails: data, revalidate: () => queryClient.invalidateQueries(["complaintDetails", tenantId, id]) };
};

export default useApplicationDetails;
