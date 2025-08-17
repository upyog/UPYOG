import { useQuery, useMutation } from "react-query";

import { ADSServices } from "../../services/elements/ADS"

// Custom hook for creating ADS resources using react-query
const useADSSlotSearch =  (tenantId, type = true) => {
   // Return mutation for create based on the 'type' parameter
  if (type) {
   
    return useMutation((data) => 
      ADSServices.slot_search(data, tenantId));
    
  } 
  else {
    return useMutation((data) => ADSServices.update(data, tenantId));
  }
};
export default useADSSlotSearch;
