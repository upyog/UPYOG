import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useReceiptsMDMS = (tenantId, type, config = {}) => {
  const client = useQueryClient();

  const useReceiptsBusinessServices = () => {
    const { isLoading, error, data } = queryTemplate({ queryKey: ["RECEIPTS_SERVICES", tenantId], queryFn: () => MdmsService.getReceiptKey(tenantId, "common-masters"), config });
    if (!isLoading && data && data[`common-masters`]?.uiCommonPay && Array.isArray(data[`common-masters`].uiCommonPay)) {
      data[`common-masters`].uiCommonPay = data[`common-masters`].uiCommonPay.filter((unit) => unit.cancelReceipt) || [];
      data.dropdownData = data[`common-masters`].uiCommonPay.map((cfg) => ({ code: cfg.code, name: `BILLINGSERVICE_BUSINESSSERVICE_${cfg.code}` }));
    }
    return { isLoading, error, data, revalidate: () => client.invalidateQueries({ queryKey: ["RECEIPTS_SERVICES", tenantId] }) };
  };

  const useCancelReceiptReason = () => {
    const { isLoading, error, data } = queryTemplate({ queryKey: ["RECEIPTS_CANCEL_REASON", tenantId], queryFn: () => MdmsService.getCancelReceiptReason(tenantId, "common-masters"), config });
    if (!isLoading && data && data[`common-masters`]?.CancelReceiptReason && Array.isArray(data[`common-masters`].CancelReceiptReason)) {
      data[`common-masters`].CancelReceiptReason = data[`common-masters`].CancelReceiptReason.filter((unit) => unit.active) || [];
      data.dropdownData = data[`common-masters`].CancelReceiptReason.map((cfg) => ({ code: cfg.code, name: `CR_REASON_${cfg.code}` }));
    }
    return { isLoading, error, data, revalidate: () => client.invalidateQueries({ queryKey: ["RECEIPTS_CANCEL_REASON", tenantId] }) };
  };

  const useCancelReceiptStatus = () => {
    const { isLoading, error, data } = queryTemplate({ queryKey: ["RECEIPTS_CANCEL_STATUS", tenantId], queryFn: () => MdmsService.getReceiptStatus(tenantId, "common-masters"), config });
    if (!isLoading && data && data[`common-masters`]?.ReceiptStatus && Array.isArray(data[`common-masters`].ReceiptStatus)) {
      data[`common-masters`].ReceiptStatus = data[`common-masters`].ReceiptStatus.filter((unit) => unit.active) || [];
      data.dropdownData = data[`common-masters`].ReceiptStatus.map((cfg) => ({ code: cfg.code, name: `RC_${cfg.code}` }));
    }
    return { isLoading, error, data, revalidate: () => client.invalidateQueries({ queryKey: ["RECEIPTS_CANCEL_STATUS", tenantId] }) };
  };

  const useCancelReceiptReasonAndStatus = () => {
    const { isLoading, error, data } = queryTemplate({ queryKey: ["RECEIPTS_CANCEL_REASON_STATUS", tenantId], queryFn: () => MdmsService.getCancelReceiptReasonAndStatus(tenantId, "common-masters"), config });
    if (!isLoading && data && data[`common-masters`]?.uiCommonPay && Array.isArray(data[`common-masters`].uiCommonPay)) {
      data[`common-masters`].uiCommonPay = data[`common-masters`].uiCommonPay.filter((unit) => unit.cancelReceipt) || [];
      data.dropdownData = data[`common-masters`].uiCommonPay.map((cfg) => ({ code: cfg.code, name: `BILLINGSERVICE_BUSINESSSERVICE_${cfg.code}` }));
      if (data[`common-masters`]?.ReceiptStatus && Array.isArray(data[`common-masters`].ReceiptStatus)) {
        data[`common-masters`].ReceiptStatus = data[`common-masters`].ReceiptStatus.filter((unit) => unit.active) || [];
        data.dropdownDataStatus = data[`common-masters`].ReceiptStatus.map((cfg) => ({ code: cfg.code, name: `RC_${cfg.code}` }));
      }
    }
    return { isLoading, error, data, revalidate: () => client.invalidateQueries({ queryKey: ["RECEIPTS_CANCEL_REASON_STATUS", tenantId] }) };
  };

  switch (type) {
    case "ReceiptsBusinessServices":
      return useReceiptsBusinessServices();
    case "CancelReceiptReason":
      return useCancelReceiptReason();
    case "CancelReceiptStatus":
      return useCancelReceiptStatus();
    case "CancelReceiptReasonAndStatus":
      return useCancelReceiptReasonAndStatus();
    default:
      return null;
  }
};

export default useReceiptsMDMS;
