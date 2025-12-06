import { useMutation } from "react-query";
import { FSMService } from "../../services/elements/FSM";

const usePlantUserUpdate = (tenantId) => {
  return useMutation((plantData) => PlantUpdateActions(plantData, tenantId));
};

const PlantUpdateActions = async (plantData, tenantId) => {
  try {
    const response = await FSMService.updatePlantUser(plantData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default usePlantUserUpdate;
