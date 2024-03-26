import { useQuery, useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPmaUpdate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.PMAApplications.update(data, tenantId));
};

export default useWmsPmaUpdate;
