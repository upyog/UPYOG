import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const queryConfig = { staleTime: Infinity, ...config };
  const useDocumentMapping = () => {
    return useQuery("DOCUMENT_MAPPING", () => MdmsService.getDocumentTypes(tenantId, moduleCode, type), queryConfig);
  };
  const useTradeTypetoRoleMapping = () => {
    return useQuery("ROLE_DOCUMENT_MAPPING", () => MdmsService.getTradeTypeRoleTypes(tenantId, moduleCode, type), queryConfig);
  };
  const useDisclaimer = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsService.getDisclaimer(tenantId, moduleCode, type), config);
  };
  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsService.getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    case "DocumentTypes":
      return useDocumentMapping();
    case "TradeTypetoRoleMapping":
      return useTradeTypetoRoleMapping();
    case "Disclaimer":
      return useDisclaimer();
    default:
      return _default();
  }
};

export default useMDMS;
