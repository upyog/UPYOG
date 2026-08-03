import { mutationTemplate } from "../../common/mutationTemplate";
import { FSMService } from "../../services/elements/FSM";

const useVehicleTripCreate = () => {
  return mutationTemplate({
    mutationFn: (details) => FSMService.vehicleTripCreate(details),
  });
};

export default useVehicleTripCreate;