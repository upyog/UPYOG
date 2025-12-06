import { useQuery, useQueryClient } from "react-query";

/**
 * Custom hook for executing an ADS search using React Query.
 * 
 * This hook takes in tenant ID, filters, and authentication details to fetch
 * search results. It handles loading states and errors, and includes a
 * method to refetch data. The hook also allows for custom configurations 
 */
const useADSSearch = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if(data.bookingApplication.length > 0)  data.bookingApplication[0].bookingNo = data.bookingApplication[0].bookingNo || [];
      
    return data;
  };

  const { isLoading, error, data, isSuccess,refetch } = useQuery(["adsSearchList", tenantId, filters, auth, config], () => Digit.ADSServices.search(args), {
    
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess,refetch, revalidate: () => client.invalidateQueries(["adsSearchList", tenantId, filters, auth]) };
};

export default useADSSearch;
