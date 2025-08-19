import { MdmsService } from "../../services/elements/MDMS";
import { useQuery } from "react-query";
/**
 * Custom hook to fetch document data from MDMS based on tenantId, moduleCode, and type.
 * It conditionally retrieves either required documents for a specific screen or multiple 
 * types of documents based on the provided type.
 */

const useADSDocumentsMDMS = (tenantId, moduleCode, type, config = {}) => {
  
  const useADSDocumentsRequiredScreen = () => {
    return useQuery("ADS_DOCUMENT_REQ_SCREEN", ()  => Digit.Hooks.useSelectedMDMS(moduleCode).getMasterData(tenantId, moduleCode, type), config);
  };
  
  return useADSDocumentsRequiredScreen();
};

export default useADSDocumentsMDMS;