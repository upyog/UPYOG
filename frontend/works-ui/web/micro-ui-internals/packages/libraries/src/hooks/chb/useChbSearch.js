import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";

/**
 * useChbSearch Hook
 * 
 * This custom hook is responsible for searching CHB (Community Hall Booking) applications based on filters and tenant information.
 * 
 * Logic:
 * - Constructs the search arguments (`args`) using `tenantId`, `filters`, and `auth`.
 * - Uses the `useQuery` hook from `react-query` to fetch search results from `Digit.CHBServices.search`.
 * - Applies a `defaultSelect` function to process the fetched data:
 *    - Ensures `applicationNo` is initialized for the first application in the results.
 * - Provides a `revalidate` function to invalidate and refetch the query.
 * 
 * Returns:
 * - An object containing:
 *    - `isLoading`: Boolean indicating whether the query is in progress.
 *    - `error`: Error object if the query fails.
 *    - `data`: The processed search results.
 *    - `isSuccess`: Boolean indicating whether the query was successful.
 *    - `refetch`: Function to manually refetch the data.
 *    - `revalidate`: Function to invalidate and refetch the query.
 */
const useChbSearch = (
  { tenantId, filters, auth },
  config = {}
) => {
  const client = useQueryClient();

  const args = tenantId
    ? { tenantId, filters, auth }
    : { filters, auth };

  const queryKey = [
    "CHB_SEARCH",
    tenantId,
    JSON.stringify(filters),
    auth,
  ];

  const queryFn = () => Digit.CHBServices.search(args);

  const select = (data) => {
    if (data?.hallsBookingApplication?.length > 0) {
      data.hallsBookingApplication[0].applicationNo =
        data.hallsBookingApplication[0].applicationNo || [];
    }
    return data;
  };

  const query = queryTemplate({
    queryKey,
    queryFn,
    select,
    config,
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useChbSearch;