import { useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPmCount = (tenantId, config = {}) => {
  return useQuery(["WMS_PM_COUNT", tenantId], () => WMSService.PMApplications.count(tenantId), config);
};

export default useWmsPmCount;
