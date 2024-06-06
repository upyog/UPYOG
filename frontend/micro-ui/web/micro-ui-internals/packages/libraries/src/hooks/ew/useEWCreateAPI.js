import { useQuery, useMutation } from "react-query";
import { EWService } from "../../services/elements/EW";


export const useEWCreateAPI = (tenantId, type = true) => {
 // return useMutation((data) => EWService.create(data, tenantId));
  if (type) {
    return useMutation((data) => EWService.create(data, tenantId));
  } 
  else {
    return useMutation((data) => EWService.update(data, tenantId));
  }
};

export default useEWCreateAPI;
