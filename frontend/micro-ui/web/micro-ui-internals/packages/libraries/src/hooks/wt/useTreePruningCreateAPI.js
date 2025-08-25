import { useMutation } from "react-query";
import { TPService } from "../../services/elements/TP";
export const useTreePruningCreateAPI = (tenantId, type = true) => {
 
  if (type) {
    return useMutation((data) => TPService.create(data, tenantId));
  } 
  else {
    return useMutation((data) => TPService.update(data, tenantId));
  }
};

export default useTreePruningCreateAPI;
