import { useQuery, useQueryClient } from "react-query";
import WmsService from "../../../services/elements/WMS";

export const useWmsWsrCount = (tenantId, config = {}) => {
  const client = useQueryClient();
  const { isLoading, Errors, data } = useQuery(
    ["WMS_WSR_COUNT",tenantId],
    async () => await WmsService.WSRApplications.count(tenantId),
    config
  );//isLoading: isLoading, Errors, data: res 
  return { isLoading, Errors, data, revalidate: () => client.invalidateQueries(["WMS_WSR_COUNT", tenantId]) };

};

export default useWmsWsrCount;
