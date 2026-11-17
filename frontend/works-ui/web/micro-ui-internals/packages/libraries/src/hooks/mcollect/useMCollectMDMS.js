import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useMCollectMDMS = (tenantId, moduleCode, type, filter, config = {}) => {
  switch (type) {
    case "BusinessService":
      return queryTemplate({
        queryKey: ["MCOLLECT_BILLING_SERVICE", tenantId],
        queryFn: () =>
          MdmsService.getMCollectBillingService(
            tenantId,
            moduleCode,
            type,
            filter
          ),
        config,
      });

    case "applicationStatus":
      return queryTemplate({
        queryKey: ["MCOLLECT_APPLICATION_STATUS", tenantId],
        queryFn: () =>
          MdmsService.getMCollectApplcationStatus(
            tenantId,
            moduleCode,
            type,
            filter
          ),
        config,
      });

    default:
      return null;
  }
};

export default useMCollectMDMS;