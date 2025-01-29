import { useQuery } from "react-query";

const usePurpose = (tenantId, moduleCode, type, config = {}) => {
  const usepurpose = () => {
    return useQuery("PURPOSE", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "Purpose", "CHB_PURPOSE_", "i18nKey"), config);
  };
  

  switch (type) {
    case "ChbPurpose":
      return usepurpose();
    default:
      return null;
  }
};
export default usePurpose;