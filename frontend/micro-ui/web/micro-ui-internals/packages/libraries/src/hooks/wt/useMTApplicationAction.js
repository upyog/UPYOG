import { useMutation } from "react-query";
import ApplicationUpdateActionsMT from "../../services/molecules/WT/ApplicationUpdateActionMT";

// For Mobile Toilet Application Actions
const useMTApplicationAction = (tenantId) => {
  return useMutation((applicationData) => ApplicationUpdateActionsMT(applicationData, tenantId));
};

export default useMTApplicationAction;
