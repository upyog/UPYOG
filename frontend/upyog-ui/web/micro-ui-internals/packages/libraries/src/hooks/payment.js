import { useQueryClient } from "../common/queryClientTemplate";
import { queryTemplate } from "../common/queryTemplate";
import { PaymentService } from "../services/elements/Payment";

export const useFetchCitizenBillsForBuissnessService = ({ businessService, ...filters }, config = {}) => {
  const queryClient = useQueryClient();
  const { mobileNumber, tenantId } = Digit.UserService.getUser()?.info || {};
  const tenant = Digit.ULBService.getCitizenCurrentTenant();
  const params = { mobileNumber, businessService, ...filters };
  if (!params["mobileNumber"]) delete params["mobileNumber"];

  /* For these business services, the fetchBill API does not require mobileNumber.
    sriranjan sir has approved these changes
  */
  const skipBusinessServices = ["adv-services", "chb-services", "pet-services", "sv-services", "request-service.mobile_toilet", "request-service.water_tanker", "request-service.tree_pruning","est-services"];
  // Early return if businessService is in the skip list
  if (skipBusinessServices.includes(businessService)) {
    return {
      isLoading: false,
      error: null,
      isError: false,
      data: null,
      status: 'skipped',
      revalidate: () => queryClient.invalidateQueries({ queryKey: ["citizenBillsForBuisnessService", businessService] }),
    };
  }

  const { isLoading, error, isError, data, status } = queryTemplate({
    queryKey: ["citizenBillsForBuisnessService", businessService, { ...params }],
    queryFn: () => Digit.PaymentService.fetchBill(window.location.href.includes("mcollect")?tenant:tenantId, { ...params }),
    config: {
      refetchOnMount: true,
      retry: false,
      ...config,
    }
  });

  return {
    isLoading,
    error,
    isError,
    data,
    status,
    revalidate: () => queryClient.invalidateQueries({ queryKey: ["citizenBillsForBuisnessService", businessService] }),
  };
};

export const useFetchBillsForBuissnessService = ({ tenantId, businessService, ...filters }, config = {}) => {
  const queryClient = useQueryClient();
  let isPTAccessDone = sessionStorage.getItem("IsPTAccessDone");
  const params = { businessService, ...filters };
  const _tenantId = tenantId || Digit.UserService.getUser()?.info?.tenantId;

  const { isLoading, error, isError, data, status } = queryTemplate({
    queryKey: ["billsForBuisnessService", businessService, { ...filters }, config, isPTAccessDone],
    queryFn: () => Digit.PaymentService.fetchBill(_tenantId, params),
    config: {
      retry: (count, err) => {
        return false;
      },
      ...config,
    }
  });

  return {
    isLoading,
    error,
    isError,
    data,
    status,
    revalidate: () => queryClient.invalidateQueries({ queryKey: ["billsForBuisnessService", businessService] }),
  };
};

export const useFetchPayment = ({ tenantId, consumerCode, businessService }, config) => {
  const queryClient = useQueryClient();

  const fetchBill = async () => {
    /*  Currently enabled the logic to get bill no and expiry date for PT Module  */
    if (businessService?.includes("PT") || businessService?.includes("SW") || businessService?.includes("WS")) {
      const fetchedBill = await Digit.PaymentService.fetchBill(tenantId, { consumerCode, businessService });
      const billdetail = fetchedBill?.Bill?.[0]?.billDetails?.sort((a, b) => b.fromPeriod - a.fromPeriod)?.[0] || {};
      fetchedBill.Bill[0].billDetails = fetchedBill?.Bill[0]?.billDetails?.map((ele) => ({
        ...ele,
        currentBillNo: fetchedBill?.Bill?.[0]?.billNumber,
        currentExpiryDate: billdetail?.expiryDate,
      }));
      if (fetchedBill && fetchedBill?.Bill?.[0]?.billDetails?.length > 1) {
        fetchedBill?.Bill?.[0]?.billDetails?.map(async (billdet) => {
          const searchBill = await Digit.PaymentService.searchBill(tenantId, {
            consumerCode,
            fromPeriod: billdet?.fromPeriod,
            toPeriod: billdet?.toPeriod,
            service: businessService,
            retrieveOldest: true,
          });
          billdet.expiryDate = searchBill?.Bill?.[0]?.billDetails?.[0]?.expiryDate;
          billdet.billNumber = searchBill?.Bill?.[0]?.billNumber;
        });
      }
      return fetchedBill;
    } else {
      return Digit.PaymentService.fetchBill(tenantId, { consumerCode, businessService });
    }
  };

  const retry = (failureCount, error) => {
    const code = error?.response?.data?.Errors?.[0]?.code || "";
    if (code === "EG_BS_BILL_NO_DEMANDS_FOUND" || code === "EMPTY_DEMANDS" || String(code).includes("NO_DEMAND")) {
      return false;
    }
    else return failureCount < 3;
  };

  const queryData = queryTemplate({
    queryKey: ["paymentFetchDetails", tenantId, consumerCode, businessService],
    queryFn: () => fetchBill(),
    config: { retry, ...config }
  });

  return {
    ...queryData,
    revalidate: () => queryClient.invalidateQueries({ queryKey: ["paymentFetchDetails", tenantId, consumerCode, businessService] }),
  };
};

