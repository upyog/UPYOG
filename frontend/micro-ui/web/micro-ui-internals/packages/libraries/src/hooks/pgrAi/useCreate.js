import { useQuery, useMutation } from "react-query";

import { PGRAIService } from "../../services/elements/PGRAI";

/**
 * useCreate Hook
 * 
 * A custom hook for calling the PGR_AI create or update API.
 *  If `type` is true, it calls `PGRAIService.create` to create a record.
 * Parameters:
 * tenantId: Tenant ID for the operation.
 * type: Boolean to determine create (`true`) or update (`false`). Defaults to `true`.
 * Returns:
 * A useMutation instance for managing the API call.
 */
export const useCreate = (tenantId, type = true) => {
    // Return mutation for create or update based on the 'type' parameter
  if (type) {
   
    return useMutation((data) => 
      PGRAIService.create(data, tenantId));
    
  } 
  else {
    return useMutation((data) => PGRAIService.update(data, tenantId));
  }
};

export default useCreate;
