import { mutationTemplate } from "../../common/mutationTemplate";
import { MTService } from "../../services/elements/MT";

export const useMobileToiletCreateAPI = (tenantId, type = true) => {
  if (type) {
    return mutationTemplate({ mutationFn: (data) => MTService.create(data, tenantId) });
  } else {
    return mutationTemplate({ mutationFn: (data) => MTService.update(data, tenantId) });
  }
};

export default useMobileToiletCreateAPI;
