import { useQuery, useMutation } from "react-query";

import { CHBServices } from "../../services/elements/CHB"

/**
 * useDemandEstimation Hook
 * 
 * This custom hook is responsible for handling demand estimation and updates for CHB (Community Hall Booking) applications.
 * 
 * Parameters:
 * - `tenantId`: The tenant ID for which the operation is being performed.
 * - `type`: Boolean indicating the operation type (true for demand estimation creation, false for updates).
 * 
 * Logic:
 * - Uses the `useMutation` hook from `react-query` to perform API calls.
 * - Calls `CHBServices.estimateCreate` for creating demand estimations when `type` is true.
 * - Calls `CHBServices.update` for updating demand estimations when `type` is false.
 * 
 * Returns:
 * - A mutation object from `react-query` for performing the create or update operation.
 */
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
