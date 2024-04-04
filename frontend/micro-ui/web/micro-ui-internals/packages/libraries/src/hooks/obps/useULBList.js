import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useULBList = (tenantId, moduleCode, type, config = {}) => {
  const useULBType = () => {
    return useQuery("BPA_ULB_TYPE", () => MdmsService.BPAUlb(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "Ulb":
      return useULBType();
    default:
      return null;
  }
};



export default useULBList;