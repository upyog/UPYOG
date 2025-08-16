import { EWSearch } from "../../services/molecules/EW/Search";
import { useQuery } from "react-query";

/**
 * Custom hook for fetching and managing E-Waste application details.
 * Provides data fetching, caching, and transformation capabilities for application information.
 *
 * @param {Function} t Translation function for localization
 * @param {string} tenantId Tenant/city identifier
 * @param {string} requestId Unique application request identifier
 * @param {Object} config Additional configuration options for the query
 * @param {string} userType Type of user accessing the details (citizen/employee)
 * @param {Object} args Additional arguments for the search
 * @returns {Object} Query result containing application data, loading state, and error information
 * 
 * @example
 * const { data, isLoading } = useEwApplicationDetail(
 *   t,
 *   "pb.amritsar",
 *   "EW-2023-01-29-000123",
 *   { refetchOnWindowFocus: false },
 *   "EMPLOYEE"
 * );
 */
const useEwApplicationDetail = (t, tenantId, requestId, config = {}, userType, args) => {
  /**
   * Processes and transforms the fetched application data
   * @param {Object} data Raw application data from API
   * @returns {Object} Transformed application data
   */
  const defaultSelect = (data) => {
    let applicationDetails = data.applicationDetails.map((obj) => {
      return obj;
    });

    return {
      applicationData: data,
      applicationDetails,
    };
  };

  return useQuery(
    ["APP_SEARCH", "EW_SEARCH", requestId, userType, args],
    () => EWSearch.applicationDetails(t, tenantId, requestId, userType, args),
    { select: defaultSelect, ...config }
  );
};

export default useEwApplicationDetail;