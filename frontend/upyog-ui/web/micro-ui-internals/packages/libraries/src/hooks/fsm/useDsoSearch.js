import { queryTemplate } from "../../common/queryTemplate";
import DsoDetails from "../../services/molecules/FSM/DsoDetails";

const useDsoSearch = (tenantId, filters, config = {}) => {
  const queryKey = [
    "FSM_DSO_SEARCH",
    tenantId,
    JSON.stringify(filters),
  ];

  const queryFn = () => DsoDetails(tenantId, filters);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useDsoSearch;