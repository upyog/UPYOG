import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetdetail = (tenantId, moduleCode, type, config = {}) => {
  const useAssetfour = () => {
    return useQuery("ASSET_COMMON", () => MdmsService.Assetcommondetail(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "assetCommonDetail":
      return useAssetfour();
    default:
      return null;
  }
};



export default useAssetdetail;