import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useWSMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const queryConfig = { staleTime: Infinity, ...config };
  const useDocumentMapping = () => {
    return queryTemplate({ queryKey: ['DOCUMENT_MAPPING'], queryFn: () => MdmsService.getDocumentTypes(tenantId, moduleCode, type), config: queryConfig });
  }
  const useTradeTypetoRoleMapping = () => {
    return queryTemplate({ queryKey: ['ROLE_DOCUMENT_MAPPING'], queryFn: () => MdmsService.getTradeTypeRoleTypes(tenantId, moduleCode, type), config: queryConfig });
  }
  const useTaxHeadMasterMapping = () => {
    return queryTemplate({ queryKey: ["TAX_HEAD_MASTER"], queryFn: ()=> MdmsService.getWSTaxHeadMaster(tenantId, moduleCode, type), config: queryConfig })
  }
  const _default = () => {
    return queryTemplate({ queryKey: [tenantId, moduleCode, type], queryFn: () => MdmsService.getMultipleTypes(tenantId, moduleCode, type), config });
  };

  switch (type) {
    case "DocumentTypes":
      return useDocumentMapping();
    case "TradeTypetoRoleMapping":
      return useTradeTypetoRoleMapping();
    case "TaxHeadMaster":
      return useTaxHeadMasterMapping()
    default:
      return _default();
  }
}

export default useWSMDMS; 