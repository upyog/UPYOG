/*
  Custom hook to fetch gender-related MDMS (Master Data Management System) data using react-query.

  Parameters:
  - tenantId: ID of the tenant.
  - moduleCode: Module code for fetching MDMS data.
  - type: Type of data to be fetched (e.g., GenderType).
  - config: Optional configuration for react-query.

  Returns:
  - Gender data from MDMS based on the provided type.
*/
import { useQuery } from "react-query";

const usePTRGenderMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePTRGenders = () => {
    return useQuery("PTR_GENDER_DETAILS", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "GenderType", "PTR_GENDER_", "i18nKey"), config);
  };
  

  switch (type) {
    case "GenderType":
      return usePTRGenders();
    default:
      return null;
  }
};



export default usePTRGenderMDMS;