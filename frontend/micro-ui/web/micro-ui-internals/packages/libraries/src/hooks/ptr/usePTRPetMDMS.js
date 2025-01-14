import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const usePTRPetMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePTRPet = () => {
    return useQuery("PTR_FORM_PET_TYPE", () => MdmsServiceV2.getMasterData(tenantId, moduleCode, type, "PetType"), config);
  };
  

  switch (type) {
    case "PetType":
      return usePTRPet();
    default:
      return null;
  }
};



export default usePTRPetMDMS;