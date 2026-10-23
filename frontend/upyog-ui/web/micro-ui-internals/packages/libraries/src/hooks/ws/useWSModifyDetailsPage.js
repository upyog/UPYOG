import { WSSearch } from "../../services/molecules/WS/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useWSModifyDetailsPage = (t, tenantId, applicationNumber, serviceType, userInfo, config = {}) => {
  return queryTemplate({
    queryKey: ["APPLICATION_WS_SEARCH", "WNS_SEARCH", applicationNumber, serviceType, userInfo, config],
    queryFn: () => WSSearch.modifyApplicationDetails(t, tenantId, applicationNumber, serviceType, userInfo),
    config,
  });
};

export default useWSModifyDetailsPage;
