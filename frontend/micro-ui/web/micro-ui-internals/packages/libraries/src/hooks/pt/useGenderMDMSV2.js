import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useGenderMDMSV2 = (tenantId, moduleCode, type, config = {}) => {
  const useGenderDetails = () => {
    return useQuery("PT_GENDER_DETAILS", () => MdmsServiceV2.getGenderType(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "GenderType":
      return useGenderDetails();
  }
};



export default useGenderMDMSV2;
