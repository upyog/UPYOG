import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsDrGet = (phmId, tenantId, isupdated, config = {}) => {
  //let Resp=useQuery(["WMS_SEARCH", searchparams, tenantId, filters, isupdated], () => WMSService.DRApplications.search(tenantId, filters, searchparams), config);
  //isLoading: hookLoading, isError, error, data, ...rest
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_DR_GET",phmId, tenantId, isupdated],
    async () => await WMSService.DRApplications.get(phmId, tenantId),
    config
  );
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_DR_GET", phmId, tenantId]) };
};

export default useWmsDrGet;
