import { useQuery, useMutation } from "react-query";

import WMSService from "../../../services/elements/WMS";

export const useWmsPrjCreate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.ProjectApplications.create(data, tenantId));
};

export default useWmsPrjCreate;
