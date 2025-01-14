import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";
/**
 * Custom hook to fetch document data from MDMS based on tenantId, moduleCode, and type.
 * It conditionally retrieves either required documents for a specific screen or multiple 
 * types of documents based on the provided type.
 */

const useADSDocumentsMDMS = (tenantId, moduleCode, type, config = {}) => {
  
  const useADSDocumentsRequiredScreen = () => {
    return useQuery("ADS_DOCUMENT_REQ_SCREEN", () => MdmsServiceV2.getMasterData(tenantId, moduleCode, "Documents"), config);
  };
  
  

  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsServiceV2.getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useADSDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useADSDocumentsMDMS;
