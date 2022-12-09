import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useCRGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  const useCRGenders = () => {
    return useQuery("CR_GENDER_DETAILS", () => MdmsService.CRGenderType(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "GenderType":
      return useCRGenders();
    default:
      return null;
  }
};



export default useCRGenderMDMS;