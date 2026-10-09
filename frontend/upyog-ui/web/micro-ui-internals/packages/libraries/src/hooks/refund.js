import { queryTemplate } from "../common/queryTemplate";
import { mutationTemplate } from "../common/mutationTemplate";
import { useQueryClient } from "../common/queryClientTemplate";
import { RefundService } from "../services/elements/Refund";

export const useRefundSearch = (
  {
    tenantId,
    consumerCode,
    moduleName = "CHB",
    businessService = "CHB.REFUND",
  },
  config = {}
) => {
  const queryClient = useQueryClient();

  const queryKey = [
    "refund-search",
    tenantId,
    consumerCode,
    moduleName,
    businessService,
  ];

  const queryData = queryTemplate({
    queryKey,
    queryFn: () =>
      RefundService.search(
        {
          consumerCode,
          moduleName,
          businessService,
        },
        {
          tenantId,
        }
      ),
    config: {
      retry: false,
      refetchOnWindowFocus: false,
      ...config,
      enabled:
        Boolean(tenantId && consumerCode) &&
        config.enabled !== false,
    },
  });

  return {
    ...queryData,
    revalidate: () =>
      queryClient.invalidateQueries({
        queryKey,
      }),
  };
};

export const useCreateRefund = (tenantId) => {
  const mutationFn = async (payload) => {
    const response = await RefundService.create(payload, {
      tenantId,
    });

    if (response?.Errors?.length) {
      throw new Error(
        response.Errors
          .map((error) => error.message || error.code)
          .join(", ")
      );
    }

    return response;
  };

  return mutationTemplate({ mutationFn });
};

export const useCompleteOfflineRefund = (tenantId) => {
  const mutationFn = async (payload) => {
    const refund = payload?.refund;

    if (!tenantId || !refund?.id || refund.tenantId !== tenantId) {
      throw new Error("A valid refund and matching tenant are required.");
    }

    if (refund.refundMode !== "OFFLINE") {
      throw new Error("Only offline refunds can be completed here.");
    }

    if (refund.processInstance?.action !== "COMPLETE_REFUND") {
      throw new Error("The action must be COMPLETE_REFUND.");
    }

    if (!["CASH", "CHEQUE", "DD"].includes(refund.beneficiaryDetails?.mode)) {
      throw new Error("Select Cash, Cheque or DD.");
    }

    const response = await RefundService.update(payload, {
      tenantId,
    });

    if (response?.Errors?.length) {
      throw new Error(
        response.Errors
          .map((error) => error.message || error.code)
          .join(", ")
      );
    }

    return response;
  };

  return mutationTemplate({ mutationFn });
};