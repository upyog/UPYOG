import { useQuery } from "react-query";
import { MdmsService } from "../services/elements/MDMS";

const useCustomMDMS = (tenantId, moduleName, masterDetails = [], config = {}) => {
  return useQuery([tenantId, moduleName, masterDetails], () => MdmsService.getMultipleTypesWithFilter(tenantId, moduleName, masterDetails), config);
};

export default useCustomMDMS;
