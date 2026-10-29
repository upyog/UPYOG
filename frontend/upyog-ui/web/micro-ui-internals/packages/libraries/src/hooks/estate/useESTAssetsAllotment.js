import { mutationTemplate } from "../../common/mutationTemplate";
import { ESTService } from "../../services/elements/EST";

export const useESTAssetsAllotment = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) => ESTService.allotmentcreate(data, tenantId),
  });
};

export default useESTAssetsAllotment;