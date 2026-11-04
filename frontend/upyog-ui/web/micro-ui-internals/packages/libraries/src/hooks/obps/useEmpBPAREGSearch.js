import { queryTemplate } from "../../common/queryTemplate";

const useEmpBPAREGSearch = (tenantId, filters, params, config = {}) => {
  return queryTemplate({
    queryKey: [
      "OBPS_EMP_BPA_REG_SEARCH",
      tenantId,
      JSON.stringify(filters),
      JSON.stringify(params),
    ],
    queryFn: async () => {
      const response = await Digit.OBPSService.BPAREGSearch(
        tenantId,
        filters,
        params
      );

      const businessIds = response?.Licenses.map(
        (a) => a.applicationNumber
      );

      const workflowRes =
        await Digit.WorkflowService.getAllApplication(
          Digit.ULBService.getStateId(),
          { businessIds: businessIds.join() }
        );

      return response?.Licenses.map((app) => ({
        ...app,
        state:
          workflowRes?.ProcessInstances?.find(
            (wf) => wf.businessId === app.applicationNumber
          )?.state?.state,
        Count: response?.Count,
      }));
    },
    config,
  });
};

export default useEmpBPAREGSearch;