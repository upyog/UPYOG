import { mutationTemplate } from "../../common/mutationTemplate";
import { FSMService } from "../../services/elements/FSM";

const DriverCreateActions = async (vendorData, tenantId) => {
  try {
    return await FSMService.createDriver(vendorData, tenantId);
  } catch (error) {
    throw new Error(error?.response?.data?.Errors?.[0]?.message);
  }
};

const useDriverCreate = (tenantId) => {
  const mutationFn = (vendorData) =>
    DriverCreateActions(vendorData, tenantId);

  return mutationTemplate({
    mutationFn,
  });
};

export default useDriverCreate;