import { useQuery } from "react-query";
import { CNDService } from "../../services/elements/CND";

/**
 * Custom React hook to search for vehicles using provided filters.
 * 
 * @param {Object} args - Arguments object
 * @param {string} args.tenantId - Tenant ID for the search
 * @param {Object} args.filters - Filters to apply in the vehicle search
 * @param {Object} args.config - Optional react-query configuration (e.g., cacheTime, staleTime, etc.)
 * 
 * @returns {Object} - React Query's result object containing loading state, data, error, etc.
 */

const useVehiclesSearch = (args) => {
  const { tenantId, filters, config } = args;
  return useQuery(["VEICLES_SEARCH", filters], () => CNDService.vehiclesSearch(tenantId, filters), config);
};

export default useVehiclesSearch;
