import { queryTemplate } from "../common/queryTemplate"; 
import { MdmsService } from "../services/elements/MDMS";

const useMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const usePaymentGateway = () => {
    return queryTemplate({
      queryKey: ["PAYMENT_GATEWAY"],
      queryFn: () => MdmsService.getPaymentGateway(tenantId, moduleCode, type),
      config: {
        select: (data) => {
          return data?.[moduleCode]?.[type].filter((e) => e.active).map(({ gateway }) => gateway);
        },
        ...config,
      },                                                  
    });
  };

  const useReceiptKey = () => {
    return queryTemplate({
      queryKey: ["RECEIPT_KEY"],
      queryFn: () => MdmsService.getReceiptKey(tenantId, moduleCode, type),
      config,
    });
  };

  const useBillsGenieKey = () => {
    return queryTemplate({
      queryKey: ["BILLS_GENIE_KEY"],
      queryFn: () => MdmsService.getBillsGenieKey(tenantId, moduleCode, type),
      config,
    });
  };

  const useFSTPPlantInfo = () => {
    return queryTemplate({
      queryKey: ["FSTP_PLANTINFO"],
      queryFn: () => MdmsService.getFSTPPlantInfo(tenantId, moduleCode, type),
      config,
    });
  };

  const _default = () => {
    return queryTemplate({
      queryKey: ["DEFAULT"],
      queryFn: () => MdmsService.getMultipleTypes(tenantId, moduleCode, type),
      config,
    });
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
