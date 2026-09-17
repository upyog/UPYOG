import { mutationTemplate } from "../../common/mutationTemplate";
import { NOCService } from "../../services/elements/NOC";

export const useFireNOCAPI = (tenantId, isCreate = true) => {
  if (isCreate) {
    return mutationTemplate({ mutationFn: (data) => NOCService.create(data, tenantId) });
  } else {
    return mutationTemplate({ mutationFn: (data) => NOCService.update(data, tenantId) });
  }
};

export default useFireNOCAPI;
