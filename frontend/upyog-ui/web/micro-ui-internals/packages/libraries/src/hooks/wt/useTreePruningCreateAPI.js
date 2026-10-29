import { mutationTemplate } from "../../common/mutationTemplate";
import { TPService } from "../../services/elements/TP";

export const useTreePruningCreateAPI = (tenantId, type = true) => {
  if (type) {
    return mutationTemplate({ mutationFn: (data) => TPService.create(data, tenantId) });
  } else {
    return mutationTemplate({ mutationFn: (data) => TPService.update(data, tenantId) });
  }
};

export default useTreePruningCreateAPI;
