import { useQuery } from "react-query";

/**
 * Custom React Hook: useEnabledMDMS
 * 
 * This hook fetches MDMS (Master Data Management System) data using `react-query` 
 * to enable caching, automatic refetching, and efficient state management.
 * 
 * @param {string} tenantId - The tenant ID for which MDMS data is being fetched.
 * @param {string} moduleName - The name of the MDMS module that contains the required data.
 * @param {Array} [masterDetails=[]] - An array of master details specifying which data to retrieve.
 * @param {Object} [config={}] - Optional configuration object for `react-query` (e.g., caching, refetching behavior).
 * 
 * @returns {Object} - Returns the `react-query` object containing the fetched data, loading state, and error state.
 * 
 * **Implementation Details:**
 * - The query key is an array `[tenantId, moduleName, masterDetails]` to ensure data is cached uniquely 
 *   based on these parameters.
 * - The data fetching function retrieves MDMS data using `Digit.Hooks.useSelectedMDMS()`, 
 *   which is expected to return the appropriate MDMS service reference.
 * - The function `getMultipleTypesWithFilter` is used to fetch multiple types of master data with applied filters.
 * - The optional `config` object allows customization of `react-query` behavior.
 */

const useEnabledMDMS = (tenantId, moduleName, masterDetails = [], config = {}) => {
    return useQuery([tenantId, moduleName, masterDetails], () => Digit.Hooks.useSelectedMDMS().getMultipleTypesWithFilter(tenantId, moduleName, masterDetails), config);
};


export default useEnabledMDMS;