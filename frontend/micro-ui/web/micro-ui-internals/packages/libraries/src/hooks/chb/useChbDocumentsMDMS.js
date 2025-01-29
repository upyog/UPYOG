import { useQuery } from "react-query";

const useChbDocumentsMDMS = (tenantId, moduleCode, type, config = {}) => {

  const useChbDocumentsRequiredScreen = () => {
    return useQuery("CHB_DOCUMENT_REQ_SCREEN", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "Documents"), config);
  };

  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => Digit.Hooks.useSelectedMDMS().getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useChbDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useChbDocumentsMDMS;
