import { useQuery } from "@tanstack/react-query";
import { MdmsServiceV2 } from "../services/elements/MDMSV2";


const useCustomMDMSV2 = (tenantId, moduleName, masterDetails = [], config = {}) => {
    return useQuery({
        queryKey: [tenantId, moduleName, masterDetails],
        queryFn: () => MdmsServiceV2.getMultipleTypesWithFilter(tenantId, moduleName, masterDetails),
        ...config
    });
    
  };

export default useCustomMDMSV2;
