import { WSSearch } from "../../services/molecules/WS/Search";
import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const useConnectionDetail = (t, tenantId, connectionNumber, serviceType, config = {}) => {
  const client = useQueryClient();
  const { isLoading, error, data, isSuccess } = queryTemplate({
    queryKey: ["APPLICATION_WS_SEARCH", "WNS_SEARCH", connectionNumber, serviceType, config],
    queryFn: () => WSSearch.connectionDetails(t, tenantId, connectionNumber, serviceType),
    config,
  });
  return {
    isLoading,
    error,
    data,
    isSuccess,
    revalidate: () => client.invalidateQueries({ queryKey: ["APPLICATION_WS_SEARCH", "WNS_SEARCH", connectionNumber, serviceType] }),
  };
};

export default useConnectionDetail;
