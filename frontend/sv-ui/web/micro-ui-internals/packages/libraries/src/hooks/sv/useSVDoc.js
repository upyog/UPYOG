import { useQuery } from "@tanstack/react-query";

const useSVDoc = (tenantId, moduleCode, type, config = {}) => {
    // Updated: TanStack Query v5 requires useQuery to accept a single object instead of positional arguments.
    // Updated: queryKey and queryFn are now explicit keys inside the object — positional args removed.
    return useQuery({
        queryKey: ["SV_DOCUMENT_REQ_SCREEN"],
        queryFn: () => Digit.Hooks.useSelectedMDMS(moduleCode).getMasterData(tenantId, moduleCode, "Documents"),
        ...config
    });
};

export default useSVDoc;
