import { useQuery } from "@tanstack/react-query";

const useStaticData = (tenantId) => {
    return useQuery({
        queryKey: ["MODULE_LEVEL_HOME_PAGE_STATIC_DATA", tenantId],
        queryFn: () => Digit.MDMSService.getStaticDataJSON(tenantId)
    });
};

export default useStaticData;