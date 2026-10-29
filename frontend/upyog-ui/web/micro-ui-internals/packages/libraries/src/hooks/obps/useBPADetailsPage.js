import { queryTemplate } from "../../common/queryTemplate";

const useBPADetailsPage = (tenantId, filters, config = {}) => {
  return queryTemplate({
    queryKey: ["OBPS_BPA_DETAILS_PAGE", tenantId, JSON.stringify(filters)],
    queryFn: () =>
      Digit.OBPSService.BPADetailsPage(tenantId, filters),
    config,
  });
};

export default useBPADetailsPage;