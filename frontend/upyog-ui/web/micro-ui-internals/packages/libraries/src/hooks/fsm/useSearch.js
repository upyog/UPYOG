import { queryTemplate } from "../../common/queryTemplate";
import { Search } from "../../services/molecules/FSM/Search";

const useSearch = (tenantId, filters, config = {}) => {
  const queryKey = [
    "FSM_SEARCH",
    tenantId,
    JSON.stringify(filters),
  ];

  const queryFn = () => Search.application(tenantId, filters);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useSearch;