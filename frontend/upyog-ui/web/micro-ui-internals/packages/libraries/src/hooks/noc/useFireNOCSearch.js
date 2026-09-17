import { queryTemplate } from "../../common/queryTemplate";
import { NOCService } from "../../services/elements/NOC";

const useFireNOCSearch = (tenantId, filters, config = {}) => {
  const queryKey = [
    "FIRENOC_SEARCH",
    tenantId,
    JSON.stringify(filters),
  ];

  const queryFn = () => NOCService.search(tenantId, filters);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useFireNOCSearch;
