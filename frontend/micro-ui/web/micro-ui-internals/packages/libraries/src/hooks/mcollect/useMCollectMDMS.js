import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useMCollectMDMS = (tenantId, moduleCode, type, filter, config = {}) => {
  const useMCollectBillingService = () => {
    return useQuery("MCOLLECT_BILLING_SERVICE", () => MdmsServiceV2.getMCollectBillingService(tenantId, moduleCode, type, filter), config);
  };
  const useMCollectApplcationStatus = () => {
    return useQuery("MCOLLECT_APPLICATION_STATUS", () => MdmsServiceV2.getMCollectApplcationStatus(tenantId, moduleCode, type, filter), config);
  };

  switch (type) {
    case "BusinessService":
      return useMCollectBillingService();
    case "applicationStatus":
      return useMCollectApplcationStatus();
    default:
      return null;
  }
};

export default useMCollectMDMS;
