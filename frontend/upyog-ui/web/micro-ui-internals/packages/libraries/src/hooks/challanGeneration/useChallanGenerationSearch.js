/**
 * Fetches challan generation search results based on filters.
 *
 * - Formats filter values (status, businessService) into comma-separated strings.
 * - Calls Digit.ChallanGenerationService.search API.
 * - Uses React Query for caching, fetching, and state management.
 * - Provides revalidate function to manually refresh cached data.
 *
 * @param {Object} input
 * @param {string} input.tenantId - Tenant identifier
 * @param {Object} input.filters - Search filters
 * @param {boolean} input.isChallanGenerationAppChanged - Trigger for refetch
 * @param {Object} config - Optional React Query config
 *
 * @returns {Object} { isLoading, error, data, revalidate }
 */

import { queryTemplate } from "../../common/queryTemplate";

const useChallanGenerationSearch = (
  { tenantId, filters, isChallanGenerationAppChanged },
  config = {}
) => {
  const formattedFilters = { ...filters };

  if (formattedFilters?.status?.length > 0)
    formattedFilters.status = formattedFilters.status.toString();
  else delete formattedFilters.status;

  if (formattedFilters?.businessService?.length > 0)
    formattedFilters.businessService =
      formattedFilters.businessService.toString();
  else delete formattedFilters.businessService;

  const args = tenantId
    ? { tenantId, filters: formattedFilters }
    : { filters: formattedFilters };

  return queryTemplate({
    queryKey: [
      "challanGenerationSearchList",
      tenantId,
      formattedFilters,
      isChallanGenerationAppChanged,
    ],

    queryFn: () => Digit.ChallanGenerationService.search(args),

    config,
  });
};

export default useChallanGenerationSearch;
