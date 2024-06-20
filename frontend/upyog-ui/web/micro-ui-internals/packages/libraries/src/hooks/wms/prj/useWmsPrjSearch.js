import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPrjSearch = (searchparams, tenantId, filters, isupdated, config = {}) => {
  const client = useQueryClient();
  const { isLoading, error, data } = useQuery(
    ["WMS_PRJ_SEARCH",searchparams, tenantId, filters, isupdated],
    async () => await WMSService.ProjectApplications.search(tenantId, filters, searchparams),
    config
  );
  return { isLoading, error, data, revalidate: () => client.invalidateQueries(["WMS_PRJ_SEARCH", tenantId,filters, searchparams]) };;
};

export default useWmsPrjSearch;
