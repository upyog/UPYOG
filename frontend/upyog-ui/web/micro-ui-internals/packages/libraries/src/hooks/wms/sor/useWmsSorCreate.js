import { useQuery, useMutation } from "react-query";

import WMSService from "../../../services/elements/WMS";

export const useWmsSorCreate = (tenantId, config = {}) => {
  return useMutation((data) => WMSService.SORApplications.create(data, tenantId));
};

export default useWmsSorCreate;
