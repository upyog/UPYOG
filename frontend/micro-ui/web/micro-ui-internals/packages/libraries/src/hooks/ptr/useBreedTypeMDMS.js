import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useBreedTypeMDMS = (tenantId, moduleCode, type,  config = {}) => {
  const useBreed = () => {
    return useQuery("PTR_FORM_BREED_TYPE", () => MdmsService.PTRBreedType(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "BreedType":
      return useBreed();
    default:
      return null;
  }
};



export default useBreedTypeMDMS;