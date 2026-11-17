import { queryTemplate } from "../../common/queryTemplate";
import { WSService } from "../../services/elements/WS";

const useOldValue = ({ tenantId, filters, businessService }, config = {}) => {
  return queryTemplate({
    queryKey: ["WS_WATER_SEARCH", tenantId, filters, businessService],
    queryFn: async () => await WSService.search({ tenantId, filters, businessService: businessService === "WATER" ? "WS" : "SW" }),
    config,
  });
};

export default useOldValue;
