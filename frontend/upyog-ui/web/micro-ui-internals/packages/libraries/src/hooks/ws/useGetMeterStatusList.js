import { queryTemplate } from "../../common/queryTemplate";

const useGetMeterStatusList = (tenantId) => {
    return queryTemplate({ queryKey: ["getMeterStatus", tenantId], queryFn: () => Digit.MDMSService.getMeterStatusType(tenantId) });
  };

export default useGetMeterStatusList;