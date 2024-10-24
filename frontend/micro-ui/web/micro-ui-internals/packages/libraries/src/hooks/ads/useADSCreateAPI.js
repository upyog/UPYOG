import { useQuery, useMutation } from "react-query";

import { ADSServices } from "../../services/elements/ADS"



export const useADSCreateAPI = (tenantId, type = true) => {
  
  if (type) {
   
    return useMutation((data) => 
      ADSServices.create(data, tenantId));
    
  } 
  else {
    return useMutation((data) => ADSServices.update(data, tenantId));
  }
};

export default useADSCreateAPI;
