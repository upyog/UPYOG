import { queryTemplate } from "../../common/queryTemplate";
import { VendorService } from "../../services/elements/EmpVendor";

const useEmpvendorSearch = (args) => {
  const { tenantId, filters, config } = args;
  return queryTemplate({ queryKey: ["EMP_VENDOR_SEARCH", filters], queryFn: () => VendorService.vendorSearch(tenantId, filters), config });
};

export default useEmpvendorSearch;
