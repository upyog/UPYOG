import { useQuery, useMutation } from "react-query";
import { PTRService } from "../../services/elements/PTR";


export const usePTRCreateAPI = (tenantId, type = true) => {
 // return useMutation((data) => PTRService.create(data, tenantId));
  if (type) {
    return useMutation((data) => PTRService.create(data, tenantId));
  } 
  else {
    return useMutation((data) => PTRService.update(data, tenantId));
  }
};

export default usePTRCreateAPI;
