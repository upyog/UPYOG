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