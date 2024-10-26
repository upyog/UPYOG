import { useMutation } from "react-query";
import { SVService } from "../../services/elements/SV";

/**
 * Custom hook for create API for street vending
 It takes a tenantId and an optional type parameter. If type is true, it returns a mutation function 
 that calls the SVService.create method with the provided data and tenantId.
*/

export const useSvCreateApi = (tenantId, type = true) => {
  if (type) {
    return useMutation((data) => SVService.create(data, tenantId));
  } 
};

export default useSvCreateApi;
