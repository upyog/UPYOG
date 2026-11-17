import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const baseConfig = { staleTime: Infinity, ...config };

  switch (type) {
    case "DocumentTypes":
      return queryTemplate({
        queryKey: ["OBPS_DOC_TYPES", tenantId],
        queryFn: () =>
          MdmsService.getDocumentTypes(tenantId, moduleCode, type),
        config: baseConfig,
      });

    case "TradeTypetoRoleMapping":
      return queryTemplate({
        queryKey: ["OBPS_ROLE_DOC", tenantId],
        queryFn: () =>
          MdmsService.getTradeTypeRoleTypes(tenantId, moduleCode, type),
        config: baseConfig,
      });

    case "Disclaimer":
      return queryTemplate({
        queryKey: ["OBPS_DISCLAIMER", tenantId],
        queryFn: () =>
          MdmsService.getDisclaimer(tenantId, moduleCode, type),
        config,
      });

    default:
      return queryTemplate({
        queryKey: ["OBPS_MDMS", tenantId, moduleCode, type],
        queryFn: () =>
          MdmsService.getMultipleTypes(tenantId, moduleCode, type),
        config,
      });
  }
};

export default useMDMS;