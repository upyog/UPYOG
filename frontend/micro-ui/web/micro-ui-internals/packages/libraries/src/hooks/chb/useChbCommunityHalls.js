import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useChbCommunityHalls = (tenantId, moduleCode, type, config = {}) => {
  const useCommunityHalls = () => {
    return useQuery("COMMUNITY_HALLS", () => MdmsService.getChbCommunityHalls(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "ChbCommunityHalls":
      return useCommunityHalls();
    default:
      return null;
  }
};
export default useChbCommunityHalls;