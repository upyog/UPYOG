import { useQuery, useMutation } from "react-query";

import { ASSETService } from "../../services/elements/ASSET"


// Create and Update Api for Asset Registry
export const useAssetCreateAPI = (tenantId, type = true) => {
  if (type) {
    return useMutation((data) => ASSETService.create(data, tenantId));
  } 
  else {
    return useMutation((data) => ASSETService.update(data, tenantId));
  }
};

export default useAssetCreateAPI;
