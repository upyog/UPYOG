import { mutationTemplate } from "../../common/mutationTemplate";
import { ASSETService } from "../../services/elements/ASSET";

export const useAssetCreateAPI = (tenantId, type = true) => {
  const mutationFn = (data) =>
    type
      ? ASSETService.create(data, tenantId)
      : ASSETService.update(data, tenantId);

  return mutationTemplate({ mutationFn });
};

export default useAssetCreateAPI;