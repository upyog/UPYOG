import { mutationTemplate } from "../../common/mutationTemplate";
import { FSMService } from "../../services/elements/FSM";

const useVehicleUpdate = () => {
  return mutationTemplate({
    mutationFn: (details) => FSMService.vehicleUpdate(details),
  });
};

export default useVehicleUpdate;