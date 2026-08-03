import { useQuery, useQueryClient } from "@tanstack/react-query";
import BillingService from "../../services/elements/Bill";

const useBillSearch = ({ filters, config = {} }) => {
  const client = useQueryClient();
  const tenantId = Digit.SessionStorage.get("User")?.info?.tenantId;

  filters.locality = filters.locality?.map((element) => {
    return element.code;
  });
  filters.url = filters.url?.replace("egov-searcher", "");

  const args = tenantId ? { tenantId, filters } : { filters };
  // Updated: TanStack Query v5 requires useQuery to accept a single object instead of positional arguments.
  // Updated: queryKey and queryFn are now explicit keys inside the object — positional args removed.
  const { isLoading, error, data } = useQuery({
    queryKey: ["BILL_INBOX", tenantId, filters],
    queryFn: async () => await BillingService.search_bill(args),
    ...config,
    enabled: filters?.businesService ? true : false,
  });
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["BILL_INBOX", tenantId, filters]) };
};

export default useBillSearch;
