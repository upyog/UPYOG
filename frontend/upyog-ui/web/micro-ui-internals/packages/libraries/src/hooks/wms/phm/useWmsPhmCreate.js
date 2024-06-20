import { useQuery, useMutation } from "react-query";

import WMSService from "../../../services/elements/WMS";

export const useWmsPhmCreate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.PHMApplications.create(data, tenantId));
};

export default useWmsPhmCreate;
