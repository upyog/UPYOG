// /*
//   Custom hook to fetch pet-related MDMS (Master Data Management System) data using react-query.

//   Parameters:
//   - tenantId: ID of the tenant.
//   - moduleCode: Module code for fetching MDMS data.
//   - type: Type of data to be fetched (e.g., PetType).
//   - config: Optional configuration for react-query.

//   Returns:
//   - Pet data from MDMS based on the provided type.
// */
// import { useQuery } from "react-query";

// const usePTRPetMDMS = (tenantId, moduleCode, type, config = {}) => {
//   const usePTRPet = () => {
//     return useQuery("PTR_FORM_PET_TYPE", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "PetType", "PTR_PET_TYPE_", "i18nKey"), config);
//   };
  

//   switch (type) {
//     case "PetType":
//       return usePTRPet();
//     default:
//       return null;
//   }
// };



// export default usePTRPetMDMS;