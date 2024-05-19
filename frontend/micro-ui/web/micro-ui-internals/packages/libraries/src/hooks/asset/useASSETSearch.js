import { useQuery, useQueryClient } from "react-query";

const useASSETSearch = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    console.log("assethook", data)
    if(data.Assets.length > 0)  data.Assets[0].applicationNo = data.Assets[0].applicationNo || [];
      
    return data;
  };

  const { isLoading, error, data, isSuccess } = useQuery(["assetSearchList", tenantId, filters, auth, config], () => Digit.ASSETService.search(args), {
    
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries(["assetSearchList", tenantId, filters, auth]) };
};

export default useASSETSearch;
