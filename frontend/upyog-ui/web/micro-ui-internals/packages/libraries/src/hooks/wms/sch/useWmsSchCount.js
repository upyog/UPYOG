import { useQuery, useQueryClient } from "react-query";
import WmsService from "../../../services/elements/WMS";

export const useWmsSchCount = (tenantId, config = {}) => {
  const client = useQueryClient();
  const { isLoading, Errors, data } = useQuery(
    ["WMS_SCH_COUNT",tenantId],
    async () => await WmsService.SCHApplications.count(tenantId),
    config
  );//isLoading: isLoading, Errors, data: res 
  return { isLoading, Errors, data, revalidate: () => client.invalidateQueries(["WMS_SCH_COUNT", tenantId]) };

};

export default useWmsSchCount;
