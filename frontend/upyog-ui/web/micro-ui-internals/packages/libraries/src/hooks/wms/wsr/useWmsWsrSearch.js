import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsWsrSearch = (tenantId, searchparams,  filters, isupdated, config = {}) => {
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_WSR_SEARCH",tenantId,searchparams,filters, isupdated],
    async () => await WMSService.WSRApplications.search(tenantId, searchparams, filters),
    config
  );//isLoading: hookLoading, isError, error, data, ...rest
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_WSR_SEARCH", tenantId,searchparams,filters]) };;
};

export default useWmsWsrSearch;
