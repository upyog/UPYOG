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

const useSpecialCategory = (tenantId, moduleCode, type, config = {}) => {
  const useSpecial = () => {
    return useQuery("SPECIAL_CATEGORY", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "SpecialCategory", "CHB_SPECIAL_CATEGORY_", "i18nKey"), config);
  };
  

  switch (type) {
    case "ChbSpecialCategory":
      return useSpecial();
    default:
      return null;
  }
};

export default useSpecialCategory;