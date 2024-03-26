import { useQuery, useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsWsrUpdate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.WSRApplications.update(data, tenantId));
};

export default useWmsWsrUpdate;
