import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useNDCDoc = (tenantId, moduleCode, type, config = {}) => {
  const isDocument = type === "Documents";

  return queryTemplate({
    queryKey: isDocument
      ? ["NDC_DOCUMENT_REQ_SCREEN", tenantId, moduleCode]
      : [tenantId, moduleCode, type],

    queryFn: () =>
      isDocument
        ? MdmsService.getNDCDocuments(tenantId, moduleCode)
        : MdmsService.getMultipleTypes(tenantId, moduleCode, type),

    config,
  });
};

export default useNDCDoc;