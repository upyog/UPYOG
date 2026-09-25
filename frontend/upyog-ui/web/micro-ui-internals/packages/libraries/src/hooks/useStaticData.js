import { queryTemplate } from "../common/queryTemplate";

const useStaticData = (tenantId) => {
    return queryTemplate({ queryKey: ["MODULE_LEVEL_HOME_PAGE_STATIC_DATA", tenantId], queryFn: () => Digit.MDMSService.getStaticDataJSON(tenantId) });
  };

export default useStaticData;