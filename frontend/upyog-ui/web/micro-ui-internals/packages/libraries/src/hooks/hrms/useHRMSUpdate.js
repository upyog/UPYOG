import { mutationTemplate } from "../../common/mutationTemplate";
import HrmsService from "../../services/elements/HRMS";

export const useHRMSUpdate = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) => HrmsService.update(data, tenantId),
  });
};

export default useHRMSUpdate;