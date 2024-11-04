/**
 * Following hook is used to get data of applications from backend in Street Vending module and returns the data in an object "SVDetail"
 */

import { useQuery, useQueryClient } from "react-query";

const useSvSearchApplication = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  // Selects the data to be returned by the hook
  const defaultSelect = (data) => {
    if(data.SVDetail.length > 0)  data.SVDetail[0].applicationNo = data.SVDetail[0].applicationNo || [];
    return data;
  };

  const { isLoading, error, data, isSuccess } = useQuery(["streetVendingSearchList", tenantId, filters, auth, config], () => Digit.SVService.search(args), {
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries(["streetVendingSearchList", tenantId, filters, auth]) };
};

export default useSvSearchApplication;
