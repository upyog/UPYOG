import { queryTemplate } from "../../common/queryTemplate";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useTLGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  const useTLGenders = () => {
    return queryTemplate({ queryKey: ["TL_GENDER_DETAILS"], queryFn: () => MdmsServiceV2.TLGenderType(tenantId, moduleCode, type), config });
  };

  switch (type) {
    case "GenderType":
      return useTLGenders();
    default:
      return null;
  }
};

export default useTLGenderMDMS;
