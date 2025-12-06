/*
  Custom hook to fetch pet-related MDMS (Master Data Management System) data using react-query.

  Parameters:
  - tenantId: ID of the tenant.
  - moduleCode: Module code for fetching MDMS data.
  - type: Type of data to be fetched (e.g., Documents).
  - config: Optional configuration for react-query.

  Returns:
  - MDMS data based on the provided type.
*/
import { useQuery } from "react-query";
const usePetMDMS = (tenantId, moduleCode, type, config = {}) => {

  const usePetDocumentsRequiredScreen = () => {
    return useQuery("PT_DOCUMENT_REQ_SCREEN", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "Documents"), config);
  };
  
  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => Digit.Hooks.useSelectedMDMS().getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return usePetDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default usePetMDMS;
