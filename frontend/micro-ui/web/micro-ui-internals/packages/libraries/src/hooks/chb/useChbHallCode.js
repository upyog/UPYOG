
//TODO: Should Have to delete after 1 month of testing


// import { useQuery } from "react-query";

// /**
//  * useHallCode Hook
//  * 
//  * This custom hook is responsible for fetching hall codes for the CHB (Community Hall Booking) module.
//  * 
//  * Parameters:
//  * - `tenantId`: The tenant ID for which the hall codes are being fetched.
//  * - `moduleCode`: The module code for CHB (e.g., "CHB").
//  * - `type`: The type of data to fetch (e.g., "HallCode").
//  * - `config`: Optional configuration object for the `useQuery` hook.
//  * 
//  * Logic:
//  * - Uses the `useQuery` hook from `react-query` to fetch hall codes from MDMS.
//  * - Calls `Digit.Hooks.useSelectedMDMS().getMasterData` with the appropriate parameters to fetch hall codes.
//  * - Returns the query object if `type` is "HallCode", otherwise returns `null`.
//  * 
//  * Returns:
//  * - A query object from `react-query` containing the fetched hall codes, loading state, and error state.
//  */
// const useHallCode = (tenantId, moduleCode, type, config = {}) => {
//   const usehallCode = () => {
//     return useQuery("HALL_CODE", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "HallCode", "CHB_HALL_CODE_", "i18nKey"), config);
//   };
  
//   switch (type) {
//     case "HallCode":
//       return usehallCode();
//     default:
//       return null;
//   }
// };



// export default useHallCode;