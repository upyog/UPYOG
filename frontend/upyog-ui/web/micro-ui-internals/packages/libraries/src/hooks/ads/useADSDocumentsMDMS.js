import { MdmsService } from "../../services/elements/MDMS";
import { useQuery } from "react-query";
/**
 * Custom hook to fetch document data from MDMS based on tenantId, moduleCode, and type.
 * It conditionally retrieves either required documents for a specific screen or multiple 
 * types of documents based on the provided type.
 */

const useADSDocumentsMDMS = (tenantId, moduleCode, type, config = {}) => {
  
  
  
  const useADSDocumentsRequiredScreen = () => {
    return useQuery("ADS_DOCUMENT_REQ_SCREEN", () => MdmsService.getADSDocuments(tenantId, moduleCode), config);
  };
  
  

  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsService.getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useADSDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useADSDocumentsMDMS;
