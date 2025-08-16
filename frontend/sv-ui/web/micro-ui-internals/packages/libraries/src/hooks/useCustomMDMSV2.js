import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../services/elements/MDMSV2";


const useCustomMDMSV2 = (tenantId, moduleName, masterDetails = [], config = {}) => {
    return useQuery([tenantId, moduleName, masterDetails], () => MdmsServiceV2.getMultipleTypesWithFilter(tenantId, moduleName, masterDetails), config);
  };

export default useCustomMDMSV2;
