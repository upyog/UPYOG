import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPmaGet = (phmId, tenantId, isupdated, config = {}) => {
  //let Resp=useQuery(["WMS_SEARCH", searchparams, tenantId, filters, isupdated], () => WMSService.PMAApplications.search(tenantId, filters, searchparams), config);
  //isLoading: hookLoading, isError, error, data, ...rest
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_PMA_GET",pmaId, tenantId, isupdated],
    // async () => await WMSService.PMAApplications.get(phmId, tenantId),
    async () => await WMSService.PMAApplications.get(),
    config
  );
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_PMA_GET", pmaId, tenantId]) };
};

export default useWmsPmaGet;
