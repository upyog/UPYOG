import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const mdmsV2Enabled = true;
let mdmsRef = null;

const getMDMSServiceRef = () => {
        if (mdmsV2Enabled) {
            mdmsRef = MdmsServiceV2;
        } else {
            mdmsRef = MdmsService;
        }
        return mdmsRef;
}

/**
 * Custom hook to fetch document data from MDMS based on tenantId, moduleCode, and type.
 * It conditionally retrieves either required documents for a specific screen or multiple 
 * types of documents based on the provided type.
 */
const useADSDocumentsMDMS = (tenantId, moduleCode, type, config = {}) => {
  
  const useADSDocumentsRequiredScreen = () => {
    return useQuery("ADS_DOCUMENT_REQ_SCREEN", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "Documents"), config);
  };
  
  

  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => getMDMSServiceRef().getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useADSDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useADSDocumentsMDMS;
