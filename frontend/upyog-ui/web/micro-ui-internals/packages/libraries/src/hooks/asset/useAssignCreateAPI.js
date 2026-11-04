import { mutationTemplate } from "../../common/mutationTemplate";
import { ASSETService } from"../../services/elements/ASSET"


export const useAssignCreateAPI = (tenantId, type = true) => {
  const mutationFn = (data) => ASSETService.assign(data, tenantId);

  return mutationTemplate({ mutationFn });
};

export default useAssignCreateAPI;
