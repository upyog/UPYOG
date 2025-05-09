import { useQuery, useMutation } from "react-query";

import { PGRAIService } from "../../services/elements/PGRAI";

// Custom hook for creating or updating PGR_AI r
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
