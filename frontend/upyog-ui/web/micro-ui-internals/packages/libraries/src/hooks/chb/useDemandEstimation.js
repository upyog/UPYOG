import { useMutation } from "react-query";

import { CHBServices } from "../../services/elements/CHB"


const useDemandEstimation =  (tenantId) => {
   
    return useMutation((data) => 
      CHBServices.estimateCreate(data, tenantId));

};
export default useDemandEstimation;
