import { mutationTemplate } from "../../common/mutationTemplate";
import { WTService } from "../../services/elements/WT";

export const useTankerCreateAPI = (tenantId, type = true) => {
  if (type) {
    return mutationTemplate({ mutationFn: (data) => WTService.create(data, tenantId) });
  } else {
    return mutationTemplate({ mutationFn: (data) => WTService.update(data, tenantId) });
  }
};

export default useTankerCreateAPI;
