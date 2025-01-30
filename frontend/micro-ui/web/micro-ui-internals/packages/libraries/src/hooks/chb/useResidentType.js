import { useQuery } from "react-query";

const useResidentType = (tenantId, moduleCode, type, config = {}) => {
  const useResident = () => {
    return useQuery("RESIDENT_TYPE", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "ResidentType", "CHB_RESIDENT_TYPE_", "i18nKey"), config);
  };
  

  switch (type) {
    case "ChbResidentType":
      return useResident();
    default:
      return null;
  }
};



export default useResidentType;