import { MdmsService } from "../services/elements/MDMS";
import { useQuery } from "@tanstack/react-query";

const useMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const usePaymentGateway = () => {
    return useQuery({
      queryKey: ["PAYMENT_GATEWAY"],
      queryFn: () => MdmsService.getPaymentGateway(tenantId, moduleCode, type),
      select: (data) => {
            return data?.[moduleCode]?.[type].filter((e) => e.active).map(({ gateway }) => gateway);
          },
      ...config
    });
  };

  const useReceiptKey = () => {
    return useQuery({
      queryKey: ["RECEIPT_KEY"],
      queryFn: () => MdmsService.getReceiptKey(tenantId, moduleCode, type),
      ...config
    });
  };

  const useBillsGenieKey = () => {
    return useQuery({
      queryKey: ["BILLS_GENIE_KEY"],
      queryFn: () => MdmsService.getBillsGenieKey(tenantId, moduleCode, type),
      ...config
    });
  };

  const _default = () => {
    return useQuery({
      queryKey: [tenantId, moduleCode, type],
      queryFn: () => MdmsService.getMultipleTypes(tenantId, moduleCode, type),
      ...config
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
