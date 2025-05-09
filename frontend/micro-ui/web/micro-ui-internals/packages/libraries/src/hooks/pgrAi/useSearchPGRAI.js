import { useQuery, useQueryClient } from "react-query";

const useSearchPGRAI = ({ tenantId, filters, auth = true }, config = {}) => {

  const client = useQueryClient();
    
  const defaultSelect = (data) => {
    if (!data) return { ServiceWrappers: [], complaintsResolved: 0, averageResolutionTime: 0, complaintTypes: 0 };
    
    return {
      ...data,
      ServiceWrappers: data.ServiceWrappers || [],
      complaintsResolved: data.complaintsResolved || 0,
      averageResolutionTime: data.averageResolutionTime || 0,
      complaintTypes: data.complaintTypes || 0
    };
  };
  
  const { isLoading, error, data } = useQuery(
    ["searchPGRAI", tenantId, filters, auth, config], 
    () => Digit.PGRAIService.search(tenantId, filters), 
    {
      // select: defaultSelect,
      ...config,
    }
  );

  return { 
    isLoading, 
    error, 
    data: data || { ServiceWrappers: [], complaintsResolved: 0, averageResolutionTime: 0, complaintTypes: 0 }, 
    revalidate: () => client.invalidateQueries(["searchPGRAI", tenantId, filters, auth])
  };
};

export default useSearchPGRAI;
