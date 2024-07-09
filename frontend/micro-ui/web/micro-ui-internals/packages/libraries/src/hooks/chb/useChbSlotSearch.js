import { useQuery, useQueryClient } from "react-query";

const useChbSlotSearch = ({ tenantId, filters, auth }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const { isLoading, error, data, isSuccess, refetch } = useQuery(
    ["chbSearchList", tenantId, filters, auth, config],
    () => Digit.CHBServices.slot_search(args),
    {
      ...config,
      enabled: false, // Disable automatic query execution
    }
  );

  return { isLoading, error, data, isSuccess, refetch, revalidate: () => client.invalidateQueries(["chbSearchList", tenantId, filters, auth]) };
};

export default useChbSlotSearch;
