import { WSSearch } from "../../services/molecules/WS/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useWSApplicationDetailsBillAmendment = (t, tenantId, applicationNumber, serviceType, config = {}) => {
  return queryTemplate({
    queryKey: ["APPLICATION_WS_SEARCH", "WNS_SEARCH", tenantId, applicationNumber, serviceType],
    queryFn: () => WSSearch.applicationDetailsBillAmendment(t, tenantId, applicationNumber, serviceType),
    config,
  });
};

export default useWSApplicationDetailsBillAmendment;
