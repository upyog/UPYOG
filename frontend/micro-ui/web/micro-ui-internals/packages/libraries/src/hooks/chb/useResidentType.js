import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useResidentType = (tenantId, moduleCode, type, config = {}) => {
  const useResident = () => {
    return useQuery("RESIDENT_TYPE", () => MdmsService.getChbResidentType(tenantId, moduleCode ,type), config);
  };
  

  switch (type) {
    case "ChbResidentType":
      return useResident();
    default:
      return null;
  }
};



export default useResidentType;