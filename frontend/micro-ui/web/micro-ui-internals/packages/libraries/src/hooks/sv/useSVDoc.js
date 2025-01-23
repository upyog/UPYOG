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

const useSVDoc = (tenantId, moduleCode, type, config = {}) => {
  
  const useSVDocumentsRequiredScreen = () => {
    return useQuery("SV_DOCUMENT_REQ_SCREEN", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "Documents"), config);
  };
  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => getMDMSServiceRef().getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useSVDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useSVDoc;
