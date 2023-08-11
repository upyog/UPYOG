import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsSorCount = (tenantId, config = {}) => {
  return useQuery(["HRMS_COUNT", tenantId], () => WMSService.SORApplications.count(tenantId), config);
};

export default useWmsSorCount;
