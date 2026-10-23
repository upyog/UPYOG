import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetparentSubType = (
  tenantId,
  moduleCode,
  type,
  config = {}
) => {
  if (type !== "assetSubCategory") return null;

  const queryKey = [
    "ASSET_PARENT_SUB_TYPE",
    tenantId,
    moduleCode,
    type,
  ];

  const queryFn = () =>
    MdmsService.AST_PARENT(tenantId, moduleCode, type);

  return queryTemplate({ queryKey, queryFn, config });
};

export default useAssetparentSubType;