import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  const useGenderDetails = () => {
    return queryTemplate({ queryKey: ["PT_GENDER_DETAILS"], queryFn: () => MdmsService.getGenderType(tenantId, moduleCode ,type), config });
  };
  

  switch (type) {
    case "GenderType":
      return useGenderDetails();
  }
};



export default useGenderMDMS;
