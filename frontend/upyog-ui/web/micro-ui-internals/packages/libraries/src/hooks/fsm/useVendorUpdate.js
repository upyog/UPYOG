import { mutationTemplate } from "../../common/mutationTemplate";
import { FSMService } from "../../services/elements/FSM";

const VendorUpdateActions = async (vendorData, tenantId) => {
  try {
    return await FSMService.updateVendor(vendorData, tenantId);
  } catch (error) {
    throw new Error(error?.response?.data?.Errors?.[0]?.message);
  }
};

const useVendorUpdate = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) => VendorUpdateActions(data, tenantId),
  });
};

export default useVendorUpdate;