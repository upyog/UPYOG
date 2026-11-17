import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";
import ReceiptsService from "../../services/elements/Receipts";

export const useReceiptsSearch = (searchparams, tenantId, filters, isupdated, config = {}) => {
  const client = useQueryClient();
  const businessService = searchparams?.businessServices;
  const { isLoading, error, data, ...rest } = queryTemplate({ queryKey: ["RECEIPTS_SEARCH", searchparams, tenantId, filters, isupdated], queryFn: () => ReceiptsService.search(tenantId, filters, searchparams, businessService), config });
  return { isLoading, error, data, revalidate: () => client.invalidateQueries({ queryKey: ["RECEIPTS_SEARCH", searchparams, tenantId, filters, isupdated] }), ...rest };
};

export default useReceiptsSearch;
