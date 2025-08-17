import { useQuery, useMutation } from "react-query";

import { ADSServices } from "../../services/elements/ADS"


// Custom hook for creating or updating ADS resources using react-query
export const useADSCreateAPI = (tenantId, type = true) => {
    // Return mutation for create or update based on the 'type' parameter
  if (type) {
   
    return useMutation((data) => 
      ADSServices.create(data, tenantId));
    
  } 
  else {
    return useMutation((data) => ADSServices.update(data, tenantId));
  }
};

export default useADSCreateAPI;
