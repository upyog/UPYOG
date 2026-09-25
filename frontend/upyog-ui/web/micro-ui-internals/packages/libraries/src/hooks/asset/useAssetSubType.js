import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetSubType = (
  tenantId,
  moduleCode,
  type,
  config = {}
) => {
  if (type !== "assetCategory") return null;

  const queryKey = [
    "ASSET_SUB_TYPE",
    tenantId,
    moduleCode,
    type,
  ];

  const queryFn = () =>
    MdmsService.AssetSubTypeParent(tenantId, moduleCode, type);

  return queryTemplate({ queryKey, queryFn, config });
};

export default useAssetSubType;