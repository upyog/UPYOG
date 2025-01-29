import { useQuery } from "react-query";

const useSpecialCategory = (tenantId, moduleCode, type, config = {}) => {
  const useSpecial = () => {
    return useQuery("SPECIAL_CATEGORY", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "SpecialCategory", "CHB_SPECIAL_CATEGORY_", "i18nKey"), config);
  };
  

  switch (type) {
    case "ChbSpecialCategory":
      return useSpecial();
    default:
      return null;
  }
};

export default useSpecialCategory;