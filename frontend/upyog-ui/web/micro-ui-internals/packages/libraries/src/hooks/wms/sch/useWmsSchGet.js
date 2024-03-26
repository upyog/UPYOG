import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsSchGet = (schId, tenantId, isupdated, config = {}) => {
  //let Resp=useQuery(["WMS_SEARCH", searchparams, tenantId, filters, isupdated], () => WMSService.SCHApplications.search(tenantId, filters, searchparams), config);
  //isLoading: hookLoading, isError, error, data, ...rest
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_SCH_GET",schId, tenantId, isupdated],
    async () => await WMSService.SCHApplications.get(schId, tenantId),
    config
  );
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_SCH_GET", schId, tenantId]) };
};

export default useWmsSchGet;
