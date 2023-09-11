import { useQuery, useMutation } from "react-query";

import WMSService from "../../../services/elements/WMS";

export const useWmsPmCreate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.PMApplications.create(data, tenantId));
};

export default useWmsPmCreate;
