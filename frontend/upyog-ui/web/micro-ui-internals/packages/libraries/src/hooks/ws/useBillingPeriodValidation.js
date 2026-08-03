import { queryTemplate } from "../../common/queryTemplate";

const useGetBillingPeriodValidation = (tenantId) => {
    return queryTemplate({ queryKey: ["getBillingPeriod", tenantId], queryFn: () => Digit.MDMSService.getBillingPeriod(tenantId) });
  };

export default useGetBillingPeriodValidation;