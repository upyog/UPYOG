import { useQuery, useMutation } from "react-query";

import { ADSServices } from "../../services/elements/ADS"

/*Custom hook  for managing demand estimation actions
 * (either creation or updating) through ADS services*/

const useADSDemandEstimation =  (tenantId, type = true) => {
  
  if (type) {
   
    return useMutation((data) => 
      ADSServices.estimateCreate(data, tenantId));
    
  } 
  else {
    return useMutation((data) => ADSServices.update(data, tenantId));
  }
};
export default useADSDemandEstimation;
