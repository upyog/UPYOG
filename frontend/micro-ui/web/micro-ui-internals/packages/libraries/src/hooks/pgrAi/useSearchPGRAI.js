import { useQuery, useQueryClient } from "react-query";
/**
 * useSearchPGRAI Hook
 * 
 * This hook is used for making the search API call for PGR_AI.
 * 
 * Functions:
 * - useSearchPGRAI:
 *   - A custom hook that uses `react-query`'s `useQuery` to fetch search results for PGR_AI records.
 *   - Calls the `Digit.PGRAIService.search` method with the provided tenant ID and filters.
 * 
 * Parameters:
 * - tenantId: The tenant ID for which the search is performed.
 * - filters: An object containing the search filters.
 * - auth: Boolean to determine if authentication is required. Defaults to `true`.
 * - config: Optional configuration object for the `useQuery` hook.
 * 
 * Returns:
 * - isLoading: Boolean indicating if the query is in a loading state.
 * - error: Any error encountered during the query.
 * - data: The search results, including `ServiceWrappers` and other metadata.
 * - revalidate: A function to manually invalidate and refetch the query.
 * 
 * Usage:
 * - This hook simplifies the process of fetching search results for PGR_AI records.
 * - Provides default values for the search results to handle empty or missing data gracefully.
 */
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
