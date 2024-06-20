import { useQuery, useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPrUpdate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.DRApplications.update(data, tenantId));
};

export default useWmsPrUpdate;
