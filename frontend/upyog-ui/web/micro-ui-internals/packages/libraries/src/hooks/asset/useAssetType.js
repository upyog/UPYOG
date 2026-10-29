import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetType = (
  tenantId,
  moduleCode,
  type,
  config = {}
) => {
  if (type !== "assetParentCategory") return null;

  const queryKey = [
    "ASSET_PARENT_TYPE",
    tenantId,
    moduleCode,
    type,
  ];

  const queryFn = () =>
    MdmsService.AssetTypeParent(tenantId, moduleCode, type);

  return queryTemplate({ queryKey, queryFn, config });
};

export default useAssetType;