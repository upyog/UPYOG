import { useMutation } from "react-query";
import ApplicationUpdateActionsMT from "../../services/molecules/WT/ApplicationUpdateActionMT";

/* Hook for Mobile Toilet (MT) application updates.  
 * Uses `ApplicationUpdateActionsMT` to handle updates based on tenantId.  
 * Returns a object for triggering and managing the update process.  
 */
const useMTApplicationAction = (tenantId) => {
  return useMutation((applicationData) => ApplicationUpdateActionsMT(applicationData, tenantId));
};

export default useMTApplicationAction;
