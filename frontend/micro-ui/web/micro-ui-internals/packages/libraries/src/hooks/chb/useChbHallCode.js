import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useHallCode = (tenantId, moduleCode, type, config = {}) => {
  const usehallCode = () => {
    return useQuery("HALL_CODE", () => MdmsServiceV2.getMasterData(tenantId, moduleCode, type, "ChbHallCode"), config);
  };
  
  switch (type) {
    case "HallCode":
      return usehallCode();
    default:
      return null;
  }
};



export default useHallCode;