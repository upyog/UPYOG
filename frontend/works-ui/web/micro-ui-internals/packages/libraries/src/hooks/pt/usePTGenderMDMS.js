import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const usePTGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePTGenders = () => {
    return queryTemplate({ queryKey: ["PT_FORM_GENDER_DETAILS"], queryFn: () => MdmsServiceV2.PTGenderType(tenantId, moduleCode, type), config });
  };

  switch (type) {
    case "GenderType":
      return usePTGenders();
    default:
      return null;
  }
};



export default usePTGenderMDMS;