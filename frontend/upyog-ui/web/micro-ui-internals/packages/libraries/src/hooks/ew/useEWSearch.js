import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";

const useEWSearch = (
  { tenantId, filters, auth, searchedFrom = "" },
  config = {}
) => {
  const client = useQueryClient();

  const args = tenantId
    ? { tenantId, filters, auth }
    : { filters, auth };

  // ⚠️ FIXED: removed config from key, stabilized filters
  const queryKey = [
    "EW_SEARCH",
    tenantId,
    JSON.stringify(filters),
    auth,
  ];

  const queryFn = () => Digit.EwService.search(args);

  const select = (data) => {
    if (data?.EwasteApplication?.length > 0) {
      data.EwasteApplication[0] =
        data.EwasteApplication[0] || [];
    }
    return data;
  };

  const query = queryTemplate({
    queryKey,
    queryFn,
    select,
    config,
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useEWSearch;