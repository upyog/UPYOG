import { useQuery, useMutation } from "react-query";
import { ASSETService } from"../../services/elements/ASSET"



export const useReturnAPI = (tenantId, type = true) => {
  if (type) {
    return useMutation((data) => ASSETService.return_asset(data, tenantId));
  } 
  else {
    return useMutation((data) => ASSETService.update(data, tenantId));
  }
};

export default useReturnAPI;
