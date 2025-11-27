import { useQuery, useMutation } from "react-query";

import { ASSETService } from "../../services/elements/ASSET";

export const useProcerementCreateAPI = (tenantId, type = true) => {
  if (type) {
    return useMutation((data) => ASSETService.createProcurement(data, tenantId));
  } else {
    return useMutation((data) => ASSETService.updateProcurement(data, tenantId));
  }
};

export default useProcerementCreateAPI;
