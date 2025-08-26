import { useMutation } from "react-query";

import { ADSServices } from "../../services/elements/ADS"

/*Custom hook  for managing demand estimation actions
 * (either creation or updating) through ADS services*/

const useADSDemandEstimation =  (tenantId) => {
   
    return useMutation((data) => 
      ADSServices.estimateCreate(data, tenantId));
    
};
export default useADSDemandEstimation;