import { WSSearch } from "../../services/molecules/WS/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useWSDetailsPage = (t, tenantId, applicationNumber, serviceType, userInfo, config = {}) => {
  return queryTemplate({
    queryKey: ["APPLICATION_WS_SEARCH", "WNS_SEARCH", applicationNumber, serviceType, userInfo,config],
    queryFn: () => WSSearch.applicationDetails(t, tenantId, applicationNumber, serviceType, userInfo),
    config,
  });
};

export default useWSDetailsPage;
