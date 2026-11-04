import { queryTemplate } from "../../common/queryTemplate";
import { Search } from "../../services/molecules/OBPS/Search";

const useEstimateDetails = (
  filters,
  enabled,
  params,
  config = {},
  key = "OBPS_ESTIMATE"
) => {
  return queryTemplate({
    queryKey: [key, JSON.stringify(filters)],
    queryFn: async () => {
      if (!enabled) return null;

      if (filters?.CalulationCriteria) {
        return Search.estimateDetails(filters, enabled, params);
      }

      return Search.estimateDetailsWithParams(filters, params);
    },
    config: { enabled, ...config },
  });
};

export default useEstimateDetails;