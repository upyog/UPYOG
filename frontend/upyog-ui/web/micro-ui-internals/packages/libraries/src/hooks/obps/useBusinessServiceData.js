import { queryTemplate } from "../../common/queryTemplate";

const useBusinessServiceData = (tenantId, businessServices, config = {}) => {
  return queryTemplate({
    queryKey: [
      "OBPS_BUSINESS_SERVICE",
      tenantId,
      businessServices,
    ],
    queryFn: () =>
      Digit.WorkflowService.init(tenantId, businessServices),
    config,
  });
};

export default useBusinessServiceData;