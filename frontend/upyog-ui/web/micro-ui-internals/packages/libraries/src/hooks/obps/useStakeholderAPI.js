import { mutationTemplate } from "../../common/mutationTemplate";
import { OBPSService } from "../../services/elements/OBPS";

const useStakeholderAPI = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) =>
      OBPSService.BPAREGupdate(data, tenantId),
  });
};

export default useStakeholderAPI;