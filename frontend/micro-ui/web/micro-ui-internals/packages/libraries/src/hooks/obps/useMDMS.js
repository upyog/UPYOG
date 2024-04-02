import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const queryConfig = { staleTime: Infinity, ...config };
  const useDocumentMapping = () => {
    return useQuery("DOCUMENT_MAPPING", () => MdmsServiceV2.getDocumentTypes(tenantId, moduleCode, type), queryConfig);
  };
  const useTradeTypetoRoleMapping = () => {
    return useQuery("ROLE_DOCUMENT_MAPPING", () => MdmsServiceV2.getTradeTypeRoleTypes(tenantId, moduleCode, type), queryConfig);
  };
  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsServiceV2.getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    case "DocumentTypes":
      return useDocumentMapping();
    case "TradeTypetoRoleMapping":
      return useTradeTypetoRoleMapping();
    default:
      return _default();
  }
};

export default useMDMS;
