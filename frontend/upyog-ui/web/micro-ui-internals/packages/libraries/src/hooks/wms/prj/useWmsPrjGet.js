import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPrjGet = (projectId, tenantId, isupdated, config = {}) => {
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_PRJ_GET",projectId, tenantId, isupdated],
    async () => await WMSService.ProjectApplications.get(projectId, tenantId),
    config
  );
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_PRJ_GET", projectId, tenantId]) };
};

export default useWmsPrjGet;
