import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useAssetDocumentsMDMS = (
  tenantId,
  moduleCode,
  type,
  config = {}
) => {
  if (type === "Documents") {
    return queryTemplate({
      queryKey: ["ASSET_DOCUMENTS_MDMS", tenantId],
      queryFn: () =>
        MdmsService.getAssetDocuments(tenantId, moduleCode),
      config,
    });
  }

  return queryTemplate({
    queryKey: [
      "ASSET_MDMS_MULTIPLE",
      tenantId,
      moduleCode,
      type,
    ],
    queryFn: () =>
      MdmsService.getMultipleTypes(tenantId, moduleCode, type),
    config,
  });
};

export default useAssetDocumentsMDMS;