import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useChbCommunityHalls = (tenantId, moduleCode, type, config = {}) => {
  const useCommunityHalls = () => {
    return useQuery("COMMUNITY_HALLS", () => MdmsServiceV2.getMasterData(tenantId, moduleCode, type, "ChbCommunityHalls"), config);
  };
  

  switch (type) {
    case "CommunityHalls":
      return useCommunityHalls();
    default:
      return null;
  }
};
export default useChbCommunityHalls;