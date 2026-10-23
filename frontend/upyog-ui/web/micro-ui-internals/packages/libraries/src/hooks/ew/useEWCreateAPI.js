import { mutationTemplate } from "../../common/mutationTemplate";
import { EwService } from "../../services/elements/EW";

export const useEwCreateAPI = (tenantId, type = true) => {
  const mutationFn = (data) =>
    type
      ? EwService.create(data, tenantId)
      : EwService.update(data, tenantId);

  return mutationTemplate({
    mutationFn,
  });
};

export default useEwCreateAPI;