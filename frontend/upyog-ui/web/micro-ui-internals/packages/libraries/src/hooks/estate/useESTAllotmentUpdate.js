import { mutationTemplate } from "../../common/mutationTemplate";
import { ESTService } from "../../services/elements/EST";

export const useESTAllotmentUpdate = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) => ESTService.allotmentUpdate(data, tenantId),
  });
};

export default useESTAllotmentUpdate;
