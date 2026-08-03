/**
 * Fetches challan bill search results using React Query.
 *
 * - Calls Digit.ChallanGenerationService.search_bill API.
 * - Uses tenantId and filters for query key (caching & refetching).
 * - Provides revalidate function to manually refresh data.
 *
 * @param {Object} input
 * @param {string} input.tenantId - Tenant identifier
 * @param {Object} input.filters - Search filters
 * @param {Object} config - Optional React Query config
 *
 * @returns {Object} { isLoading, error, data, revalidate }
 */

import { queryTemplate } from "../../common/queryTemplate";

const useChallanGenerationSearchBill = (
  { tenantId, filters },
  config = {}
) => {
  const args = tenantId ? { tenantId, filters } : { filters };

  return queryTemplate({
    queryKey: ["billSearchList", tenantId, filters],

    queryFn: () => Digit.ChallanGenerationService.search_bill(args),

    config,
  });
};

export default useChallanGenerationSearchBill;
