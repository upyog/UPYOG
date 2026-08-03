import { queryTemplate } from "../../common/queryTemplate";
import { Search } from "../../services/molecules/OBPS/Search";

const useBPADetails = (tenantId, filters, config = {}) => {
  return queryTemplate({
    queryKey: ["OBPS_BPA_DETAILS", tenantId, JSON.stringify(filters)],
    queryFn: () => Search.BPADetails(tenantId, filters),
    config,
  });
};

export default useBPADetails;