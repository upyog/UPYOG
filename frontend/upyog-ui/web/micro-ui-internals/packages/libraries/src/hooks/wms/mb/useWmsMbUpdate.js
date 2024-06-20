import { useQuery, useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsMbUpdate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.MBApplications.update(data, tenantId));
};

export default useWmsMbUpdate;
