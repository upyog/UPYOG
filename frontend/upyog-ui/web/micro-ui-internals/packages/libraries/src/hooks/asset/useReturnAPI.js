import { mutationTemplate } from "../../common/mutationTemplate";
import { ASSETService } from "../../services/elements/ASSET";

export const useReturnAPI = (tenantId, type = true) => {
  const mutationFn = (data) => ASSETService.return_asset(data, tenantId);

  return mutationTemplate({ mutationFn });
};

export default useReturnAPI;