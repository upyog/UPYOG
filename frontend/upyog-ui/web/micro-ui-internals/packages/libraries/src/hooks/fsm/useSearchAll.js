import { queryTemplate } from "../../common/queryTemplate";
import { Search } from "../../services/molecules/FSM/Search";

const useSearchAll = (tenantId, filters, queryFn, config = {}) => {
  const queryKey = [
    "FSM_SEARCH_ALL",
    tenantId,
    JSON.stringify(filters),
  ];

  const finalQueryFn =
    typeof queryFn === "function"
      ? queryFn
      : () => Search.all(tenantId, filters);

  const select = (data) => ({
    data: { table: data.fsm ? data.fsm : [data] },
    totalCount: data.totalCount || 1,
  });

  return queryTemplate({
    queryKey,
    queryFn: finalQueryFn,
    select,
    config,
  });
};

export default useSearchAll;