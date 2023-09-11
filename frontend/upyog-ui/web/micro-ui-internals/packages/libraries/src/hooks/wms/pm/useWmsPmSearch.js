import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPmSearch = (searchparams, tenantId, filters, isupdated, config = {}) => {
  return useQuery(["WMS_PM_SEARCH", searchparams, tenantId, filters, isupdated], () => WMSService.PMApplications.search(tenantId, filters, searchparams), config);
};

export default useWmsPmSearch;
