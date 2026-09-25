import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";

const useMcollectSearchBill = (
  { tenantId, filters },
  config = {}
) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters } : { filters };

  const queryKey = [
    "MCOLLECT_BILL_SEARCH",
    tenantId,
    JSON.stringify(filters),
  ];

  const query = queryTemplate({
    queryKey,
    queryFn: () => Digit.MCollectService.search_bill(args),
    config,
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useMcollectSearchBill;