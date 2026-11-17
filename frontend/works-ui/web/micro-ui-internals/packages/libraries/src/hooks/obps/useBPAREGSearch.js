import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";

const useBPAREGSearch = (tenantId, filters, params, config = {}) => {
  const client = useQueryClient();

  const queryKey = [
    "OBPS_BPA_REG_SEARCH",
    tenantId,
    JSON.stringify(filters),
    JSON.stringify(params),
  ];

  const query = queryTemplate({
    queryKey,
    queryFn: () =>
      Digit.OBPSService.BPAREGSearch(tenantId, filters, params),
    config,
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useBPAREGSearch;