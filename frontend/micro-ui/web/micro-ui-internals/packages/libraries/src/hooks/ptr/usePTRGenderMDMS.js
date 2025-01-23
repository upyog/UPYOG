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

const usePTRGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePTRGenders = () => {
    return useQuery("PTR_GENDER_DETAILS", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "GenderType", "PTR_GENDER_", "i18nKey"), config);
  };
  

  switch (type) {
    case "GenderType":
      return usePTRGenders();
    default:
      return null;
  }
};



export default usePTRGenderMDMS;