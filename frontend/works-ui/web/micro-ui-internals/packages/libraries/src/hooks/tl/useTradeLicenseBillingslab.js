import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const useTradeLicenseBillingslab = ({ tenantId, filters, auth }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };
  const { isLoading, error, data } = queryTemplate({ queryKey: ["TLbillingSlabSerach", tenantId, filters], queryFn: () => Digit.TLService.billingslab(args), config });
  return { isLoading, error, data, revalidate: () => client.invalidateQueries({ queryKey: ["TLbillingSlabSerach", tenantId, filters] }) };
};

export default useTradeLicenseBillingslab;
