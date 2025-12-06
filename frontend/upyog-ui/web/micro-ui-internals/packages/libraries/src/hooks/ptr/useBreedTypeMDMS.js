import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

<<<<<<< HEAD
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



=======
 const useBreed = (tenantId, moduleCode, type, config = {}) => {
   return useQuery("PTR_FORM_BREED_TYPE", () => MdmsService.PTRBreedType(tenantId, moduleCode ,type), config);
 };

 const useBreedTypeMDMS = (tenantId, moduleCode, type,  config = {}) => {
   if (type === "BreedType") {
     return useBreed(tenantId, moduleCode, type, config);
   }
   return null;
 };
>>>>>>> master-LTS
export default useBreedTypeMDMS;