import { MdmsService } from "../services/elements/MDMS";
import { useQuery } from "react-query";

const useMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const usePaymentGateway = () => {
    return useQuery("PAYMENT_GATEWAY", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, type), {
      select: (data) => {
            return data?.[moduleCode]?.[type].filter((e) => e.active).map(({ gateway }) => gateway);
          },
      ...config
    });
  };

  const useReceiptKey = () => {
    return useQuery("RECEIPT_KEY", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, type), config);
  };

  const useBillsGenieKey = () => {
    return useQuery("BILLS_GENIE_KEY", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, type), config);
  };

  const useFSTPPlantInfo = () => {
    return useQuery("FSTP_PLANTINFO", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, type), config);
  };

  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, type), config);
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
