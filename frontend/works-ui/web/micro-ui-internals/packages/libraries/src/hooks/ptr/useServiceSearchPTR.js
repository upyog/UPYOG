import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const useServiceSearchPTR = ({ tenantId, filters }, config = {}) => {
  const client = useQueryClient();

  const serviceSearchArg = { filters: { ServiceCriteria: { tenantId: filters?.serviceSearchArgs?.tenantId, referenceIds: filters?.serviceSearchArgs?.referenceIds } } };
  const serviceconfig = { ...config, cacheTime: 0 };

  const { isLoading: serviceLoading, error: serviceerror, data: servicedata } = queryTemplate({
    queryKey: ["ServiceSearch", tenantId, filters],
    queryFn: () => Digit.PTService.cfsearch(serviceSearchArg),
    config: serviceconfig,
  });

  return { isLoading: serviceLoading, error: serviceerror, data: servicedata, revalidate: () => client.invalidateQueries({ queryKey: ["ServiceSearch", tenantId, filters] }) };
};

export default useServiceSearchPTR;
