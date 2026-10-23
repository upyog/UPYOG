import { mutationTemplate } from "../../common/mutationTemplate";
import { VendorService } from "../../services/elements/EmpVendor";

const useVendorAdditionaldetailsAPI = (tenantId) => {

  return mutationTemplate({ mutationFn: (data) => VendorService.createVendorAdditionaldetails(data, tenantId) });
};

export default useVendorAdditionaldetailsAPI;
