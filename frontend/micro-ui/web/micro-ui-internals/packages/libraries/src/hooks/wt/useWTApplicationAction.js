import { useMutation } from "react-query";

import ApplicationUpdateActionsWT from "../../services/molecules/WT/ApplicationUpdateActionsWT";

const useWTApplicationAction = (tenantId) => {
  
  return useMutation((applicationData) => ApplicationUpdateActionsWT(applicationData, tenantId));
};

export default useWTApplicationAction;
