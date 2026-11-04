import { queryTemplate } from "../common/queryTemplate";

const useGetDSSAboutJSON = (tenantId, config = {}) => {
  return queryTemplate({
    queryKey: ["DSS_ABOUT", tenantId],
    queryFn: () => Digit.MDMSService.getDSSAboutJSONData(tenantId),
    config,
  });
};

export default useGetDSSAboutJSON;