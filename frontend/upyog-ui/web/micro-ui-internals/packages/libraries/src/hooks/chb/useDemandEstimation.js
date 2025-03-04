import { useQuery, useMutation } from "react-query";

import { CHBServices } from "../../services/elements/CHB"


const useDemandEstimation =  (tenantId, type = true) => {
  
  if (type) {
   
    return useMutation((data) => 
      CHBServices.estimateCreate(data, tenantId));
    
  } 
  else {
    return useMutation((data) => CHBServices.update(data, tenantId));
  }
};
export default useDemandEstimation;
