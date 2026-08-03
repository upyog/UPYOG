import { WSSearch } from "../../services/molecules/WS/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useDisConnectionDetails = (t, tenantId, applicationNumber, serviceType, config = {}) => {
  return queryTemplate({
    queryKey: ["APPLICATION_WS_SEARCH", "WNS_SEARCH", applicationNumber, serviceType,config],
    queryFn: () => WSSearch.disConnectionDetails(t, tenantId, applicationNumber, serviceType),
    config,
  });
};

export default useDisConnectionDetails;
