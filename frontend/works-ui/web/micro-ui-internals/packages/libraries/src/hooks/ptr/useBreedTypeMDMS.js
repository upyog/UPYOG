import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useBreed = (tenantId, moduleCode, type, config = {}) => {
  return queryTemplate({ queryKey: ["PTR_FORM_BREED_TYPE"], queryFn: () => MdmsService.PTRBreedType(tenantId, moduleCode, type), config });
};

const useBreedTypeMDMS = (tenantId, moduleCode, type, config = {}) => {
  if (type === "BreedType") {
    return useBreed(tenantId, moduleCode, type, config);
  }
  return null;
};

export default useBreedTypeMDMS;
