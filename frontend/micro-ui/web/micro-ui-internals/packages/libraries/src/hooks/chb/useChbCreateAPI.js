import { useQuery, useMutation } from "react-query";

import { CHBServices } from "../../services/elements/CHB"



export const useChbCreateAPI = (tenantId, type = true) => {
 // return useMutation((data) => PTRService.create(data, tenantId));
  if (type) {
    return useMutation((data) => CHBServices.create(data, tenantId));
  } 
  else {
    return useMutation((data) => CHBServices.update(data, tenantId));
  }
};

export default useChbCreateAPI;
