import { useQuery } from "react-query";
import { Search } from "../../services/molecules/OBPS/Search";

const useEstimateDetails = (filters, enabled, params, config, key = "OBPS_PREAPPROVEESTIMATE") => {
  return useQuery(
    [key, filters], 
    async () => {
      if (filters && filters.CalulationCriteria && enabled) {
        return Search.estimateDetails(filters, enabled, params);
      } else if(enabled){
        return Search.estimateDetailsWithParams(filters, params);
      }
    },
    config
  );
}

export default useEstimateDetails;
