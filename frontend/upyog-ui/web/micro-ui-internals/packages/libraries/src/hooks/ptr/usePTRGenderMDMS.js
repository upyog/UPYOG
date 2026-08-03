import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const usePTRGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePTRGenders = () => {
    return queryTemplate({ queryKey: ["PTR_GENDER_DETAILS"], queryFn: () => MdmsService.PTRGenderType(tenantId, moduleCode, type), config });
  };

  switch (type) {
    case "GenderType":
      return usePTRGenders();
    default:
      return null;
  }
};

export default usePTRGenderMDMS;
