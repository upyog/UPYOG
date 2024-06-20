import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsWsrGet = (prId, tenantId, isupdated, config = {}) => {
  //let Resp=useQuery(["WMS_SEARCH", searchparams, tenantId, filters, isupdated], () => WMSService.WSRApplications.search(tenantId, filters, searchparams), config);
  //isLoading: hookLoading, isError, error, data, ...rest
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_WSR_GET",prId, tenantId, isupdated],
    // async () => await WMSService.WSRApplications.get(prId, tenantId),
    async () => await WMSService.WSRApplications.get(),
    config
  );
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_WSR_GET", prId, tenantId]) };
};

export default useWmsWsrGet;
