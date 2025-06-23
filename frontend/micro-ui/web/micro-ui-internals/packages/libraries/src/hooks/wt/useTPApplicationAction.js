import { useMutation } from "react-query";
import ApplicationUpdateActionsTP from "../../services/molecules/WT/ApplicationUpdateActionTP";

/* Hook for Tree Pruning (TP) application updates.  
 * Uses `ApplicationUpdateActionsTP` to handle updates based on tenantId.  
 * Returns a object for triggering and managing the update process.  
 */
const useTPApplicationAction = (tenantId) => {
  return useMutation((applicationData) => ApplicationUpdateActionsTP(applicationData, tenantId));
};

export default useTPApplicationAction;
