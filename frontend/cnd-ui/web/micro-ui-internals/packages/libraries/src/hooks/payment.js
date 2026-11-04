import { useQueryClient } from "../common/queryClientTemplate";
import { queryTemplate } from "../common/queryTemplate";
import { PaymentService } from "../services/elements/Payment";

export const useFetchCitizenBillsForBuissnessService = ({ businessService, ...filters }, config = {}) => {
  const queryClient = useQueryClient();
  const { mobileNumber, tenantId } = Digit.UserService.getUser()?.info || {};
  const tenant = Digit.ULBService.getCitizenCurrentTenant();
  const params = { mobileNumber, businessService, ...filters };
  if (!params["mobileNumber"]) delete params["mobileNumber"];


  const { isLoading, error, isError, data, status } = queryTemplate({
    queryKey: ["citizenBillsForBuisnessService", businessService, { ...params }],
    queryFn: () => Digit.PaymentService.fetchBill(tenantId, { ...params }),
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
  // let isPTAccessDone = sessionStorage.getItem("IsPTAccessDone");
  const params = { businessService, ...filters };
  const _tenantId = tenantId || Digit.UserService.getUser()?.info?.tenantId;
  const { isLoading, error, isError, data, status } = queryTemplate({
    queryKey: ["billsForBuisnessService", businessService, { ...filters }, config, /*isPTAccessDone*/ false],
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


  //commented out above code and make it efficient
  const fetchBill = () =>{
    return Digit.PaymentService.fetchBill(tenantId, { consumerCode, businessService });
  }
  
  const retry = (failureCount, error) => {
    if (error?.response?.data?.Errors?.[0]?.code === "EG_BS_BILL_NO_DEMANDS_FOUND") return false;
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
