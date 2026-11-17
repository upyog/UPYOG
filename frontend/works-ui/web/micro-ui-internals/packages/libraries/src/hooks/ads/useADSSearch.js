import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";

const useADSSearch = (
  { tenantId, filters, auth },
  config = {}
) => {
  const client = useQueryClient();

  const args = tenantId
    ? { tenantId, filters, auth }
    : { filters, auth };

  const queryKey = [
    "ADS_SEARCH",
    tenantId,
    JSON.stringify(filters),
    auth,
  ];

  const queryFn = () => Digit.ADSServices.search(args);

  const select = (data) => {
    if (data?.bookingApplication?.length > 0) {
      data.bookingApplication[0].bookingNo =
        data.bookingApplication[0].bookingNo || [];
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

export default useADSSearch;