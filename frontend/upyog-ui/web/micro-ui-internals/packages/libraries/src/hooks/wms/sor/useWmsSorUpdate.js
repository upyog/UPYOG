import { useQuery, useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

export const useWmsSorUpdate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.SORApplications.update(data, tenantId));
};

export default useWmsSorUpdate;
