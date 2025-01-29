import { useQuery } from "react-query";

const useChbCommunityHalls = (tenantId, moduleCode, type, config = {}) => {
  const useCommunityHalls = () => {
    return useQuery("COMMUNITY_HALLS", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "CommunityHalls", "CHB_COMMUNITY_HALLS_", "i18nKey"), config);
  };
  

  switch (type) {
    case "CommunityHalls":
      return useCommunityHalls();
    default:
      return null;
  }
};
export default useChbCommunityHalls;