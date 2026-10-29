import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";

const useSearchPGRAI = (
  { tenantId, filters, auth = true },
  config = {}
) => {
  const client = useQueryClient();

  const queryKey = [
    "PGRAI_SEARCH",
    tenantId,
    JSON.stringify(filters),
    auth,
  ];

  const query = queryTemplate({
    queryKey,
    queryFn: () =>
      Digit.PGRAIService.search(tenantId, filters),
    config,
  });

  return {
    ...query,
    data:
      query.data || {
        ServiceWrappers: [],
        complaintsResolved: 0,
        averageResolutionTime: 0,
        complaintTypes: 0,
      },
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useSearchPGRAI;