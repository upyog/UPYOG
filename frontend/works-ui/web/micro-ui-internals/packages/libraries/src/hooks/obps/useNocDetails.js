import { queryTemplate } from "../../common/queryTemplate";
import { Search } from "../../services/molecules/OBPS/Search";

const useNocDetails = (tenantId, filters, config = {}) => {
  return queryTemplate({
    queryKey: [
      "OBPS_NOC_DETAILS",
      tenantId,
      JSON.stringify(filters),
    ],
    queryFn: () => Search.NOCDetails(tenantId, filters),
    config,
  });
};

export default useNocDetails;