import { useQuery } from "react-query";
import { VendorService } from "../../services/elements/EmpVendor";



const useEmpvendorSearch = (args) => {
  const { tenantId, filters, config } = args;
  return useQuery(["EMP_VENDOR_SEARCH", filters], () => VendorService.vendorSearch(tenantId, filters), config);
};

export default useEmpvendorSearch;