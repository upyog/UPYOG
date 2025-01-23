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

const useHallCode = (tenantId, moduleCode, type, config = {}) => {
  const usehallCode = () => {
    return useQuery("HALL_CODE", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "HallCode", "CHB_HALL_CODE_", "i18nKey"), config);
  };
  
  switch (type) {
    case "HallCode":
      return usehallCode();
    default:
      return null;
  }
};



export default useHallCode;