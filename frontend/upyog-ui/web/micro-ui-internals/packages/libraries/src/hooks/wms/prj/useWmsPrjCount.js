import { useQuery, useQueryClient } from "react-query";
import WmsService from "../../../services/elements/WMS";

export const useWmsPrjCount = (tenantId, config = {}) => {
  return useQuery(["WMS_PRJ_COUNT", tenantId], () => WmsService.ProjectApplications.count(tenantId), config);
};

export default useWmsPrjCount;
