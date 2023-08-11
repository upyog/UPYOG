import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsSorSearch = (searchparams, tenantId, filters, isupdated, config = {}) => {
  return useQuery(["HRMS_SEARCH", searchparams, tenantId, filters, isupdated], () => WMSService.SORApplications.search(tenantId, filters, searchparams), config);
};

export default useWmsSorSearch;
