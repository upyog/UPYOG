import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useHallCode = (tenantId, moduleCode, type, config = {}) => {
  const usehallCode = () => {
    return useQuery("HALL_CODE", () => MdmsService.getChbHallCode(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "ChbHallCode":
      return usehallCode();
    default:
      return null;
  }
};



export default useHallCode;