import { useMutation } from "react-query";
import ApplicationUpdateActionsEW from "../../services/molecules/EW/ApplicationUpdateActionsEW"

const useEWApplicationAction = (tenantId) => {
  return useMutation((applicationData) => ApplicationUpdateActionsEW(applicationData, tenantId));
};

export default useEWApplicationAction;
