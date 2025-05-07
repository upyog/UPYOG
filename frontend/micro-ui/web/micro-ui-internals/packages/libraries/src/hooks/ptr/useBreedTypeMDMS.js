// /*
// Custom hook to fetch Breed Type master data from MDMS using react-query
// Parameters:
//  - tenantId: ID of the tenant (location-specific context)
//  - moduleCode: MDMS module code
//  - type: The type of data to fetch (e.g., "BreedType")
//  - config: Optional configuration for react-query
// Returns:
//  - Query result object containing MDMS breed type data or null if type is invalid
// */
// import { useQuery } from "react-query";

// const useBreedTypeMDMS = (tenantId, moduleCode, type,  config = {}) => {
//   const useBreed = () => {
//     return useQuery("PTR_FORM_BREED_TYPE", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "BreedType", "PTR_BREED_TYPE_", "i18nKey"), config);
//   };
  

//   switch (type) {
//     case "BreedType":
//       return useBreed();
//     default:
//       return null;
//   }
// };



// export default useBreedTypeMDMS;