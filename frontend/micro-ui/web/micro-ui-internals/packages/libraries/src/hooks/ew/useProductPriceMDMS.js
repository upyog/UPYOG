// import { useQuery } from "react-query";

// /**
//  * Custom hook to fetch product pricing data from the Master Data Management System.
//  * Handles caching and data fetching for E-Waste product prices.
//  *
//  * @param {string} tenantId Tenant/city identifier
//  * @param {string} moduleCode Module identifier (e.g., "EW")
//  * @param {string} type Type of data to fetch (e.g., "ProductName")
//  * @param {Object} config Additional react-query configuration options
//  * @returns {Object|null} Query result containing product price data or null if type is unsupported
//  *
//  * @example
//  * const { data, isLoading } = useProductPriceMDMS(
//  *   "pb.amritsar",
//  *   "EW",
//  *   "ProductName",
//  *   { refetchOnWindowFocus: false }
//  * );
//  */
// const useProductPriceMDMS = (tenantId, moduleCode, type, config = {}) => {
//   const useProductPrices = () => {
//     return useQuery(
//       "PRODUCT_PRICES",
//       () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "ProductName"),
//       config
//     );
//   };

//   switch (type) {
//     case "ProductName":
//       return useProductPrices();
//     default:
//       return null;
//   }
// };

// export default useProductPriceMDMS;