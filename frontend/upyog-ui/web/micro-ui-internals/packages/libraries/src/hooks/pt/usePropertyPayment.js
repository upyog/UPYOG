import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const usePropertyPayment = ({ tenantId, consumerCodes }) => {
  const client = useQueryClient();
  const { isLoading, error, data } = queryTemplate({
    queryKey: ["propertyPaymentList", tenantId, consumerCodes],
    queryFn: () => Digit.PTService.fetchPaymentDetails({ tenantId, consumerCodes }),
  });
  return { isLoading, error, data, revalidate: () => client.invalidateQueries({ queryKey: ["propertyPaymentList", tenantId, consumerCodes] }) };
};

export default usePropertyPayment;
