import { useQuery, useMutation } from "react-query";

import WMSService from "../../../services/elements/WMS";

export const useWmsDrCreate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.DRApplications.create(data, tenantId));
};

export default useWmsDrCreate;
