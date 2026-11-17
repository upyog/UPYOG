import { mutationTemplate } from "../../common/mutationTemplate";
import { GCServices } from "../../services/elements/GC";

export const useGCCreateAPI = (tenantId, type = true) => {
  const mutationFn = (data) =>
    type
      ? GCServices.create(data, tenantId)
      : GCServices.update(data, tenantId);

  return mutationTemplate({ mutationFn });
};

export default useGCCreateAPI;