import { queryTemplate } from "../../common/queryTemplate";

const useChallanGenerationCount = (tenantId, config = {}) => {
  return queryTemplate({
    queryKey: ["ChallanGeneration_COUNT", tenantId],

    queryFn: () => Digit.ChallanGenerationService.count(tenantId),

    config,
  });
};


/**
 * Fetches challan generation count for a given tenant using React Query.
 *
 * - Calls Digit.ChallanGenerationService.count API.
 * - Uses tenantId in query key for caching and refetching.
 *
 * @param {string} tenantId - Tenant identifier
 * @param {Object} config - Optional React Query config
 *
 * @returns {Object} Query result (data, isLoading, error, etc.)
 */

export default useChallanGenerationCount;
