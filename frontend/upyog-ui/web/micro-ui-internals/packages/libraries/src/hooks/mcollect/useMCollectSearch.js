import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";

const useMCollectSearch = (
  { tenantId, filters, isMcollectAppChanged },
  config = {}
) => {
  const client = useQueryClient();

  // ⚠️ DO NOT mutate original filters
  const finalFilters = { ...filters };

  if (finalFilters.status?.length > 0) {
    finalFilters.status = finalFilters.status.toString();
  } else {
    delete finalFilters.status;
  }

  if (finalFilters.businessService?.length > 0) {
    finalFilters.businessService = finalFilters.businessService.toString();
  } else {
    delete finalFilters.businessService;
  }

  const args = tenantId
    ? { tenantId, filters: finalFilters }
    : { filters: finalFilters };

  const queryKey = [
    "MCOLLECT_SEARCH",
    tenantId,
    JSON.stringify(finalFilters),
    isMcollectAppChanged,
  ];

  const query = queryTemplate({
    queryKey,
    queryFn: () => Digit.MCollectService.search(args),
    config,
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useMCollectSearch;