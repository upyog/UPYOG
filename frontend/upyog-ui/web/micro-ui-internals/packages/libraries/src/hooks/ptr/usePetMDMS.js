import { MdmsService } from "../../services/elements/MDMS";
import { queryTemplate } from "../../common/queryTemplate";

const usePetMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePetDocumentsRequiredScreen = () => {
    return queryTemplate({ queryKey: ["PT_DOCUMENT_REQ_SCREEN"], queryFn: () => MdmsService.getPetDocumentsRequiredScreen(tenantId, moduleCode), config });
  };

  const _default = () => {
    return queryTemplate({ queryKey: [tenantId, moduleCode, type], queryFn: () => MdmsService.getMultipleTypes(tenantId, moduleCode, type), config });
  };

  switch (type) {
    case "Documents":
      return usePetDocumentsRequiredScreen();
    default:
      return _default();
  }
};

export default usePetMDMS;
