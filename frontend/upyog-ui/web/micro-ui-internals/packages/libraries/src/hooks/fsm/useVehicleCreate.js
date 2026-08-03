import { mutationTemplate } from "../../common/mutationTemplate";
import { FSMService } from "../../services/elements/FSM";

const VehicleCreateActions = async (vendorData, tenantId) => {
  try {
    return await FSMService.createVehicle(vendorData, tenantId);
  } catch (error) {
    throw new Error(error?.response?.data?.Errors?.[0]?.message);
  }
};

const useVehicleCreate = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) => VehicleCreateActions(data, tenantId),
  });
};

export default useVehicleCreate;