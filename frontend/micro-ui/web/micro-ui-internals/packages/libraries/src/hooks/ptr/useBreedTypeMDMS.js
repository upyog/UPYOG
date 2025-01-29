import { useQuery } from "react-query";

const useBreedTypeMDMS = (tenantId, moduleCode, type,  config = {}) => {
  const useBreed = () => {
    return useQuery("PTR_FORM_BREED_TYPE", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "BreedType", "PTR_BREED_TYPE_", "i18nKey"), config);
  };
  

  switch (type) {
    case "BreedType":
      return useBreed();
    default:
      return null;
  }
};



export default useBreedTypeMDMS;