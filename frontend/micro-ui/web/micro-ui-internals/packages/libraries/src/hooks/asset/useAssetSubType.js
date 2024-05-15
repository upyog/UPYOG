import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetSubType = (tenantId, moduleCode, type,  config = {}) => {
  const useAssetthird = () => {
    return useQuery("ASSET_SUB_PARENT_CATEGORY", () => MdmsService.AssetSubTypeParent(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "assetSubCategory":
      return useAssetthird();
    default:
      return null;
  }
};



export default useAssetSubType;