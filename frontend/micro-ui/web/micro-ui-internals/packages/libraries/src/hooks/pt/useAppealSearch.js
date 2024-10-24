import { useQuery, useQueryClient } from "react-query";

const useAppealSearch = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };


  const { isLoading, error, data, isSuccess } = useQuery(["appealSearchList", tenantId, filters, auth, config], () => Digit.PTService.appealSearch(args));

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries(["appealSearchList", tenantId, filters, auth]) };
};

export default useAppealSearch;
