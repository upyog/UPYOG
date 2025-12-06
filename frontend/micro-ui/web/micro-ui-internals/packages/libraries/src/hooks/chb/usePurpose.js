//TODO: Should Have to delete after 1 month of testing

// import { useQuery } from "react-query";

// /**
//  * usePurpose Hook
//  * 
//  * This custom hook is responsible for fetching purpose-related master data (MDMS) for the CHB (Community Hall Booking) module.
//  * 
//  * Parameters:
//  * - `tenantId`: The tenant ID for which the purpose data is being fetched.
//  * - `moduleCode`: The module code for CHB (e.g., "CHB").
//  * - `type`: The type of data to fetch (e.g., "ChbPurpose").
//  * - `config`: Optional configuration object for the `useQuery` hook.
//  * 
//  * Logic:
//  * - Uses the `useQuery` hook from `react-query` to fetch purpose data from MDMS.
//  * - Calls `Digit.Hooks.useSelectedMDMS().getMasterData` with appropriate parameters to fetch purpose data.
//  * - Returns the query object if `type` is "ChbPurpose", otherwise returns `null`.
//  * 
//  * Returns:
//  * - A query object from `react-query` containing the fetched purpose data, loading state, and error state.
//  */
// const usePurpose = (tenantId, moduleCode, type, config = {}) => {
//   const usepurpose = () => {
//     return useQuery("PURPOSE", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "Purpose", "CHB_PURPOSE_", "i18nKey"), config);
//   };
  

//   switch (type) {
//     case "ChbPurpose":
//       return usepurpose();
//     default:
//       return null;
//   }
// };
// export default usePurpose;