import { useQuery, useMutation } from "react-query";
import { ADSServices } from "../../services/elements/ADS"

// Custom hook for creating ADS resources using react-query
const useADSSlotSearch =  (tenantId) => {
    return useMutation((data) => 
      ADSServices.slot_search(data, tenantId));
    
};
export default useADSSlotSearch;