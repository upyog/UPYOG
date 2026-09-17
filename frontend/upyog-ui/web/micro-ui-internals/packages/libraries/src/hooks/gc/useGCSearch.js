import { queryTemplate } from "../../common/queryTemplate";
import { GCServices } from "../../services/elements/GC";

const useGCSearch = ({ tenantId, filters = {}, auth, data = {} }, config = {}) => {
  return queryTemplate({
    queryKey: ["GC_SEARCH_APPLICATIONS", tenantId, filters, data],
    queryFn: () => GCServices.search({ tenantId, filters, auth, data }),
    config,
  });
};

export default useGCSearch;