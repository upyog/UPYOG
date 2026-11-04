import { mutationTemplate } from "../../common/mutationTemplate";
import { FSMService } from "../../services/elements/FSM";

const DriverUpdateActions = async (vendorData, tenantId) => {
  try {
    return await FSMService.updateDriver(vendorData, tenantId);
  } catch (error) {
    throw new Error(error?.response?.data?.Errors?.[0]?.message);
  }
};

const useDriverUpdate = (tenantId) => {
  const mutationFn = (vendorData) =>
    DriverUpdateActions(vendorData, tenantId);

  return mutationTemplate({
    mutationFn,
  });
};

export default useDriverUpdate;