import { useMutation } from "react-query";
import ApplicationUpdateActionsSV from "../../services/molecules/SV/ApplicationUpdateActionsSV";

/** The following function is used for the mutation function */

const useSVApplicationAction = (tenantId) => {
  
  return useMutation((applicationData) => ApplicationUpdateActionsSV(applicationData, tenantId));
};

export default useSVApplicationAction;
