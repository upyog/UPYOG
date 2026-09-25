import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetClassification = (
  tenantId,
  moduleCode,
  type,
  config = {}
) => {
  if (type !== "assetClassification") return null;

  const queryKey = [
    "ASSET_CLASSIFICATION",
    tenantId,
    moduleCode,
    type,
  ];

  const queryFn = () =>
    MdmsService.Asset_Classification(tenantId, moduleCode, type);

  return queryTemplate({ queryKey, queryFn, config });
};

export default useAssetClassification;