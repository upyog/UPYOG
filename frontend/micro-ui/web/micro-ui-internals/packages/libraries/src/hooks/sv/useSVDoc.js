import { MdmsService } from "../../services/elements/MDMS";
import { useQuery } from "react-query";

const useSVDoc = (tenantId, moduleCode, type, config = {}) => {
  
  const useSVDocumentsRequiredScreen = () => {
    return useQuery("SV_DOCUMENT_REQ_SCREEN", () => MdmsService.getSVDocuments(tenantId, moduleCode), config);
  };
  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsService.getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useSVDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useSVDoc;
