import { useQuery, useMutation } from "react-query";

import WMSService from "../../../services/elements/WMS";

export const useWmsMbCreate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.MBApplications.create(data, tenantId));
};

export default useWmsMbCreate;
