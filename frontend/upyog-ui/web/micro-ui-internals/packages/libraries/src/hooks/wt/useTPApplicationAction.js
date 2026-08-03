import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActionsTP from "../../services/molecules/WT/ApplicationUpdateActionTP";

/* Hook for Tree Pruning (TP) application updates.  
 * Uses `ApplicationUpdateActionsTP` to handle updates based on tenantId.  
 * Returns a object for triggering and managing the update process.  
 */
const useTPApplicationAction = (tenantId) => {
  return mutationTemplate({ mutationFn: (applicationData) => ApplicationUpdateActionsTP(applicationData, tenantId) });
};

export default useTPApplicationAction;
