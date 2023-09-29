import { useQuery, useQueryClient } from "react-query";
import WmsService from "../../../services/elements/WMS";

export const useWmsPhmCount = (tenantId, config = {}) => {
  const client = useQueryClient();
  const { isLoading, Errors, data } = useQuery(
    ["WMS_PHM_COUNT",tenantId],
    async () => await WmsService.PHMApplications.count(tenantId).PHMApplications,
    config
  );//isLoading: isLoading, Errors, data: res 
  return { isLoading, Errors, data, revalidate: () => client.invalidateQueries(["WMS_PHM_COUNT", tenantId]) };

};

export default useWmsPhmCount;
