import { MdmsService } from "../../services/elements/MDMS";
import { useQuery } from "react-query";

const useChbDocumentsMDMS = (tenantId, moduleCode, type, config = {}) => {
  
  
  
  const useChbDocumentsRequiredScreen = () => {
    return useQuery("CHB_DOCUMENT_REQ_SCREEN", () => MdmsService.getChbDocuments(tenantId, moduleCode), config);
  };
  
  

  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsService.getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useChbDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useChbDocumentsMDMS;
