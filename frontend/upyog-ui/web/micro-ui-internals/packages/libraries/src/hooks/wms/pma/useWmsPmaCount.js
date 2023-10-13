import { useQuery, useQueryClient } from "react-query";
import WmsService from "../../../services/elements/WMS";

export const useWmsPmaCount = (tenantId, config = {}) => {
  const client = useQueryClient();
  const { isLoading, Errors, data } = useQuery(
    ["WMS_PMA_COUNT",tenantId],
    async () => await WmsService.PMAApplications.count(tenantId),
    config
  );//isLoading: isLoading, Errors, data: res
  return { isLoading, Errors, data, revalidate: () => client.invalidateQueries(["WMS_PMA_COUNT", tenantId]) };

};

export default useWmsPmaCount;
