import { mutationTemplate } from "../../common/mutationTemplate";
import { FSMService } from "../../services/elements/FSM";

const VendorCreateActions = async (vendorData, tenantId) => {
  try {
    return await FSMService.createVendor(vendorData, tenantId);
  } catch (error) {
    throw new Error(error?.response?.data?.Errors?.[0]?.message);
  }
};

const useVendorCreate = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) => VendorCreateActions(data, tenantId),
  });
};

export default useVendorCreate;