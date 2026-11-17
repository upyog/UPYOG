import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

/**
 * Fetch DSS MDMS data.
 */
const useDssMDMS = (tenantId, moduleCode, type, config = {}) => {
  if (type === "DssDashboard") {
    return queryTemplate({
      queryKey: ["DSS_MDMS_DASHBOARD", tenantId, moduleCode],
      queryFn: () =>
        MdmsService.getDssDashboard(tenantId, moduleCode),
      config,
    });
  }

  return queryTemplate({
    queryKey: [
      "DSS_MDMS",
      tenantId,
      moduleCode,
      type,
    ],
    queryFn: () =>
      MdmsService.getMultipleTypes(
        tenantId,
        moduleCode,
        type
      ),
    config,
  });
};

export default useDssMDMS;