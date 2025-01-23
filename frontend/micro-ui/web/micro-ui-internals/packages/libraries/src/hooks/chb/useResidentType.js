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

const useResidentType = (tenantId, moduleCode, type, config = {}) => {
  const useResident = () => {
    return useQuery("RESIDENT_TYPE", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "ResidentType", "CHB_RESIDENT_TYPE_", "i18nKey"), config);
  };
  

  switch (type) {
    case "ChbResidentType":
      return useResident();
    default:
      return null;
  }
};



export default useResidentType;