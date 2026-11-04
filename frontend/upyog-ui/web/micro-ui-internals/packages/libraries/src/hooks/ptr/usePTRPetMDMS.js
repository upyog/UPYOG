import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const usePTRPetMDMS = (tenantId, moduleCode, type, config = {}) => {
  return queryTemplate({ queryKey: ["PTR_FORM_PET_TYPE"], queryFn: () => MdmsService.PTRPetType(tenantId, moduleCode, type), config });
};

export default usePTRPetMDMS;
