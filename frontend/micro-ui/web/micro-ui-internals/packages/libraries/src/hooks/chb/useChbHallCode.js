import { useQuery } from "react-query";

const useHallCode = (tenantId, moduleCode, type, config = {}) => {
  const usehallCode = () => {
    return useQuery("HALL_CODE", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "HallCode", "CHB_HALL_CODE_", "i18nKey"), config);
  };
  
  switch (type) {
    case "HallCode":
      return usehallCode();
    default:
      return null;
  }
};



export default useHallCode;