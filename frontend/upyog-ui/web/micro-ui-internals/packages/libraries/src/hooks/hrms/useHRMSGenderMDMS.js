import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useHRMSGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  switch (type) {
    case "GenderType":
      return queryTemplate({
        queryKey: ["HRMS_GENDER", tenantId],
        queryFn: () =>
          MdmsService.HRGenderType(tenantId, moduleCode, type),
        config,
      });

    default:
      return null;
  }
};

export default useHRMSGenderMDMS;