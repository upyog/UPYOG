import { queryTemplate } from "../common/queryTemplate";
import { MdmsService } from "../services/elements/MDMS";

const useCustomMDMS = (tenantId, moduleName, masterDetails = [], config = {}) => {
  return queryTemplate({
    queryKey: [tenantId, moduleName, masterDetails],
    queryFn: () => MdmsService.getMultipleTypesWithFilter(tenantId, moduleName, masterDetails),
    config
  });
};

export default useCustomMDMS;
