import { mutationTemplate } from "../../common/mutationTemplate";
import { PTRService } from "../../services/elements/PTR";

export const usePTRCreateAPI = (tenantId, type = true) => {
  if (type) {
    return mutationTemplate({ mutationFn: (data) => PTRService.create(data, tenantId) });
  } else {
    return mutationTemplate({ mutationFn: (data) => PTRService.update(data, tenantId) });
  }
};

export default usePTRCreateAPI;
