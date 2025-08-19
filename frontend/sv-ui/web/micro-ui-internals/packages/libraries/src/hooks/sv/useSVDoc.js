import { useQuery } from "react-query";

const useSVDoc = (tenantId, moduleCode, type, config = {}) => {
    return useQuery("SV_DOCUMENT_REQ_SCREEN", () => Digit.Hooks.useSelectedMDMS(moduleCode).getMasterData(tenantId, moduleCode, "Documents"), config);
};

export default useSVDoc;
