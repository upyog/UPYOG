import { useQueries } from "@tanstack/react-query";
import { getDSSDashboardData } from "../../services/molecules/DSS/getDSSDashboardData";

/**
 * Special Case, here used useQueries not useQuery
 * 
 * useQuery
    handles ONE API call
    returns ONE result

 * useQueries
    handles MULTIPLE API calls in parallel
    returns an ARRAY of results in the same order as the queries were defined

  
  Our Dashboard has multiple charts / cards / KPIs
 */
const useDSSDashboard = (stateCode, mdmsType, moduleCode, config) => {
  return useQueries({
    queries: getDSSDashboardData(
      stateCode,
      mdmsType,
      moduleCode,
      config
    ),
  });
};

export default useDSSDashboard;