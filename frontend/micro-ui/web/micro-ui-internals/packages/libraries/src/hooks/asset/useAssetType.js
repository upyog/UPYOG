import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetType = (tenantId, moduleCode, type,  config = {}) => {
  const useAssetsecond = () => {
    return useQuery("ASSET_PARENT_CATEGORY", () => MdmsService.AssetTypeParent(tenantId, moduleCode ,type), config);
  };
  // Check if the type is "assetParentCategory" and return the query
  return type === "assetParentCategory" ? useAssetsecond() : null;  
};



export default useAssetType;