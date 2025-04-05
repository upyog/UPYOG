import { useQuery, useQueryClient } from "react-query";

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
const useChbSearch = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if(data.hallsBookingApplication.length > 0)  data.hallsBookingApplication[0].applicationNo = data.hallsBookingApplication[0].applicationNo || [];
      
    return data;
  };

  const { isLoading, error, data, isSuccess,refetch } = useQuery(["chbSearchList", tenantId, filters, auth, config], () => Digit.CHBServices.search(args), {
    
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess,refetch, revalidate: () => client.invalidateQueries(["chbSearchList", tenantId, filters, auth]) };
};

export default useChbSearch;
