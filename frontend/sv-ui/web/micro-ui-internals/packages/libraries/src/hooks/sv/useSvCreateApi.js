import { useMutation } from "@tanstack/react-query";
import { SVService } from "../../services/elements/SV";

/**
 * Custom hook for create API for street vending
 It takes a tenantId and an optional type parameter. If type is true, it returns a mutation function 
 that calls the SVService.create method with the provided data and tenantId.
*/

// Updated: TanStack Query v5 requires useMutation to accept an object with mutationFn key instead of a direct function
export const useSvCreateApi = (tenantId, type = true) => {
  if (type) {
    return useMutation({
      mutationFn: (data) => SVService.create(data, tenantId)
    });
  } else {
    return useMutation({
      mutationFn: (data) => SVService.update(data, tenantId)
    });
  }
};

export default useSvCreateApi;
