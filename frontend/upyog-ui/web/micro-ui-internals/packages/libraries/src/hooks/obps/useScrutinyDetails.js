import { queryTemplate } from "../../common/queryTemplate";
import { Search } from "../../services/molecules/OBPS/Search";

const useScrutinyDetails = (
  tenantId,
  filters,
  config = {},
  key = "OBPS_SCRUTINY_DETAILS"
) => {
  return queryTemplate({
    queryKey: [key, tenantId, JSON.stringify(filters)],
    queryFn: () =>
      Search.scrutinyDetails(tenantId, filters, undefined, true),
    config,
  });
};

export default useScrutinyDetails;