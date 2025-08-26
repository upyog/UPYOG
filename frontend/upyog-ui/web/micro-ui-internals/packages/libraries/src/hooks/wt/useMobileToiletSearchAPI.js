import { useQuery, useQueryClient } from "react-query";

/**
 * Custom hook for executing an water Tanker search using React Query.
 * 
 * This hook takes in tenant ID, filters, and authentication details to fetch
 * search results. It handles loading states and errors, and includes a
 * method to refetch data. The hook also allows for custom configurations 
 */
const useMobileToiletSearchAPI = ({ tenantId, filters, auth }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if(data.mobileToiletBookingDetails.length >0)  data.mobileToiletBookingDetails[0].bookingNo = data.mobileToiletBookingDetails[0].bookingNo || [];
    return data;
  };

  const { isLoading, error, data, isSuccess,refetch } = useQuery([tenantId, filters, auth, config], () => Digit.MTService.search(args), {
    
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess,refetch, revalidate: () => client.invalidateQueries([tenantId, filters, auth]) };
};

export default useMobileToiletSearchAPI;
