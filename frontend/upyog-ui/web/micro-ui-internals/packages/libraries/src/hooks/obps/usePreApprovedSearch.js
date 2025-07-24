
import { useQuery } from "react-query";
import { Search } from "../../services/molecules/OBPS/Search";

const usePreApprovedSearch = (filters, config, key = "OBPS_PREAPPROVESEARCH") => {
  
  return useQuery([key, filters], async () => {
   // const preApproveData = await Search.scrutinyDetails(filters, undefined, true);
    return Search.preApproveData(filters, true);
  }, config)
}

export default usePreApprovedSearch; 
