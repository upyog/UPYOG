import { useQuery } from "react-query";

/**
 * Custom hook to fetch document data from MDMS based on tenantId, moduleCode, and type.
 * It conditionally retrieves either required documents for a specific screen or multiple 
 * types of documents based on the provided type.
 */
const useADSDocumentsMDMS = (tenantId, moduleCode, type, config = {}) => {
  
  const useADSDocumentsRequiredScreen = () => {
    return useQuery("ADS_DOCUMENT_REQ_SCREEN", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "Documents"), config);
  };
  
  

  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => Digit.Hooks.useSelectedMDMS().getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useADSDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useADSDocumentsMDMS;
