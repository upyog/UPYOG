import { queryTemplate } from "../../common/queryTemplate";
import HrmsService from "../../services/elements/HRMS";

export const useHRMSCount = (tenantId, config = {}) => {
  const queryKey = ["HRMS_COUNT", tenantId];

  return queryTemplate({
    queryKey,
    queryFn: () => HrmsService.count(tenantId),
    config,
  });
};

export default useHRMSCount;