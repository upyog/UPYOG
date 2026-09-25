import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";

/**
 * useServiceSearchCHB Hook
 * 
 * This custom hook is responsible for searching services in the CHB (Community Hall Booking) module based on tenant and filter criteria.
 * 
 * Parameters:
 * - `tenantId`: The tenant ID for which the service search is being performed.
 * - `filters`: Filters to apply for the service search (e.g., reference IDs, attributes).
 * - `config`: Optional configuration object for the `useQuery` hook.
 * 
 * Logic:
 * - Constructs the search arguments (`serviceSearchArg`) using `tenantId` and `filters`.
 * - Uses the `useQuery` hook from `react-query` to fetch service data from `Digit.PTService.cfsearch`.
 * - Configures the query with caching disabled (`cacheTime: 0`) for real-time results.
 * - Provides a `revalidate` function to invalidate and refetch the query.
 * 
 * Returns:
 * - An object containing:
 *    - `isLoading`: Boolean indicating whether the query is in progress.
 *    - `error`: Error object if the query fails.
 *    - `data`: The fetched service data.
 *    - `revalidate`: Function to invalidate and refetch the query.
 */

const useServiceSearchCHB = (
  { tenantId, filters },
  config = {}
) => {
  const client = useQueryClient();

  const serviceSearchArg = {
    filters: {
      ServiceCriteria: {
        tenantId: filters?.serviceSearchArgs?.tenantId,
        referenceIds: filters?.serviceSearchArgs?.referenceIds,
      },
    },
  };

  const queryKey = [
    "CHB_SERVICE_SEARCH",
    tenantId,
    JSON.stringify(filters),
  ];

  const queryFn = () =>
    Digit.PTService.cfsearch(serviceSearchArg);

  const query = queryTemplate({
    queryKey,
    queryFn,
    config: { ...config, cacheTime: 0 },
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useServiceSearchCHB;