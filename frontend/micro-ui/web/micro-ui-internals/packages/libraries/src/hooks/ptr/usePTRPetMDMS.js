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

const usePTRPetMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePTRPet = () => {
    return useQuery("PTR_FORM_PET_TYPE", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "PetType", "PTR_PET_TYPE_", "i18nKey"), config);
  };
  

  switch (type) {
    case "PetType":
      return usePTRPet();
    default:
      return null;
  }
};



export default usePTRPetMDMS;