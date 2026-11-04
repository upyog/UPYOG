import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

/**
 * Custom hook for executing an water Tanker search using React Query.
 * 
 * This hook takes in tenant ID, filters, and authentication details to fetch
 * search results. It handles loading states and errors, and includes a
 * method to refetch data. The hook also allows for custom configurations 
 */
const useTankerSearchAPI = ({ tenantId, filters, auth }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if (data.waterTankerBookingDetail.length > 0) data.waterTankerBookingDetail[0].bookingNo = data.waterTankerBookingDetail[0].bookingNo || [];
    return data;
  };

  const { isLoading, error, data, isSuccess, refetch } = queryTemplate({ queryKey: ["wtSearchList", tenantId, filters, auth, config], queryFn: () => Digit.WTService.search(args), select: defaultSelect, config });

  return { isLoading, error, data, isSuccess, refetch, revalidate: () => client.invalidateQueries({ queryKey: ["wtSearchList", tenantId, filters, auth] }) };
};

export default useTankerSearchAPI;
