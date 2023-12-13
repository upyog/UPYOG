import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsDrSearch = (tenantId, searchparams,  filters, isupdated, config = {}) => {
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_DR_SEARCH",tenantId,searchparams,filters, isupdated],
    async () => await WMSService.DRApplications.search(tenantId, searchparams, filters),
    config
  );//isLoading: hookLoading, isError, error, data, ...rest
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_DR_SEARCH", tenantId,searchparams,filters]) };;
};

export default useWmsDrSearch;
