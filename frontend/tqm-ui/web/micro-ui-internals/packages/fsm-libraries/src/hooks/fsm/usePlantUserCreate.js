import { useMutation } from "react-query";
import { FSMService } from "../../services/elements/FSM";

const usePlantUserCreate = (tenantId) => {
  return useMutation((plantData) => PlantCreateActions(plantData, tenantId));
};

const PlantCreateActions = async (plantData, tenantId) => {
  try {
    const response = await FSMService.createPlantUser(plantData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default usePlantUserCreate;
