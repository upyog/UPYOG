import { useQuery, useMutation } from "react-query";
import { EwService } from "../../services/elements/EW";


export const useEwCreateAPI = (tenantId, type = true) => {
 // return useMutation((data) => EWService.create(data, tenantId));
  if (type) {
    return useMutation((data) => EwService.create(data, tenantId));
  } 
  else {
    return useMutation((data) => EwService.update(data, tenantId));
  }
};

export default useEwCreateAPI;