export const usePaymentUpdate = ({ egId }, businessService, config) => {
  const getPaymentData = async (egId) => {
    const transaction = await Digit.PaymentService.updateCitizenReciept(egId);
    const payments = await Digit.PaymentService.getReciept(transaction.Transaction[0].tenantId, businessService, {
      consumerCodes: transaction.Transaction[0].consumerCode,
    });
    return { payments, applicationNo: transaction.Transaction[0].consumerCode, txnStatus: transaction.Transaction[0].txnStatus };
  };

  return queryTemplate({
    queryKey: ["paymentUpdate", egId],
    queryFn: () => getPaymentData(egId),
    config
  });
};

export const useGetPaymentRulesForBusinessServices = (tenantId) => {
  return queryTemplate({
    queryKey: ["getPaymentRules", tenantId],
    queryFn: () => Digit.MDMSService.getPaymentRules(tenantId),
  });
};

export const usePaymentSearch = (tenantId, filters, config = {}) => {
  return queryTemplate({
    queryKey: ["PAYMENT_SERACH", tenantId],
    queryFn: () => Digit.PaymentService.searchBill(tenantId, filters),
    select: (data) => {
      return data?.Bill?.[0]?.billDetails?.[0]?.billAccountDetails.filter((e) => {
        switch (e.taxHeadCode) {
          case "WS_CHARGE":
          case "WS_TIME_PENALTY":
          case "WS_TIME_INTEREST":
          case "SW_TIME_INTEREST":
          case "SW_TIME_PENALTY":
          case "SW_CHARGE":
          case "WS_WATER_CESS":
          case "WS_TIME_ADHOC_PENALTY":
          case "WS_TIME_ADHOC_REBATE":
          case "SW_TIME_ADHOC_PENALTY":
          case "SW_TIME_ADHOC_REBATE":
            return true;
          default:
            return false;
        }
      });
    },
    config,
  });
};

export const useDemandSearch = ({ consumerCode, businessService, tenantId }, config = {}) => {
  if (!tenantId) tenantId = Digit.ULBService.getCurrentTenantId();
  const queryFn = () => Digit.PaymentService.demandSearch(tenantId, consumerCode, businessService);
  const queryData = queryTemplate({
    queryKey: ["demand_search", { consumerCode, businessService, tenantId }],
    queryFn,
    config: { refetchOnMount: "always", ...config }
  });
  return queryData;
};

export const useAssetQrCode = ({ tenantId, ...params }, config = {}) => {     
  return queryTemplate({
    queryKey: ["assets_Reciept_Search", { tenantId, params },config],
    queryFn: () => Digit.PaymentService.useAssetQrCodeService(tenantId, params),
    config: {
      refetchOnMount: false,
      ...config,
    }
  });
};

export const useRecieptSearch = ({ tenantId, businessService, ...params }, config = {}) => {
  return queryTemplate({
    queryKey: ["reciept_search", { tenantId, businessService, params },config],
    queryFn: () => Digit.PaymentService.recieptSearch(tenantId, businessService, params),
    config: {
      refetchOnMount: false,
      ...config,
    }
  });
};

export const useRecieptSearchNew = ({ tenantId, ...params }, config = {}) => {
  return queryTemplate({
    queryKey: ["obps_Reciept_Search", { tenantId, params },config],
    queryFn: () => Digit.PaymentService.recieptSearchNew(tenantId, params),
    config: {
      refetchOnMount: false,
      ...config,
    }
  });
};

export const useBulkPdfDetails = ({ filters }) => {
  return queryTemplate({
    queryKey: ["BULK_PDF_DETAILS", filters],
    queryFn: async () => await PaymentService.getBulkPdfRecordsDetails(filters)
  });
};