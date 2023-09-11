import { useQuery, useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPmUpdate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.PMApplications.update(data, tenantId));
};

export default useWmsPmUpdate;
