import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPhmGet = (phmId, tenantId, isupdated, config = {}) => {
  //let Resp=useQuery(["WMS_SEARCH", searchparams, tenantId, filters, isupdated], () => WMSService.PHMApplications.search(tenantId, filters, searchparams), config);
  //isLoading: hookLoading, isError, error, data, ...rest
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_PHM_GET",phmId, tenantId, isupdated],
    async () => await WMSService.PHMApplications.get(),
    // async () => await WMSService.PHMApplications.get(phmId, tenantId),
    config
  );
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_PHM_GET", phmId, tenantId]) };
};

export default useWmsPhmGet;
