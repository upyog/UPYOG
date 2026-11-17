import { queryTemplate } from "../../common/queryTemplate";
import { NOCSearch } from "../../services/molecules/NOC/Search";

const useNOCSearchApplication = (tenantId, filters, config = {}) => {
  const queryKey = [
    "NOC_SEARCH",
    tenantId,
    JSON.stringify(filters),
  ];

  const queryFn = () =>
    NOCSearch.all(tenantId, filters);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useNOCSearchApplication;