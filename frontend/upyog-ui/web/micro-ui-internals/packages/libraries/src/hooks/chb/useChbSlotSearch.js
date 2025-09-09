import { useQuery, useQueryClient } from "react-query";

/**
 * useChbSlotSearch Hook
 * 
 * This custom hook is responsible for searching available slots for CHB (Community Hall Booking) based on filters and tenant information.
 * 
 * Parameters:
 * - `tenantId`: The tenant ID for which the slot search is being performed.
 * - `filters`: Filters to apply for the slot search (e.g., dates, hall codes).
 * - `auth`: Authentication details for the API request.
 * - `config`: Optional configuration object for the `useQuery` hook.
 * 
 * Logic:
 * - Constructs the search arguments (`args`) using `tenantId`, `filters`, and `auth`.
 * - Uses the `useQuery` hook from `react-query` to fetch slot availability from `Digit.CHBServices.slot_search`.
 * - Disables automatic query execution by setting `enabled: false` in the query configuration.
 * - Provides a `revalidate` function to invalidate and refetch the query.
 * 
 * Returns:
 * - An object containing:
 *    - `isLoading`: Boolean indicating whether the query is in progress.
 *    - `error`: Error object if the query fails.
 *    - `data`: The fetched slot availability data.
 *    - `isSuccess`: Boolean indicating whether the query was successful.
 *    - `refetch`: Function to manually refetch the data.
 *    - `revalidate`: Function to invalidate and refetch the query.
 */
const useChbSlotSearch = ({ tenantId, filters, auth }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const { isLoading, error, data, isSuccess, refetch } = useQuery(
    ["chbSearchList", tenantId, filters, auth, config],
    () => Digit.CHBServices.slot_search(args),
    {
      ...config,
      enabled: false, // Disable automatic query execution
    }
  );

  return { isLoading, error, data, isSuccess, refetch, revalidate: () => client.invalidateQueries(["chbSearchList", tenantId, filters, auth]) };
};

export default useChbSlotSearch;
