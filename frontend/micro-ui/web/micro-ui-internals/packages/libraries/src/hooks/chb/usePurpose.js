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

const usePurpose = (tenantId, moduleCode, type, config = {}) => {
  const usepurpose = () => {
    return useQuery("PURPOSE", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "Purpose", "CHB_PURPOSE_", "i18nKey"), config);
  };
  

  switch (type) {
    case "ChbPurpose":
      return usepurpose();
    default:
      return null;
  }
};
export default usePurpose;