import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const usePTRPetMDMS = (tenantId, moduleCode, type, config = {}) => {
  return useQuery("PTR_FORM_PET_TYPE", () => MdmsService.PTRPetType(tenantId, moduleCode ,type), config);
};
export default usePTRPetMDMS;