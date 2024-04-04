import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useDistricts = (tenantId, moduleCode, type, config = {}) => {
  const usedistricttype = () => {
    return useQuery("BPA_DISTRICTS", () => MdmsService.BPADistrict(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "Districts":
      return usedistricttype();
    default:
      return null;
  }
};



export default useDistricts;