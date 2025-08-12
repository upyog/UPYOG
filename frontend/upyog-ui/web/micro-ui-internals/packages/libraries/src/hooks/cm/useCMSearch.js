import { useQuery, useQueryClient } from "react-query";

/**
 * Custom React Hook for performing a search operation using the React Query library.
 * 
 * This hook utilizes the `useQuery` hook from React Query to fetch data based on the provided
 * tenant ID, filters, and authentication parameters. It abstracts the logic for managing 
 * loading states, error handling, and data retrieval, making it easier to use in components.
 * 
 * @param {string} params.tenantId - The ID of the tenant for which the search is being performed.
 * @param {Object} params.filters - The filters to apply to the search query.
 * @param {Object} params.auth - The authentication details required for the search.
 * @param {Object} [config={}] - Optional configuration object for the `useQuery` hook.
 * 
 * @returns {Object} An object containing the following properties:
 *   - {boolean} isLoading - Indicates if the query is currently loading.
 *   - {Error} error - Contains any error that occurred during the query.
 *   - {any} data - The data returned from the search query.
 *   - {boolean} isSuccess - Indicates if the query was successful.
 *   - {function} refetch - A function to manually refetch the data.
 *   - {function} revalidate - A function to invalidate the cached query and trigger a refetch.
 * 
 * Usage:
 * const { isLoading, error, data, isSuccess, refetch, revalidate } = useCMSearch({
 *   tenantId: '123',
 *   filters: { status: 'active' },
 *   auth: { token: 'abc123' }
 * });
 * 
 * This hook will automatically fetch the search results based on the provided parameters
 * and manage the loading and error states for you.
 */

const useCMSearch = ({ tenantId, filters, auth}, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const { isLoading, error, data, isSuccess,refetch } = useQuery(["cmSearchList", tenantId, filters, auth, config], () => Digit.CMServices.search(args), {
      ...config,
  });

  return { isLoading, error, data, isSuccess,refetch, revalidate: () => client.invalidateQueries(["cmSearchList", tenantId, filters, auth]) };
};

export default useCMSearch;
