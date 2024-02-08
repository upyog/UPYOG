import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPrGet = (prId, tenantId, isupdated, config = {}) => {
  //let Resp=useQuery(["WMS_SEARCH", searchparams, tenantId, filters, isupdated], () => WMSService.PRApplications.search(tenantId, filters, searchparams), config);
  //isLoading: hookLoading, isError, error, data, ...rest
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_PR_GET",prId, tenantId, isupdated],
    // async () => await WMSService.PRApplications.get(prId, tenantId),
    async () => await WMSService.PRApplications.get(),
    config
  );
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_PR_GET", prId, tenantId]) };
};

export default useWmsPrGet;
