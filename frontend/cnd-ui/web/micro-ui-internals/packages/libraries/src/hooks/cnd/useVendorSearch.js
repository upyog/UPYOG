import { queryTemplate } from "../../common/queryTemplate";
import { CNDService } from "../../services/elements/CND";

const useVendorSearch = (args) => {
  const { tenantId, filters, config } = args;
  return queryTemplate({
    queryKey: ["VENDOR_SEARCH", JSON.stringify(filters)],
    queryFn: () => CNDService.vendorSearch(tenantId, filters),
    config
  });
};

export default useVendorSearch;
