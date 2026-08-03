import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService, getGeneralCriteria } from "../../services/elements/MDMS";

/**
 * Fetch MDMS data for engagement.
 */
export const useEngagementMDMS = (
  tenantId,
  moduleCode,
  type,
  config = {},
  payload = []
) => {
  if (type === "DocumentsCategory") {
    return queryTemplate({
      queryKey: ["ENGAGEMENT_MDMS_CATEGORY", tenantId, moduleCode, type],
      queryFn: () =>
        MdmsService.getDataByCriteria(
          tenantId,
          getGeneralCriteria(tenantId, moduleCode, type),
          moduleCode
        ),
      config,
    });
  }

  return queryTemplate({
    queryKey: [
      "ENGAGEMENT_MDMS",
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