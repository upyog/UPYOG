import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const usePurpose = (tenantId, moduleCode, type, config = {}) => {
  const usepurpose = () => {
    return useQuery("PURPOSE", () => MdmsService.getChbPurpose(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "ChbPurpose":
      return usepurpose();
    default:
      return null;
  }
};
export default usePurpose;