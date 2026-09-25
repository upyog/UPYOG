import { queryTemplate } from "../../common/queryTemplate";
import { VendorService } from "../../services/elements/EmpVendor";

const useEmpvendorCreate = (args) => {
  const { tenantId, filters, config } = args;
  return queryTemplate({ queryKey: ["EMP_VENDOR_CREATE", filters], queryFn: () => VendorService.createVendor(tenantId, filters), config });
};

export default useEmpvendorCreate;
