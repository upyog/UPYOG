//TODO: Should Have to delete after 1 month of testing

// import { useQuery } from "react-query";

// /**
//  * useChbCommunityHalls Hook
//  * 
//  * This custom hook is responsible for fetching the list of community halls for the CHB (Community Hall Booking) module.
//  * It retrieves master data for community halls from the MDMS (Master Data Management System) using the `useQuery` hook.
//  * 
//  * Parameters:
//  * - `tenantId`: The tenant ID for which the community halls are being fetched.
//  * - `moduleCode`: The module code for CHB (e.g., "CHB").
//  * - `type`: The type of data to fetch (currently unused in the implementation).
//  * - `config`: Optional configuration object for the `useQuery` hook (e.g., for caching, refetching).
//  * 
//  * Logic:
//  * - Uses the `useQuery` hook from `react-query` to fetch community hall data.
//  * - The query key is "COMMUNITY_HALLS", ensuring that the data is cached and reused across components.
//  * - Calls the `Digit.Hooks.useSelectedMDMS().getMasterData` function to fetch master data for community halls.
//  *    - Parameters for `getMasterData`:
//  *        - `tenantId`: The tenant ID for the request.
//  *        - `moduleCode`: The module code for CHB.
//  *        - `"CommunityHalls"`: The master data name to fetch.
//  *        - `"CHB_COMMUNITY_HALLS_"`: The prefix for the i18n keys.
//  *        - `"i18nKey"`: The key used for internationalization.
//  * - Passes the `config` object to the `useQuery` hook for additional customization.
//  * 
//  * Returns:
//  * - A query object from `react-query` that includes:
//  *    - `data`: The fetched community hall data.
//  *    - `isLoading`: Boolean indicating whether the query is in progress.
//  *    - `isError`: Boolean indicating whether the query resulted in an error.
//  *    - `refetch`: Function to manually refetch the data.
//  * 
//  * Usage:
//  * - This hook can be used in components to fetch and display a list of community halls for the CHB module.
//  * - Example:
//  *    const { data, isLoading, isError } = useChbCommunityHalls(tenantId, "CHB", "CommunityHalls", config);
//  */
// const useChbCommunityHalls = (tenantId, moduleCode, type, config = {}) => {
//   const useCommunityHalls = () => {
//     return useQuery("COMMUNITY_HALLS", () => Digit.Hooks.useSelectedMDMS(moduleCode).getMasterData(tenantId, moduleCode, "CommunityHalls", "CHB_COMMUNITY_HALLS_", "i18nKey"), config);
//   };
//   return useCommunityHalls();
//  };
// export default useChbCommunityHalls;