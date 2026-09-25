import { PTService } from "../../services/elements/PT";
import { mutationTemplate } from "../../common/mutationTemplate";

const usePropertyAssessment = (tenantId, config = {}) => {
  return mutationTemplate({ mutationFn: (data) => PTService.assessmentCreate(data, tenantId) });
};

export default usePropertyAssessment;
