import { useQuery, useQueryClient } from "@tanstack/react-query";
import { UserService } from "../services/elements/User";

export const useUserSearch = (tenantId, data, filters, options = {}) => {
  const client = useQueryClient();
  const queryData = useQuery({
    queryKey: ["USER_SEARCH", filters, data],
    queryFn: () => UserService.userSearch(tenantId, data, filters),
    ...options
  });
  return { ...queryData, revalidate: () => client.invalidateQueries({ queryKey: ["USER_SEARCH", filters, data] }) };
};
