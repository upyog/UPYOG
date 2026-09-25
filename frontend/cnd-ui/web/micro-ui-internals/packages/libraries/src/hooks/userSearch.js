import { queryTemplate } from "../common/queryTemplate";
import { useQueryClient } from "../common/queryClientTemplate";
import { UserService } from "../services/elements/User";

export const useUserSearch = (tenantId, data, filters, options = {}) => {
  const client = useQueryClient();
  const queryData = queryTemplate({ queryKey: ["USER_SEARCH", filters, data], queryFn: () => UserService.userSearch(tenantId, data, filters), config: options });
  return { ...queryData, revalidate: () => client.invalidateQueries({ queryKey: ["USER_SEARCH", filters, data] }) };
};
