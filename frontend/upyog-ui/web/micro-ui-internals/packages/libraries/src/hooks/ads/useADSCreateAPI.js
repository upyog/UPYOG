import { mutationTemplate } from "../../common/mutationTemplate";
import { ADSServices } from "../../services/elements/ADS";

export const useADSCreateAPI = (tenantId, type = true) => {
  const mutationFn = (data) =>
    type
      ? ADSServices.create(data, tenantId)
      : ADSServices.update(data, tenantId);

  return mutationTemplate({ mutationFn });
};

export default useADSCreateAPI;