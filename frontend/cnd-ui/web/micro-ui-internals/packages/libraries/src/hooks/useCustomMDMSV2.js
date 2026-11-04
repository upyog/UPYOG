import { queryTemplate } from "../common/queryTemplate";
import { MdmsServiceV2 } from "../services/elements/MDMSV2";


const useCustomMDMSV2 = (tenantId, moduleName, masterDetails = [], config = {}) => {
    return queryTemplate({
      queryKey: [tenantId, moduleName, masterDetails],
      queryFn: () => MdmsServiceV2.getMultipleTypesWithFilter(tenantId, moduleName, masterDetails),
      config
    });
  };

export default useCustomMDMSV2;
