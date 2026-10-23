import { mutationTemplate } from "../../common/mutationTemplate";
import { OBPSService } from "../../services/elements/OBPS";

const updateNOCAPI = (data, tenantId) =>
  OBPSService.updateNOC(data, tenantId);

const updateAPI = (data, tenantId) =>
  OBPSService.update(data, tenantId);

const useObpsAPI = (tenantId, type = false) => {
  return mutationTemplate({
    mutationFn: (data) =>
      type
        ? updateNOCAPI(data, tenantId)
        : updateAPI(data, tenantId),
  });
};

export default useObpsAPI;