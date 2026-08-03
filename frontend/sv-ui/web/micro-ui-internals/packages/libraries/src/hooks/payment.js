import { useQuery, useQueryClient } from "@tanstack/react-query";
import { PaymentService } from "../services/elements/Payment";

export const useFetchCitizenBillsForBuissnessService = ({ businessService, ...filters }, config = {}) => {
  const queryClient = useQueryClient();
  const { mobileNumber, tenantId } = Digit.UserService.getUser()?.info || {};
  const tenant = Digit.ULBService.getCitizenCurrentTenant();
  const params = { mobileNumber, businessService, ...filters };
  if (!params["mobileNumber"]) delete params["mobileNumber"];

// Updated: TanStack Query v5 requires useQuery to accept a single object instead of positional arguments.
// Updated: queryKey and queryFn are now explicit keys inside the object — positional args removed.
  const { isLoading, error, isError, data, status } = useQuery({
    queryKey: ["citizenBillsForBuisnessService", businessService, { ...params }],
    queryFn: () => Digit.PaymentService.fetchBill(tenant, { ...params }),
    refetchOnMount: true,
    retry: false,
    ...config,
  });

  return {
    isLoading,
    error,
    isError,
    data,
    status,
    // Updated: invalidateQueries now requires an object with queryKey instead of positional arguments.
    revalidate: () => queryClient.invalidateQueries({ queryKey: ["citizenBillsForBuisnessService", businessService] }),
  };
};

export const useFetchBillsForBuissnessService = ({ tenantId, businessService, ...filters }, config = {}) => {
  const queryClient = useQueryClient();
  // let isPTAccessDone = sessionStorage.getItem("IsPTAccessDone");
  const params = { businessService, ...filters };
  const _tenantId = tenantId || Digit.UserService.getUser()?.info?.tenantId;
  const { isLoading, error, isError, data, status } = useQuery({
    queryKey: ["billsForBuisnessService", businessService, { ...filters }, config, /*isPTAccessDone*/ false],
    queryFn: () => Digit.PaymentService.fetchBill(_tenantId, params),
    retry: (count, err) => {
      return false;
    },
    ...config,
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

  const queryData = useQuery({
    queryKey: ["paymentFetchDetails", tenantId, consumerCode, businessService],
    queryFn: () => fetchBill(),
    retry,
    ...config
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

  return useQuery({
    queryKey: ["paymentUpdate", egId],
    queryFn: () => getPaymentData(egId),
    ...config
  });
};


export const useDemandSearch = ({ consumerCode, businessService, tenantId }, config = {}) => {
  if (!tenantId) tenantId = Digit.ULBService.getCurrentTenantId();
  const queryFn = () => Digit.PaymentService.demandSearch(tenantId, consumerCode, businessService);
  // Updated: TanStack Query v5 requires useQuery to accept a single object instead of positional arguments.
  // Updated: queryKey and queryFn are now explicit keys inside the object — positional args removed.
  const queryData = useQuery({
    queryKey: ["demand_search", { consumerCode, businessService, tenantId }],
    queryFn,
    refetchOnMount: "always",
    ...config
  });
  return queryData;
};

export const useAssetQrCode = ({ tenantId, ...params }, config = {}) => {     
  return useQuery(
    {
      queryKey: ["assets_Reciept_Search", { tenantId, params },config],
      queryFn: () => Digit.PaymentService.useAssetQrCodeService(tenantId, params),
      refetchOnMount: false,
      ...config,
    }
  );
};

export const useRecieptSearch = ({ tenantId, businessService, ...params }, config = {}) => {
  // Updated: TanStack Query v5 requires useQuery to accept a single object instead of positional arguments.
  return useQuery(
    {
      queryKey: ["reciept_search", { tenantId, businessService, params },config],
      queryFn: () => Digit.PaymentService.recieptSearch(tenantId, businessService, params),
      refetchOnMount: false,
      ...config,
    }
  );
};
export const useRecieptSearchNew = ({ tenantId, ...params }, config = {}) => {
  // Updated: TanStack Query v5 requires useQuery to accept a single object instead of positional arguments.
  return useQuery(
    {
      queryKey: ["obps_Reciept_Search", { tenantId, params },config],
      queryFn: () => Digit.PaymentService.recieptSearchNew(tenantId, params),
      refetchOnMount: false,
      ...config,
    }
  );
};

export const useBulkPdfDetails = ({ filters }) => {
  // Updated: TanStack Query v5 requires useQuery to accept a single object instead of positional arguments.
  return useQuery({
    queryKey: ["BULK_PDF_DETAILS", filters],
    queryFn: async () => await PaymentService.getBulkPdfRecordsDetails(filters)
  });
};
