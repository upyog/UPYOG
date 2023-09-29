import { useQuery, useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPhmUpdate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.PHMApplications.update(data, tenantId));
};

export default useWmsPhmUpdate;
