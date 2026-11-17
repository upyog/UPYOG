import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";
import BillingService from "../../services/elements/Bill";

const useBillSearch = ({ filters, config = {} }) => {
  const client = useQueryClient();

  const tenantId =
    Digit.SessionStorage.get("User")?.info?.tenantId;

  // ✅ DO NOT mutate original filters
  const updatedFilters = {
    ...filters,
    locality: filters?.locality?.map((el) => el.code),
    url: filters?.url?.replace("egov-searcher", ""),
  };

  const args = tenantId
    ? { tenantId, filters: updatedFilters }
    : { filters: updatedFilters };

  const queryKey = [
    "BILL_INBOX",
    tenantId,
    JSON.stringify(updatedFilters),
  ];

  const queryFn = () => BillingService.search_bill(args);

  const enabled = !!filters?.businesService;

  const query = queryTemplate({
    queryKey,
    queryFn,
    enabled,
    config,
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useBillSearch;