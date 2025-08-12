import { useMutation } from "react-query";

import { CHBServices } from "../../services/elements/CHB"



export const useChbCreateAPI = (tenantId, type = true) => {
  
  if (type) {
   
    return useMutation((data) => 
      CHBServices.create(data, tenantId));
    
  } 
  // If type is false, return mutation for update
    return useMutation((data) => CHBServices.update(data, tenantId));
};

export default useChbCreateAPI;
