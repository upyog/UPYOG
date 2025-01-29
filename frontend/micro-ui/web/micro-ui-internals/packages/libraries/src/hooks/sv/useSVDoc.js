import { useQuery } from "react-query";

const useSVDoc = (tenantId, moduleCode, type, config = {}) => {
  
  const useSVDocumentsRequiredScreen = () => {
    return useQuery("SV_DOCUMENT_REQ_SCREEN", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "Documents"), config);
  };
  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => Digit.Hooks.useSelectedMDMS().getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useSVDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useSVDoc;
