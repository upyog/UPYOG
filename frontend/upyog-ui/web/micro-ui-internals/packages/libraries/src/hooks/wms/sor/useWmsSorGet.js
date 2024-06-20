import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsSorGet = (sorId, tenantId, isupdated, config = {}) => {
  //let Resp=useQuery(["WMS_SEARCH", searchparams, tenantId, filters, isupdated], () => WMSService.SORApplications.search(tenantId, filters, searchparams), config);
  //isLoading: hookLoading, isError, error, data, ...rest
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_GET",sorId, tenantId, isupdated],
    async () => await WMSService.SORApplications.get(sorId, tenantId),
    config
  );
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_GET", sorId, tenantId]) };
};

export default useWmsSorGet;
