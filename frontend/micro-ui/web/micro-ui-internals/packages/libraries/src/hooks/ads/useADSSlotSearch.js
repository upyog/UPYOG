import { useQuery, useMutation } from "react-query";

import { ADSServices } from "../../services/elements/ADS"


const useADSSlotSearch =  (tenantId, type = true) => {
  
  if (type) {
   
    return useMutation((data) => 
      ADSServices.slot_search(data, tenantId));
    
  } 
  else {
    return useMutation((data) => ADSServices.update(data, tenantId));
  }
};
export default useADSSlotSearch;
