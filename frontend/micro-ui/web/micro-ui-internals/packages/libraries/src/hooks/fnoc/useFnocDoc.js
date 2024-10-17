import { MdmsService } from "../../services/elements/MDMS";
import { useQuery } from "react-query";

const useFnocDoc = (tenantId, moduleCode, type, config = {}) => {
  const useFnocDocument = () => {
    return useQuery("FNOC_DOCUMENT_REQ_SCREEN", () => MdmsService.getFNOCDocuments(tenantId, moduleCode), config);
  };
  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsService.getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    case "Documents":
      return useFnocDocument();
    default:
      return _default();
  }
};
export default useFnocDoc;
