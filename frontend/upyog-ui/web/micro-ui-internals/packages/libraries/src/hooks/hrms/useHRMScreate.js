import { mutationTemplate } from "../../common/mutationTemplate";
import HrmsService from "../../services/elements/HRMS";

export const useHRMSCreate = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) => HrmsService.create(data, tenantId),
  });
};

export default useHRMSCreate;