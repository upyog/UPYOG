import { MdmsService } from "../services/elements/MDMS";
import { queryTemplate } from "../common/queryTemplate";

const useMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const usePaymentGateway = () => {
    return queryTemplate({
      queryKey: ["PAYMENT_GATEWAY"],
      queryFn: () => MdmsService.getPaymentGateway(tenantId, moduleCode, type),
      select: (data) => {
        return data?.[moduleCode]?.[type].filter((e) => e.active).map(({ gateway }) => gateway);
      },
      config
    });
  };

  const useReceiptKey = () => {
    return queryTemplate({
      queryKey: ["RECEIPT_KEY"],
      queryFn: () => MdmsService.getReceiptKey(tenantId, moduleCode, type),
      config
    });
  };

  const useBillsGenieKey = () => {
    return queryTemplate({
      queryKey: ["BILLS_GENIE_KEY"],
      queryFn: () => MdmsService.getBillsGenieKey(tenantId, moduleCode, type),
      config
    });
  };

  const _default = () => {
    return queryTemplate({
      queryKey: [tenantId, moduleCode, type],
      queryFn: () => MdmsService.getMultipleTypes(tenantId, moduleCode, type),
      config
    });
  };

  switch (type) {
    case "PaymentGateway":
      return usePaymentGateway();
    case "ReceiptKey":
      return useReceiptKey();
    case "BillsGenieKey":
      return useBillsGenieKey();
    default:
      return _default();
  }
};

export default useMDMS;
