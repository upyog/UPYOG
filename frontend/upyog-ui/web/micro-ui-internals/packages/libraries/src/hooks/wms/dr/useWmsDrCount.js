import { useQuery, useQueryClient } from "react-query";
import WmsService from "../../../services/elements/WMS";

export const useWmsDrCount = (tenantId, config = {}) => {
  const client = useQueryClient();
  const { isLoading, Errors, data } = useQuery(
    ["WMS_DR_COUNT",tenantId],
    async () => await WmsService.DRApplications.count(tenantId),
    config
  );//isLoading: isLoading, Errors, data: res 
  return { isLoading, Errors, data, revalidate: () => client.invalidateQueries(["WMS_DR_COUNT", tenantId]) };

};

export default useWmsDrCount;
