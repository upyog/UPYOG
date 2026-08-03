import { mutationTemplate } from "../../common/mutationTemplate";
import { ESTService } from "../../services/elements/EST";

export const useESTCreateAPI = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) => ESTService.create(data, tenantId),
  });
};

export default useESTCreateAPI;