import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const usePTRPetMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePTRPet = () => {
    return useQuery("PTR_FORM_PET_TYPE", () => MdmsService.PTRPetType(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "PetType":
      return usePTRPet();
    default:
      return null;
  }
};



export default usePTRPetMDMS;