import { PTService } from "../../services/elements/PT";
import { mutationTemplate } from "../../common/mutationTemplate";

const usePtCalculationEstimate = (tenantId, config = {}) => {
  return mutationTemplate({ mutationFn: (data) => PTService.ptCalculationEstimate(data, tenantId) });
};

export default usePtCalculationEstimate;
