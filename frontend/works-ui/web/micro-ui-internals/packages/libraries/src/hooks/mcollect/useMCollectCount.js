import { queryTemplate } from "../../common/queryTemplate";

const useMCollectCount = (tenantId, config = {}) => {
  return queryTemplate({
    queryKey: ["MCOLLECT_COUNT", tenantId],
    queryFn: () => Digit.MCollectService.count(tenantId),
    config,
  });
};

export default useMCollectCount;