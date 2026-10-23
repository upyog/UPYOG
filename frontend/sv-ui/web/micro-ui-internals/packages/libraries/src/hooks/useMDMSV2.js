import { MdmsServiceV2 } from "../services/elements/MDMSV2";
import { useQuery } from "@tanstack/react-query";

const useMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const usePaymentGateway = () => {
    return useQuery({
      queryKey: ["PAYMENT_GATEWAY"],
      queryFn: () => MdmsServiceV2.getPaymentGateway(tenantId, moduleCode, type),
      select: (data) => {
        return data?.[moduleCode]?.[type].filter((e) => e.active).map(({ gateway }) => gateway);
      },
      ...config,
    });
  };

  const useReceiptKey = () => {
    return useQuery({
      queryKey: ["RECEIPT_KEY"],
      queryFn: () => MdmsServiceV2.getReceiptKey(tenantId, moduleCode, type),
      ...config
    });
  };

  const useBillsGenieKey = () => {
    return useQuery({
      queryKey: ["BILLS_GENIE_KEY"],
      queryFn: () => MdmsServiceV2.getBillsGenieKey(tenantId, moduleCode, type),
      ...config
    });
  };


  const _default = () => {
    return useQuery({
      queryKey: [tenantId, moduleCode, type],
      queryFn: () => MdmsServiceV2.getMultipleTypes(tenantId, moduleCode, type),
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
