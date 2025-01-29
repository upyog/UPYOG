import { useQuery } from "react-query";

const usePTRPetMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePTRPet = () => {
    return useQuery("PTR_FORM_PET_TYPE", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "PetType", "PTR_PET_TYPE_", "i18nKey"), config);
  };
  

  switch (type) {
    case "PetType":
      return usePTRPet();
    default:
      return null;
  }
};



export default usePTRPetMDMS;