//TODO: Should Have to delete after 1 month of testing


// import { useQuery } from "react-query";

// /**
//  * useSpecialCategory Hook
//  * 
//  * This custom hook is responsible for fetching special category-related master data (MDMS) for the CHB (Community Hall Booking) module.
//  * 
//  * Parameters:
//  * - `tenantId`: The tenant ID for which the special category data is being fetched.
//  * - `moduleCode`: The module code for CHB (e.g., "CHB").
//  * - `type`: The type of data to fetch (e.g., "ChbSpecialCategory").
//  * - `config`: Optional configuration object for the `useQuery` hook.
//  * 
//  * Logic:
//  * - Uses the `useQuery` hook from `react-query` to fetch special category data from MDMS.
//  * - Calls `Digit.Hooks.useSelectedMDMS().getMasterData` with appropriate parameters to fetch the data.
//  * - Returns the query object if `type` is "ChbSpecialCategory", otherwise returns `null`.
//  * 
//  * Returns:
//  * - A query object from `react-query` containing the fetched special category data, loading state, and error state.
//  */
// const useSpecialCategory = (tenantId, moduleCode, type, config = {}) => {
//   const useSpecial = () => {
//     return useQuery("SPECIAL_CATEGORY", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "SpecialCategory", "CHB_SPECIAL_CATEGORY_", "i18nKey"), config);
//   };
  

//   switch (type) {
//     case "ChbSpecialCategory":
//       return useSpecial();
//     default:
//       return null;
//   }
// };

// export default useSpecialCategory;