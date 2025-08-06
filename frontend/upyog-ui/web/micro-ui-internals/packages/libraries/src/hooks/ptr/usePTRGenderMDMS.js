import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const usePTRGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePTRGenders = () => {
    return useQuery("PTR_GENDER_DETAILS", () => MdmsService.PTRGenderType(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "GenderType":
      return usePTRGenders();
    default:
      return null;
  }
};



export default usePTRGenderMDMS;