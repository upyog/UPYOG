import { queryTemplate } from "../../common/queryTemplate";
import { FSMService } from "../../services/elements/FSM";

const useVendorSearch = ({ tenantId, filters, config = {} }) => {
  const queryKey = [
    "FSM_VENDOR_SEARCH",
    tenantId,
    JSON.stringify(filters),
  ];

  return queryTemplate({
    queryKey,
    queryFn: () => FSMService.vendorSearch(tenantId, filters),
    config,
  });
};

export default useVendorSearch;