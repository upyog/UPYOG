import { useQuery } from "@tanstack/react-query";
import { MdmsService } from "../services/elements/MDMS";

const useCustomMDMS = (tenantId, moduleName, masterDetails = [], config = {}) => {
  return useQuery({
    queryKey: [tenantId, moduleName, masterDetails],
    queryFn: () => MdmsService.getMultipleTypesWithFilter(tenantId, moduleName, masterDetails),
    ...config
  });
};

export default useCustomMDMS;
