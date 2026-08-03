import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";

const useASSETSearch = (
  { tenantId, filters, auth },
  config = {}
) => {
  const client = useQueryClient();

  const args = tenantId
    ? { tenantId, filters, auth }
    : { filters, auth };

  const queryKey = [
    "ASSET_SEARCH",
    tenantId,
    JSON.stringify(filters),
    auth,
  ];

  const queryFn = () => Digit.ASSETService.search(args);

  const select = (data) => {
    if (data?.Assets?.length > 0) {
      data.Assets[0].applicationNo =
        data.Assets[0].applicationNo || [];
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

export default useASSETSearch;