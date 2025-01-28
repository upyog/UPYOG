import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useSpecialCategory = (tenantId, moduleCode, type, config = {}) => {
  const useSpecial = () => {
    return useQuery("SPECIAL_CATEGORY", () => MdmsService.getChbSpecialCategory(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "ChbSpecialCategory":
      return useSpecial();
    default:
      return null;
  }
};

export default useSpecialCategory;