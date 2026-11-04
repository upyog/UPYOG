import { queryTemplate } from "../common/queryTemplate";
import { MdmsServiceV2 } from "../services/elements/MDMSV2";


const useMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePaymentGateway = () => {
    return queryTemplate({ queryKey: ["PAYMENT_GATEWAY"], queryFn: () => MdmsServiceV2.getPaymentGateway(tenantId, moduleCode, type), config: {
      select: (data) => {
        return data?.[moduleCode]?.[type].filter((e) => e.active).map(({ gateway }) => gateway);
      },
      ...config,
    } });
  };

  const useReceiptKey = () => {
    return queryTemplate({ queryKey: ["RECEIPT_KEY"], queryFn: () => MdmsServiceV2.getReceiptKey(tenantId, moduleCode, type), config });
  };

  const useBillsGenieKey = () => {
    return queryTemplate({ queryKey: ["BILLS_GENIE_KEY"], queryFn: () => MdmsServiceV2.getBillsGenieKey(tenantId, moduleCode, type), config });
  };

  const useFSTPPlantInfo = () => {
    return queryTemplate({ queryKey: ["FSTP_PLANTINFO"], queryFn: () => MdmsServiceV2.getFSTPPlantInfo(tenantId, moduleCode, type), config });
  };

  const _default = () => {
    return queryTemplate({ queryKey: [tenantId, moduleCode, type], queryFn: () => MdmsServiceV2.getMultipleTypes(tenantId, moduleCode, type), config });
  };

  switch (type) {
    case "PaymentGateway":
      return usePaymentGateway();
    case "ReceiptKey":
      return useReceiptKey();
    case "FSTPPlantInfo":
      return useFSTPPlantInfo();
    case "BillsGenieKey":
      return useBillsGenieKey();
    default:
      return _default();
  }
};

export default useMDMS;
