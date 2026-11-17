import { mutationTemplate } from "../../common/mutationTemplate";
import { ASSETService } from "../../services/elements/ASSET"



export const useEditUpdateAPI = (tenantId, type = true) => {
  const mutationFn = (data) => ASSETService.update(data, tenantId)

  return mutationTemplate({ mutationFn });
};

export default useEditUpdateAPI;
