import { queryTemplate } from "../../common/queryTemplate";

const mapWfBybusinessId = (wfs) =>
  wfs.reduce((acc, item) => {
    acc[item.businessId] = item;
    return acc;
  }, {});

const combineResponses = (res, wf) => {
  const wfMap = mapWfBybusinessId(wf.ProcessInstances);

  return res.ServiceWrappers.reduce((acc, c) => {
    const wfItem = wfMap[c.service.serviceRequestId];
    if (!wfItem) return acc;

    acc.push({
      serviceRequestId: c.service.serviceRequestId,
      complaintSubType: c.service.serviceCode,
      priorityLevel: c.service.priority,
      locality: c.service.address.locality.code,
      status: c.service.applicationStatus,
      taskOwner: wfItem?.assignes?.[0]?.name || "-",
      sla: Math.round(
        wfItem.businesssServiceSla / (24 * 60 * 60 * 1000)
      ),
      tenantId: c.service.tenantId,
    });

    return acc;
  }, []);
};

const useInboxData = (searchParams) => {
  return queryTemplate({
    queryKey: ["fetchInboxData", searchParams],

    queryFn: async () => {
      const tenantId = Digit.ULBService.getCurrentTenantId();

      const { limit, offset } = searchParams;

      const appFilters = {
        start: 1,
        end: 10,
        ...searchParams.filters.pgrQuery,
        ...searchParams.search,
        limit,
        offset,
      };

      const wfFilters = {
        start: 1,
        end: 10,
        ...searchParams.filters.wfQuery,
      };

      const res = await Digit.PGRService.search(tenantId, appFilters);

      const ids = res.ServiceWrappers.map(
        (s) => s.service.serviceRequestId
      ).join();

      const wf = await Digit.WorkflowService.getByBusinessId(
        tenantId,
        ids,
        wfFilters,
        false
      );

      if (!wf.ProcessInstances.length) return [];

      return combineResponses(res, wf);
    },

    config: {
      staleTime: Infinity,
    },
  });
};

export default useInboxData;