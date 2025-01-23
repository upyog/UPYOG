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

const useBreedTypeMDMS = (tenantId, moduleCode, type,  config = {}) => {
  const useBreed = () => {
    return useQuery("PTR_FORM_BREED_TYPE", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "BreedType", "PTR_BREED_TYPE_", "i18nKey"), config);
  };
  

  switch (type) {
    case "BreedType":
      return useBreed();
    default:
      return null;
  }
};



export default useBreedTypeMDMS;