import { useQuery } from "react-query";
import { CNDService } from "../../services/elements/CND";

/**
 * Custom React hook to search for vendors using specified filters.
 * 
 * @param {Object} args - Arguments object
 * @param {string} args.tenantId - Tenant ID to identify the scope of the search
 * @param {Object} args.filters - Filters to be applied in the vendor search (e.g., name, type)
 * @param {Object} args.config - Optional configuration object for react-query (e.g., refetchInterval, enabled, etc.)
 * 
 * @returns {Object} - Result object from react-query containing data, error, loading state, etc.
 */

const useVendorSearch = (args) => {
  const { tenantId, filters, config } = args;
  return useQuery(["VENDOR_SEARCH", filters], () => CNDService.vendorSearch(tenantId, filters), config);
};

export default useVendorSearch;
