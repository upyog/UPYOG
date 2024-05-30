import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetparentSubType = (tenantId, moduleCode, type, config = {}) => {
  const useAssetparentsubsub = () => {
    return useQuery("AST_PARENT_SUB_TYPE", () => MdmsService.AST_PARENT(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "assetSubCategory":
      return useAssetparentsubsub();
    default:
      return null;
  }
};



export default useAssetparentSubType;