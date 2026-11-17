import { queryTemplate } from "../common/queryTemplate";

const useGetFAQsJSON = (tenantId) => {
    return queryTemplate({ queryKey: ["FAQ_S", tenantId], queryFn: () => Digit.MDMSService.getFAQsJSONData(tenantId) });
  };

export default useGetFAQsJSON;