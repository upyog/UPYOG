import { useQuery, useMutation } from "react-query";

import WMSService from "../../../services/elements/WMS";

export const useWmsSchCreate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.SCHApplications.create(data, tenantId));
};

export default useWmsSchCreate;
