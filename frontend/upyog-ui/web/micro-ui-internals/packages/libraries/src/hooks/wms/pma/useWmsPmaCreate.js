import { useQuery, useMutation } from "react-query";

import WMSService from "../../../services/elements/WMS";

export const useWmsPmaCreate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.PMAApplications.create(data, tenantId));
};

export default useWmsPmaCreate;
