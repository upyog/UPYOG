import { MdmsService } from "../../services/elements/MDMS";
import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const usePetMDMS = (tenantId, moduleCode, type, config = {}) => {
  
  
  
  const usePetDocumentsRequiredScreen = () => {
    return useQuery("PT_DOCUMENT_REQ_SCREEN", () => MdmsServiceV2.getMasterData(tenantId, moduleCode, "Documents"), config);
  };
  
  

  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsServiceV2.getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return usePetDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default usePetMDMS;
