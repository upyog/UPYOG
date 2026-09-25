import { queryTemplate } from "../common/queryTemplate";

const useGetDSSFAQsJSON = (tenantId, config = {}) => {
  return queryTemplate({
    queryKey: ["DSS_FAQS", tenantId],
    queryFn: () => Digit.MDMSService.getDSSFAQsJSONData(tenantId),
    config,
  });
};

export default useGetDSSFAQsJSON;