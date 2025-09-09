//TODO: Should Have to delete after 1 month of testing


// import { useQuery } from "react-query";

// /**
//  * useResidentType Hook
//  * 
//  * This custom hook is responsible for fetching resident type-related master data (MDMS) for the CHB (Community Hall Booking) module.
//  * 
//  * Parameters:
//  * - `tenantId`: The tenant ID for which the resident type data is being fetched.
//  * - `moduleCode`: The module code for CHB (e.g., "CHB").
//  * - `type`: The type of data to fetch (e.g., "ChbResidentType").
//  * - `config`: Optional configuration object for the `useQuery` hook.
//  * 
//  * Logic:
//  * - Uses the `useQuery` hook from `react-query` to fetch resident type data from MDMS.
//  * - Calls `Digit.Hooks.useSelectedMDMS().getMasterData` with appropriate parameters to fetch resident type data.
//  * - Returns the query object if `type` is "ChbResidentType", otherwise returns `null`.
//  * 
//  * Returns:
//  * - A query object from `react-query` containing the fetched resident type data, loading state, and error state.
//  */
// const useResidentType = (tenantId, moduleCode, type, config = {}) => {
//   const useResident = () => {
//     return useQuery("RESIDENT_TYPE", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "ResidentType", "CHB_RESIDENT_TYPE_", "i18nKey"), config);
//   };
  

//   switch (type) {
//     case "ChbResidentType":
//       return useResident();
//     default:
//       return null;
//   }
// };



// export default useResidentType;