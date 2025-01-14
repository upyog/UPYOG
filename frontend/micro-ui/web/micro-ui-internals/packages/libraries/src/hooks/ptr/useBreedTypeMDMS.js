import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useBreedTypeMDMS = (tenantId, moduleCode, type,  config = {}) => {
  const useBreed = () => {
    return useQuery("PTR_FORM_BREED_TYPE", () => MdmsServiceV2.getMasterData(tenantId, moduleCode, type, "BreedType"), config);
  };
  

  switch (type) {
    case "BreedType":
      return useBreed();
    default:
      return null;
  }
};



export default useBreedTypeMDMS;