import { useQuery } from "react-query";
import { VendorService } from "../../services/elements/EmpVendor";



const useEmpvendorCreate = (args) => {
  const { tenantId, filters, config } = args;
  return useQuery(["EMP_VENDOR_CREATE", filters], () => VendorService.createVendor(tenantId, filters), config);
};

export default useEmpvendorCreate;