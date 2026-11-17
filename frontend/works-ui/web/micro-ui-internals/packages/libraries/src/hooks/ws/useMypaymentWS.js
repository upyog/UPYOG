import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

export const useMypaymentWS = ({ tenantId, filters, BusinessService="WS",searchedFrom="" }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, BusinessService } : { filters, BusinessService };

  const { isLoading, error, data, isSuccess } = queryTemplate({ queryKey: ["WSSearchList", tenantId, filters, BusinessService], queryFn: () => Digit.WSService.paymentsearch(args), config });

  return {isLoading,error, data, isSuccess, revalidate: () => client.invalidateQueries({ queryKey: ["WSSearchList", tenantId, filters, BusinessService] }) };

};
export default useMypaymentWS