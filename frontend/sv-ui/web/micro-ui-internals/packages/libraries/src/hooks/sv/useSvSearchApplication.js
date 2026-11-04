/**
 * Following hook is used to get data of applications from backend in Street Vending module and returns the data in an object "SVDetail"
 */

import { useQuery, useQueryClient } from "@tanstack/react-query";

const useSvSearchApplication = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  // Selects the data to be returned by the hook
  const defaultSelect = (data) => {
    // if(data.SVDetail.length > 0)
    return data;
  };
// Updated: TanStack Query v5 requires useQuery to accept a single object instead of positional arguments.
// Updated: queryKey and queryFn are now explicit keys inside the object — positional args removed.
  const { isLoading, error, data, isSuccess } = useQuery({
    queryKey: ["streetVendingSearchList", tenantId, filters, auth, config],
    queryFn: () => Digit.SVService.search(args),
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries({ queryKey: ["streetVendingSearchList", tenantId, filters, auth] }) };
};

export default useSvSearchApplication;
