import { queryTemplate } from "../../common/queryTemplate";

const getThumbnails = async (ids, tenantId) => {
  const res = await Digit.UploadServices.Filefetch(ids, tenantId);
  if (res.data.fileStoreIds?.length) {
    return {
      thumbs: res.data.fileStoreIds.map((o) => o.url.split(",")[3]),
      images: res.data.fileStoreIds.map((o) =>
        Digit.Utils.getFileUrl(o.url)
      ),
    };
  }
  return null;
};

const getDetailsRow = ({ id, service, complaintType }) => ({
  CS_COMPLAINT_DETAILS_COMPLAINT_NO: id,
  CS_COMPLAINT_DETAILS_APPLICATION_STATUS: `CS_COMMON_${service.applicationStatus}`,
  CS_ADDCOMPLAINT_COMPLAINT_TYPE:
    complaintType === "" ? `SERVICEDEFS.OTHERS` : `SERVICEDEFS.${complaintType}`,
  CS_ADDCOMPLAINT_COMPLAINT_SUB_TYPE: `SERVICEDEFS.${service.serviceCode.toUpperCase()}`,
  CS_ADDCOMPLAINT_PRIORITY_LEVEL: service?.priority,
  CS_COMPLAINT_ADDTIONAL_DETAILS: service.description,
  CS_COMPLAINT_FILED_DATE: Digit.DateUtils.ConvertTimestampToDate(
    service.auditDetails.createdTime
  ),
  ES_CREATECOMPLAINT_ADDRESS: [
    service.address.landmark,
    Digit.Utils.locale.getLocalityCode(
      service.address.locality,
      service.tenantId
    ),
    service.address.city,
    service.address.pincode,
  ],
});

const isEmpty = (obj) =>
  obj === undefined || obj === null || Object.keys(obj).length === 0;

export const transformDetails = ({ id, service, workflow, thumbnails, complaintType }) => {
  const { Customizations, SessionStorage } = window.Digit;
  const role = (SessionStorage.get("user_type") || "CITIZEN").toUpperCase();

  const custom =
    Customizations?.PGR?.getComplaintDetailsTableRows?.({
      id,
      service,
      role,
    }) || {};

  return {
    details: !isEmpty(custom)
      ? custom
      : getDetailsRow({ id, service, complaintType }),
    thumbnails: thumbnails?.thumbs,
    images: thumbnails?.images,
    workflow,
    service,
    audit: {
      citizen: service.citizen,
      details: service.auditDetails,
      source: service.source,
      rating: service.rating,
      serviceCode: service.serviceCode,
      prioritylevel: service.priorityLevel,
    },
  };
};

const useComplaintDetails = ({ tenantId, id }) => {
  return queryTemplate({
    queryKey: ["complaintDetails", tenantId, id],

    queryFn: async () => {
      const serviceDefs = await Digit.MDMSService.getServiceDefs(
        tenantId,
        "PGR"
      );

      const { service, workflow } =
        (
          await Digit.PGRService.search(tenantId, {
            serviceRequestId: id,
          })
        ).ServiceWrappers[0] || {};

      if (!service || !workflow || !serviceDefs) return {};

      const complaintType = serviceDefs.find(
        (d) => d.serviceCode === service.serviceCode
      )?.menuPath?.toUpperCase();

      const ids =
        workflow.verificationDocuments
          ?.filter((d) => d.documentType === "PHOTO")
          ?.map((p) => p.fileStoreId || p.id) || null;

      const thumbnails = ids
        ? await getThumbnails(ids, service.tenantId)
        : null;

      return transformDetails({
        id,
        service,
        workflow,
        thumbnails,
        complaintType,
      });
    },
  });
};

export default useComplaintDetails;