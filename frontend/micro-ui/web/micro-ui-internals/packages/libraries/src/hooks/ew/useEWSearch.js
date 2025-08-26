import { useQuery, useQueryClient } from "react-query";

/**
 * Custom hook for searching and filtering E-Waste applications.
 * Provides data fetching, caching, and revalidation capabilities.
 *
 * @param {Object} params Search parameters and configuration
 * @param {string} params.tenantId Tenant/city identifier
 * @param {Object} params.filters Search filters (status, date range, etc.)
 * @param {Object} params.auth Authentication details
 * @param {string} params.searchedFrom Source of search request
 * @param {Object} config Additional react-query configuration options
 * @returns {Object} Query result with data, loading state, and revalidation function
 *
 * @example
 * const { data, isLoading, revalidate } = useEWSearch({
 *   tenantId: "pb.amritsar",
 *   filters: { status: "PENDING" }
 * });
 */
const useEWSearch = ({ tenantId, filters, auth, searchedFrom = "" }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  /**
   * Transforms raw API response data into required format
   * @param {Object} data Raw response data
   * @returns {Object} Processed application data
   */
  const defaultSelect = (data) => {
    if (data.EwasteApplication.length > 0) data.EwasteApplication[0] = data.EwasteApplication[0] || [];
    return data;
  };

  const { isLoading, error, data, isSuccess } = useQuery(
    ["ewSearchList", tenantId, filters, auth, config],
    () => Digit.EwService.search(args),
    {
      select: defaultSelect,
      ...config,
    }
  );

  return {
    isLoading,
    error,
    data,
    isSuccess,
    revalidate: () => client.invalidateQueries(["ewSearchList", tenantId, filters, auth]),
  };
};

export default useEWSearch;