import { MdmsServiceV2 } from "../../services/elements/MDMSV2";
import { useQuery } from "react-query";

const useSVDoc = (tenantId, moduleCode, type, config = {}) => {
  
  const useSVDocumentsRequiredScreen = () => {
    return useQuery("SV_DOCUMENT_REQ_SCREEN", () => MdmsServiceV2.getMasterData(tenantId, moduleCode, "Documents"), config);
  };
  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsServiceV2.getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useSVDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useSVDoc;
