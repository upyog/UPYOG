import { useQuery, useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsSchUpdate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.SCHApplications.update(data, tenantId));
};

export default useWmsSchUpdate;
