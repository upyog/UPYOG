import { mutationTemplate } from "../../common/mutationTemplate";
import { ADSServices } from "../../services/elements/ADS";

const useADSDemandEstimation = (tenantId) => {
  const mutationFn = (data) =>
    ADSServices.estimateCreate(data, tenantId);

  return mutationTemplate({ mutationFn });
};

export default useADSDemandEstimation;