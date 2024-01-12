import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useTLGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  const useTLGenders = () => {
    return useQuery("TL_GENDER_DETAILS", () => MdmsServiceV2.TLGenderType(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "GenderType":
      return useTLGenders();
    default:
      return null;
  }
};



export default useTLGenderMDMS;