import { useQuery, useQueryClient } from "react-query";

const useEWSearch = ({ tenantId, filters, auth, searchedFrom="" }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };
  const defaultSelect = (data) => {
    if(data.EwasteApplication.length > 0)  data.EwasteApplication[0] = data.EwasteApplication[0] || [];
    return data;
  };

  const { isLoading, error, data, isSuccess } = useQuery(["ewSearchList", tenantId, filters, auth, config], () => Digit.EwService.search(args), {
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries(["ewSearchList", tenantId, filters, auth]) };
};

export default useEWSearch;
