import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useUlbType = (tenantId, moduleCode, type, config = {}) => {
  const useULB = () => {
    return useQuery("BPA_ULB", () => MdmsService.BPAUlbType(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "UlbType":
      return useULB();
    default:
      return null;
  }
};



export default useUlbType;