import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetdetail = (
  tenantId,
  moduleCode,
  type,
  config = {}
) => {
  if (type !== "assetCommonDetail") return null;

  const queryKey = [
    "ASSET_COMMON_DETAIL",
    tenantId,
    moduleCode,
    type,
  ];

  const queryFn = () =>
    MdmsService.Assetcommondetail(tenantId, moduleCode, type);

  return queryTemplate({ queryKey, queryFn, config });
};

export default useAssetdetail;