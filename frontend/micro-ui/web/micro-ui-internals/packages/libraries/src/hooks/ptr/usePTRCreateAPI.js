/*

created by - Shivank

*/

import { useQuery, useMutation } from "react-query";
import { PTRService } from "../../services/elements/PTR";


export const usePTRCreateAPI = (tenantId, shivank = true) => {
 // return useMutation((data) => PTRService.create(data, tenantId));
  if (shivank) {
    return useMutation((data) => PTRService.create(data, tenantId));
  } 
  else {
    return useMutation((data) => PTRService.update(data, tenantId));
  }
};

export default usePTRCreateAPI;
