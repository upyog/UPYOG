import { useQuery, useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsPrjUpdate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.ProjectApplications.update(data, tenantId));
};

export default useWmsPrjUpdate;
