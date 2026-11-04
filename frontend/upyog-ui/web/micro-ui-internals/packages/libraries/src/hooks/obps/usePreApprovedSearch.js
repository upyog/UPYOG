import { queryTemplate } from "../../common/queryTemplate";
import { Search } from "../../services/molecules/OBPS/Search";

const usePreApprovedSearch = (
  filters,
  config = {},
  key = "OBPS_PREAPPROVE_SEARCH"
) => {
  return queryTemplate({
    queryKey: [key, JSON.stringify(filters)],
    queryFn: () => Search.preApproveData(filters, true),
    config,
  });
};

export default usePreApprovedSearch;