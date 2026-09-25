import { queryTemplate } from "../../common/queryTemplate";

/**
 * Custom Hook: useChallanGenerationRecieptSearch
 *
 * This hook is responsible for fetching challan receipt data using React Query.
 * It abstracts the API call logic and provides built-in support for caching,
 * background fetching, and state management (loading, error, success).
 *
 * Functionality:
 * - Accepts a tenantId and additional search parameters to filter receipt data.
 * - Calls the backend service (Digit.ChallanGenerationService.recieptSearch)
 *   to retrieve receipt details.
 * - Uses a unique query key to cache results and automatically refetch data
 *   when tenantId or search parameters change.
 * - Prevents unnecessary API calls on component remount using `refetchOnMount: false`.
 * - Allows custom React Query configurations (like enabled, retry, staleTime, etc.)
 *   through the optional `config` parameter.
 *
 * Parameters:
 * @param {Object} input
 * @param {string} input.tenantId - Unique tenant identifier (e.g., city/ULB)
 * @param {Object} input.params - Additional filters for receipt search
 *
 * @param {Object} config - Optional React Query configuration overrides
 *
 * Returns:
 * - React Query response object containing:
 *   data: API response data
 *   isLoading: loading state
 *   isError: error state
 *   error: error details
 *   refetch: function to manually refetch data
 *
 * Note:
 * - The query key ensures proper caching and re-fetching behavior.
 * - Avoid passing unstable objects in the query key to prevent unnecessary re-renders.
 */

export const useChallanGenerationRecieptSearch = (
  { tenantId, ...params },
  config = {}
) => {
  return queryTemplate({
    queryKey: ["challanGenerationReceiptSearch", tenantId, params],

    queryFn: () =>
      Digit.ChallanGenerationService.recieptSearch(tenantId, params),

    config: {
      refetchOnMount: false,
      ...config,
    },
  });
};
