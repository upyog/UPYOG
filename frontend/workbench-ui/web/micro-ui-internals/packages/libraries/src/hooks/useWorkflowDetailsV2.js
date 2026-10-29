import { queryTemplate } from "../common/queryTemplate";
import { useQueryClient } from "../common/queryClientTemplate";

const useWorkflowDetailsV2 = ({ tenantId, id, moduleCode, role = "CITIZEN", serviceData = {}, getStaleData, getTripData = false, config }) => {
  const queryClient = useQueryClient();

  const staleDataConfig = { staleTime: Infinity };

  const { isLoading, error, isError, data } = queryTemplate({
    queryKey: ["workFlowDetailsWorks", tenantId, id, moduleCode, role, config],
    queryFn: () => Digit.WorkflowService.getDetailsByIdV2({ tenantId, id, moduleCode, role, getTripData }),
    config: getStaleData ? { ...staleDataConfig, ...config } : config,
  });                                                            

  if (getStaleData) return { isLoading, error, isError, data };

  return {
    isLoading,
    error,
    isError,
    data,
    revalidate: () => queryClient.invalidateQueries({           
      queryKey: ["workFlowDetailsWorks", tenantId, id, moduleCode, role]
    }),
  };
};

export default useWorkflowDetailsV2;