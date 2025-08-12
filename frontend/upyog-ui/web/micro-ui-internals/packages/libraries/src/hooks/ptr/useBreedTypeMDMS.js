import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

 const useBreed = (tenantId, moduleCode, type, config = {}) => {
   return useQuery("PTR_FORM_BREED_TYPE", () => MdmsService.PTRBreedType(tenantId, moduleCode ,type), config);
 };

 const useBreedTypeMDMS = (tenantId, moduleCode, type,  config = {}) => {
   if (type === "BreedType") {
     return useBreed(tenantId, moduleCode, type, config);
   }
   return null;
 };
export default useBreedTypeMDMS;