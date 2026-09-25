import { queryTemplate } from "../common/queryTemplate";

const useGetHowItWorksJSON = (tenantId) => {
    return queryTemplate({ queryKey: ["HOW_IT_WORKS", tenantId], queryFn: () => Digit.MDMSService.getHowItWorksJSONData(tenantId) });
  };

export default useGetHowItWorksJSON;