import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const usePTRGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePTRGenders = () => {
    return useQuery("PTR_GENDER_DETAILS", () => MdmsServiceV2.getMasterData(tenantId, moduleCode, type, "GenderType"), config);
  };
  

  switch (type) {
    case "GenderType":
      return usePTRGenders();
    default:
      return null;
  }
};



export default usePTRGenderMDMS;