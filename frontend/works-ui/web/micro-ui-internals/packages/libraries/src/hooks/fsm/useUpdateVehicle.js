import { mutationTemplate } from "../../common/mutationTemplate";
import { FSMService } from "../../services/elements/FSM";

const VehicleUpdateActions = async (vendorData, tenantId) => {
  try {
    return await FSMService.updateVehicle(vendorData, tenantId);
  } catch (error) {
    throw new Error(error?.response?.data?.Errors?.[0]?.message);
  }
};

const useUpdateVehicle = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) => VehicleUpdateActions(data, tenantId),
  });
};

export default useUpdateVehicle;