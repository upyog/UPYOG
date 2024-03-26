import { useQuery, useQueryClient } from "react-query";
import WmsService from "../../../services/elements/WMS";

export const useWmsMbCount = (tenantId, config = {}) => {
  const client = useQueryClient();
  const { isLoading, Errors, data } = useQuery(
    ["WMS_MB_COUNT",tenantId],
    async () => await WmsService.MBApplications.count(tenantId),
    config
  );//isLoading: isLoading, Errors, data: res 
  return { isLoading, Errors, data, revalidate: () => client.invalidateQueries(["WMS_MB_COUNT", tenantId]) };

};

export default useWmsMbCount;
