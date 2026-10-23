import { useMutation } from "@tanstack/react-query";
import ApplicationUpdateActionsSV from "../../services/molecules/SV/ApplicationUpdateActionsSV";

/** The following function is used for the mutation function */

const useSVApplicationAction = (tenantId) => {
  // Updated: TanStack Query v5 requires useMutation to accept an object with mutationFn key instead of a direct function
  return useMutation({
    mutationFn: (applicationData) => ApplicationUpdateActionsSV(applicationData, tenantId)
  });
};

export default useSVApplicationAction;
