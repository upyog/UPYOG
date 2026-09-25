import { mutationTemplate } from "../../common/mutationTemplate";
import { ASSETService } from "../../services/elements/ASSET";

export const useMaintenanceAPI = (tenantId, type = true) => {
  const mutationFn = (data) => {
    if (type) {
      return ASSETService.maintenance(data, tenantId);
    }
    return ASSETService.edit_maintenance(data, tenantId);
  };

  return mutationTemplate({ mutationFn });
};

export default useMaintenanceAPI;