/**
 * Following hook is used to get data of applications from backend in CND module and returns the data in an object "SVDetail"
 */

import { useQuery, useQueryClient } from "react-query";

const useCndSearchApplication = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  // Selects the data to be returned by the hook
  const defaultSelect = (data) => {
    if(data.cndApplicationDetail.length > 0)  data.cndApplicationDetail[0].cndApplicationDetail = data.cndApplicationDetail[0].cndApplicationDetail || [];
    return data;
  };

  const { isLoading, error, data, isSuccess } = useQuery(["cndSearchList", tenantId, filters, auth, config], () => Digit.CNDService.search(args), {
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries(["cndSearchList", tenantId, filters, auth]) };
};

export default useCndSearchApplication;
