import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetClassification = (tenantId, moduleCode, type, config = {}) => {
  const useAsset = () => {
    return useQuery("A_CLASSIFICATION_TYPE", () => MdmsService.Asset_Classification(tenantId, moduleCode ,type), config);
  };
  // Return the query based on the type
  return type === "assetClassification" ? useAsset() : null;
};



export default useAssetClassification;