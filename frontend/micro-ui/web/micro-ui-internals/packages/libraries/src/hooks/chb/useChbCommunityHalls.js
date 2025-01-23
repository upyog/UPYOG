import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const mdmsV2Enabled = true;
let mdmsRef = null;

const getMDMSServiceRef = () => {
        if (mdmsV2Enabled) {
            mdmsRef = MdmsServiceV2;
        } else {
            mdmsRef = MdmsService;
        }
        return mdmsRef;
}

const useChbCommunityHalls = (tenantId, moduleCode, type, config = {}) => {
  const useCommunityHalls = () => {
    return useQuery("COMMUNITY_HALLS", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "CommunityHalls", "CHB_COMMUNITY_HALLS_", "i18nKey"), config);
  };
  

  switch (type) {
    case "CommunityHalls":
      return useCommunityHalls();
    default:
      return null;
  }
};
export default useChbCommunityHalls;