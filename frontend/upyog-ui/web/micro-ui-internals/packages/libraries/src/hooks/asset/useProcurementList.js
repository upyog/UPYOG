
import { useQuery, useQueryClient } from "react-query";

const useProcurementList = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };
 
  const defaultSelect = (data) => {
    if(data.ProcurementRequests.length > 0)  data.ProcurementRequests[0].requestId = data.ProcurementRequests[0].requestId || [];
    return data;
  };

  const { isLoading, error, data, isSuccess } = useQuery(["procurementList", tenantId, filters, auth, config], () => Digit.ASSETService.procurementList(args), {
    
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries(["procurementList", tenantId, filters, auth]) };
};

export default useProcurementList;
