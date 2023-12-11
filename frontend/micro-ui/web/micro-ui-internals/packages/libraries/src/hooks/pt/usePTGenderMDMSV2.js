import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const usePTGenderMDMSV2 = (tenantId, moduleCode, type, config = {}) => {
  const usePTGenders = () => {
    return useQuery("PT_FORM_GENDER_DETAILS", () => MdmsServiceV2.PTGenderType(tenantId, moduleCode, type), config);
  };

  switch (type) {
    case "GenderType":
      return usePTGenders();
    default:
      return null;
  }
};

export default usePTGenderMDMSV2;